/*
 * $Id$
 * (c) Copyright 2003 pagstract development team.
 *
 * This file is part of pagstract (http://www.pagstract.org/).
 *
 * Pagstract is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 *
 * Please see COPYING for the complete licence.
 */
package org.pagstract.view.template;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;

import org.pagstract.io.Device;
import org.pagstract.model.SingleValueModel;
import org.pagstract.model.ActionModel;
import org.pagstract.model.ComponentModel;
import org.pagstract.view.ObjectRenderer;
import org.pagstract.view.namespace.NameResolver;
import org.pagstract.view.namespace.Namespace;
import org.pagstract.view.namespace.NamingContext;
import org.pagstract.view.template.parser.ast.NamedTemplateNode;
import org.pagstract.view.template.parser.ast.AnchorNode;
import org.pagstract.view.template.parser.ast.BeanNode;
import org.pagstract.view.template.parser.ast.ConstantNode;
import org.pagstract.view.template.parser.ast.FormNode;
import org.pagstract.view.template.parser.ast.InputFieldNode;
import org.pagstract.view.template.parser.ast.IteratorNode;
import org.pagstract.view.template.parser.ast.NodeSequence;
import org.pagstract.view.template.parser.ast.SelectFieldNode;
import org.pagstract.view.template.parser.ast.SwitchNode;
import org.pagstract.view.template.parser.ast.TemplateNode;
import org.pagstract.view.template.parser.ast.TileNode;
import org.pagstract.view.template.parser.ast.ValueNode;
import org.pagstract.view.template.parser.ast.IfVisibleNode;
import org.pagstract.view.template.parser.ast.ResourceNode;
import org.pagstract.view.template.parser.ast.MessageNode;
import org.pagstract.view.template.parser.ast.DebugNode;
import org.pagstract.view.template.parser.ast.Visitor;
import org.pagstract.view.template.parser.scanner.FilePosition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Visitor that emits Template Pages into a Device.
 */
public class TemplatePageEmitter implements Visitor {
    private final static int RESOURCE_REMOVE_FIRST = "resource:/".length();
    private static final Log _log = LogFactory.getLog(TemplatePageEmitter.class);
    protected final ResourceResolver _resourceResolver;
    protected final NameResolver     _nameResolver;
    protected final TemplateResolver _templateResolver;
    protected final ActionUrlProvider _urlProvider;
    protected final RendererResolver  _rendererResolver;

    protected Device _out; //not final, since JSTemplatePageEmitter replaces it
    protected final String _parentResource;
    protected final ResourceResolver _messageBundle;

    // FIXME: think about this; should this be handled here ?
    /**
     * form parameters, that are emitted within the current form as
     * input field.
     */
    protected Set _currentFormParameters;
    protected FormNode _currentForm;
    
    public TemplatePageEmitter(String resourceName,
                               Device out, 
                               NameResolver nameResolver,
                               TemplateResolver templateResolver) {
        this(resourceName, out, nameResolver, templateResolver, null, null,
             new RendererResolver(), null);
    }
    
    public TemplatePageEmitter(String resourceName,
                               Device out, 
                               NameResolver nameResolver,
                               TemplateResolver templateResolver,
                               ActionUrlProvider urlProvider,
                               ResourceResolver resourceResolver,
                               RendererResolver rendererResolver,
                               ResourceResolver messageBundle)
    {
        _out = out;
        _nameResolver = nameResolver;
        _templateResolver = templateResolver;
        _resourceResolver = resourceResolver;
        _parentResource = resourceName;
        _urlProvider = urlProvider;
        _rendererResolver = rendererResolver;
        _messageBundle = messageBundle;
    }

    void setFormParameterCollector(Set s, FormNode formNode) {
        if (s != null && _currentFormParameters != null) {
            throw new IllegalArgumentException("Nested Forms! "
                                               + "attempt to nest form " 
                                               + formNode.getModelName()
                                               + "@" + formNode.getPosition()
                                               + " in "
                                               + _currentForm.getModelName()
                                               + _currentForm.getPosition());
        }
        _currentFormParameters = s;
        _currentForm = formNode;
    }
    
    void addUsedInputFieldName(String inputName) {
        if (_currentFormParameters != null) {
            _currentFormParameters.add(inputName);
        }
    }

    public void visit(NodeSequence node) throws Exception {
        Iterator it = node.getElements();
        while (it.hasNext()) {
            TemplateNode subnode = (TemplateNode) it.next();
            subnode.accept(this);
        }
    }
    
    public void visit(TileNode node) throws Exception {
        String resourceName = null;
        
        /*
         * resolve dynamic name...
         */
        final String modelName = node.getModelName();
        if (modelName != null) {
            Object value = resolveNamedObject(node);
            if (value != null) {
                resourceName = value.toString();
            }
        }
        
        /*
         * no dynamic name: use static fallback..
         */ 
        if (resourceName == null || resourceName.length() == 0) {
            resourceName = node.getFileName();
        }

        if (resourceName == null || resourceName.length() == 0) {
            return;
        }

        if (resourceName.startsWith("resource://")) {
            resourceName = resolveResource(resourceName.substring( RESOURCE_REMOVE_FIRST ), node.getPosition());
        }

        TemplateNode resourceNode;
        try {
            resourceNode = 
                _templateResolver.resolveRelativeTemplate(_parentResource,
                                                          resourceName);
            resourceNode.accept(this);
        }
        catch (InvocationTargetException ie) {
            writeHiddenMessage(ie.getMessage() + ": "
                               + ie.getTargetException());
            _log.error("Exception rendering", ie);
        }
        catch (Exception e) {
            writeHiddenMessage(e.getMessage());
            _log.error("Exception rendering", e);
        }
    }

    public void visit(ConstantNode node) throws Exception {
        _out.write(node.getContentBuffer());
    }

    public void visit(IteratorNode node) throws Exception {
        NamingContext ctxt = resolveNameCtxt(node);
        if (ctxt == null) {
            return;
        }

        Namespace ctxtNs = ctxt.getNamespace();
        if (!ctxtNs.isIteratableObject(ctxt.getName())) {
            throw new IOException("Not iteratable: " + node.getModelName()
                                  + " " 
                                  + ctxtNs.getNamedObject(ctxt.getName()));
        }
        
        String subName = ctxt.getName();
        Iterator/*<Namespace>*/ nsIt = ctxtNs.getNamespaceIterator( subName );

        if (nsIt == null) {
            return;
        }

        TemplateNode content = node.getContent();
        TemplateNode separator = node.getSeparator();

        if (content == null) {
            return;
        }
        
        Integer startPos = node.getStartPos();
        int start = (startPos != null) ? startPos.intValue() : 0;
        // vorausnudeln .. unhübsch..
        for (int consume = 0; consume < start && nsIt.hasNext(); ++consume) {
            nsIt.next();
        }

        boolean hasAnyContent = nsIt.hasNext();

        if (hasAnyContent && node.getHeader() != null) {
            node.getHeader().accept(this);
        }

        int max = ((node.getCount() != null)
                   ? node.getCount().intValue() + start
                   : -1);
        for (int index=start; nsIt.hasNext() && (max < 0 || index < max); ++index) {
            Namespace ns = (Namespace) nsIt.next();

            try {
                _nameResolver.pushNamespace(ns);
                _nameResolver.pushIteratorIndex(index);
                /*
                 * entweder direkt content oder eine contentgroup.
                 */
                content.accept(this);
            }
            finally {
                _nameResolver.popNamespace();
                _nameResolver.popIteratorIndex();
            }
            if (separator != null && nsIt.hasNext()) {
                separator.accept(this);
            }
        }

        if (hasAnyContent && node.getFooter() != null) {
            node.getFooter().accept(this);
        }

        if (!hasAnyContent && node.getEmptyContent() != null) {
            node.getEmptyContent().accept(this);
        }
    }

    public void visit(ValueNode node) throws Exception {
        ComponentRenderer renderer = new ValueRenderer(_rendererResolver);
        Object value = resolveNamedObject(node);
        renderer.render(this, node, value, _out);
    }

    public void visit(ResourceNode node) throws Exception {
        _out.print(resolveResource(node.getResourceValue(),
                                   node.getPosition()));
    }

    public void visit(MessageNode node) throws Exception {
        if (_messageBundle == null) {
            writeHiddenMessage("No message bundle given");
            return;
        }
        Object msg = _messageBundle.resolveResource(node.getResourceValue());
        if (msg != null) {
            _out.print(msg);
        }
        else {
            writeHiddenMessage("No resource found for " + node.getResourceValue());
        }
    }
    
    public void visit(BeanNode node) throws Exception {
        NamingContext ctxt = resolveNameCtxt(node);
        if (ctxt == null) {
            return;
        }

        if (ctxt.getNamespace().getNamedObject(ctxt.getName()) == null) {
            if (_log.isDebugEnabled()) {
                writeHiddenMessage("not displayed null-bean '" 
                                   + node.getModelName() + "'");
            }
            return;
        }

        _nameResolver.pushNamespace(ctxt.getNamespace().getSubNamespace(ctxt.getName()));
        
        try {
            TemplateNode content = node.getTemplateContent();
            if (content != null) {
                content.accept(this);
            }
        }
        finally {
            _nameResolver.popNamespace();
        }
    }

    public void visit(IfVisibleNode node) throws Exception {
        final String modelName = node.getModelName();
        final Object value = resolveNamedObject(node);

        boolean visible = true;
        
        if (value == null) {
            if (_log.isDebugEnabled()) {
                writeHiddenMessage("if-visible: empty " + modelName);
            }
            visible = false;
        }
        else if (value instanceof ComponentModel) {
            ComponentModel model = (ComponentModel) value;
            if (!model.isVisible()) {
                if (_log.isDebugEnabled()) {
                    writeHiddenMessage("if-visible: invisible component " + modelName);
                }
                visible = false;
            }
        }
        else if (value.toString().length() == 0) {
            // leerer string ist ja in der Form auch nicht sichtbar.
            if (_log.isDebugEnabled()) {
                writeHiddenMessage("if-visible: 0 length " + modelName);
            }
            visible = false;
        }
        
        if (node.isPositiveBooleanCondition() == visible) {
            node.getTemplateContent().accept(this);
        }
    }

    public void visit(AnchorNode node) throws Exception {
        ComponentRenderer renderer = new AnchorRenderer(_urlProvider);
        Object value = resolveNamedObject(node);
        renderer.render(this, node, value, _out);
    }

    public void visit(FormNode node) throws Exception {
        ComponentRenderer renderer = new FormRenderer(_urlProvider);
        Object value = resolveNamedObject(node);
        renderer.render(this, node, value, _out);
    }

    public void visit(InputFieldNode node) throws Exception {
        String suffix = _nameResolver.determineInputfieldSuffix();
        ComponentRenderer renderer = new InputFieldRenderer(suffix);
        Object value = resolveNamedObject(node);
        renderer.render(this, node, value, _out);
    }

    public void visit(SelectFieldNode node) throws Exception {
        String suffix = _nameResolver.determineInputfieldSuffix();
        ComponentRenderer renderer = new SelectFieldRenderer(suffix);
        Object value = resolveNamedObject(node);
        renderer.render(this, node, value, _out);
    }

    public void visit(SwitchNode node) throws Exception {
        Object value = resolveNamedObject(node);

        String matchValue = null;
        if ( value instanceof SingleValueModel ) {
            matchValue = ((SingleValueModel)value).getValue();
        }
        else if (value != null) {
            matchValue = value.toString();
        }
	
        TemplateNode caseNode = node.getNamedContent(matchValue);
        if (caseNode != null) {
            caseNode.accept(this);
        }
    }

    public void visit(DebugNode node) throws Exception {
        NamingContext ctxt = resolveNameCtxt(node);
        _out.print("<div class='pagstract-debug'>");
        printNamespace(ctxt.getNamespace(), ctxt.getName());
        _out.print("</div>");
    }

    /** Blödes hack debug output gelöt */
    private void printNamespace(Namespace ns, String name)
        throws IOException 
    {
        _out.print("<table style='border:1px solid;'>");
        _out.print("<tr><td colspan='2'>" + name);

        if (ns.isIteratableObject(name)) {
            _out.print(" ( pma:list )");
        }
        Namespace subNs = null;
        if (ns.isIteratableObject(name)) {
            Iterator/*<Namespace>*/ nsit = ns.getNamespaceIterator(name);
            if (nsit != null && nsit.hasNext()) {
                subNs = (Namespace) nsit.next();
            }
            else {
                _out.print("..empty");
            }
        }
        else if (ns.isNamespace(name)) {
            subNs = ns.getSubNamespace(name);
        }
        
        _out.print("</td></tr>");

        if (subNs != null) {
            Set names = subNs.availableNames();
            Iterator it = names.iterator();
            while (it.hasNext()) {
                final String currentName = (String) it.next();
                _out.print("\n<tr><td>&nbsp;&nbsp;</td><td>");
                final Object obj = subNs.getNamedObject(currentName);
                Class renderableClass = subNs.getNamedObjectType(currentName);
                if (subNs.isNamespace(currentName) 
                    && (!_rendererResolver.hasRendererFor(renderableClass))) {
                    printNamespace(subNs, currentName);
                }
                else {
                    _out.print(currentName);
                    try {
                        if (obj instanceof ActionModel) {
                            String url = AnchorRenderer.buildActionUrl((ActionModel) obj, _urlProvider);
                            _out.print(" = <em><a href=\"" + url + "\">" + url + "</a></em>");
                        }
                        else if (obj instanceof SingleValueModel) {
                            _out.print(" = [single-value-model] <em>");
                            _out.print(((SingleValueModel) obj).getValue());
                            _out.print("</em>");
                        }
                        else if (obj != null) {
                            _out.print(" = <em>");
                            ObjectRenderer renderer = _rendererResolver.findRendererFor(renderableClass);
                            _out.print(renderer.renderObject(currentName, null, obj));
                            _out.print("</em>");
                            if (renderer.getClass() != SimpleStringRenderer.class) {
                                _out.print(" <font size='-1'>(" + renderer.getClass() + ")</font>");
                            }
                        }
                        else {
                            _out.print(" = NULL");
                        }
                    }
                    catch (Exception e) {
                        // egal.
                    }
                }
                _out.print("</td></tr>");
            }
        }
        _out.print("</table>");
    }

    private void writeHiddenMessage(String msg) throws IOException {
        /*
         * should not be displayed, otherwise text-templates will be doomed.
         */
        if (System.getProperty("org.pagstract.view.template.DEBUG") != null) {
            _out.print("<!-- " + msg + " -->");
        }
    }

    private final String resolveResource(String resource, FilePosition pos) {
        if (_resourceResolver == null) {
            return resource;
        }
        try {
            return _resourceResolver.resolveResource(resource);
        }
        catch (Exception e) {
            _log.error("problem resolving resource '" + resource + "' "
                       + "at " + pos, e);
        }
        return "";
    }

    private final Object resolveNamedObject(NamedTemplateNode node) 
        throws IOException 
    {
        try {
            return _nameResolver.resolveName(node.getModelName());
        }
        catch (Exception e) {
            writeHiddenMessage("problem resolving '" +node.getModelName()+"'");
            _log.error("at " + node.getPosition(), e);
        }
        return null;
    }

    
    private final NamingContext resolveNameCtxt(NamedTemplateNode node) 
        throws IOException 
    {
        try {
            return _nameResolver.resolveNameContext(node.getModelName());
        }
        catch (Exception e) {
            writeHiddenMessage("problem resolving '" +node.getModelName()+"'");
            _log.error("at " + node.getPosition(), e);
        }
        return null;
    }

}

/* Emacs: 
 * Local variables:
 * c-basic-offset: 4
 * tab-width: 8
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml compile"
 * End:
 * vi:set tabstop=8 shiftwidth=4 nowrap: 
 */
