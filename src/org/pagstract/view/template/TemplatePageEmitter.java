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
import org.pagstract.view.namespace.NameResolver;
import org.pagstract.view.namespace.Namespace;
import org.pagstract.view.namespace.NamingContext;
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
import org.pagstract.view.template.parser.ast.Visitor;

/**
 * A Visitor that emits Template Pages into a Device.
 */
public class TemplatePageEmitter implements Visitor {
    protected final NameResolver     _nameResolver;
    protected final TemplateResolver _templateResolver;
    protected final ActionUrlProvider _urlProvider;
    
    protected Device _out; //not final, since JSTemplatePageEmitter replaces it
    protected final String _parentResource;

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
        this(resourceName, out, nameResolver, templateResolver, null);
    }
    
    public TemplatePageEmitter(String resourceName,
                               Device out, 
                               NameResolver nameResolver,
                               TemplateResolver templateResolver,
                               ActionUrlProvider urlProvider) 
    {
        _out = out;
        _nameResolver = nameResolver;
        _templateResolver = templateResolver;
        _parentResource = resourceName;
        _urlProvider = urlProvider;
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
            Object value = _nameResolver.resolveName(modelName);
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

        TemplateNode resourceNode;
        try {
            resourceNode = 
                _templateResolver.resolveRelativeTemplate(_parentResource,
                                                          resourceName);
            resourceNode.accept(this);
        }
        catch (InvocationTargetException ie) {
            _out.print("<!--" + ie.getMessage() + ": "
                       + ie.getTargetException() + "-->");
        }
        catch (Exception e) {
            _out.print("<!-- " + e.getMessage() + " -->");
        }
    }

    public void visit(ConstantNode node) throws Exception {
        _out.write(node.getContentBuffer());
    }

    public void visit(IteratorNode node) throws Exception {
        NamingContext ctxt = _nameResolver.resolveNameContext(node.getModelName());
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
        
        boolean hasAnyContent = nsIt.hasNext();

        if (hasAnyContent && node.getHeader() != null) {
            node.getHeader().accept(this);
        }

        int i = 0;
        for (int index=0; nsIt.hasNext(); ++index) {
            Namespace ns = (Namespace) nsIt.next();
            
            try {
                _nameResolver.pushNamespace(ns);
                _nameResolver.pushIteratorIndex(index);
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
    }

    public void visit(ValueNode node) throws Exception {
        ComponentRenderer renderer = new ValueRenderer();
        Object value = _nameResolver.resolveName(node.getModelName());
        renderer.render(this, node, value, _out);
    }

    public void visit(BeanNode node) throws Exception {
        NamingContext ctxt = _nameResolver.resolveNameContext(node.getModelName());
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

    public void visit(AnchorNode node) throws Exception {
        ComponentRenderer renderer = new AnchorRenderer(_urlProvider);
        Object value = _nameResolver.resolveName(node.getModelName());
        renderer.render(this, node, value, _out);
    }

    public void visit(FormNode node) throws Exception {
        ComponentRenderer renderer = new FormRenderer(_urlProvider);
        Object value = _nameResolver.resolveName(node.getModelName());
        renderer.render(this, node, value, _out);
    }

    public void visit(InputFieldNode node) throws Exception {
        String suffix = _nameResolver.determineInputfieldSuffix();
        ComponentRenderer renderer = new InputFieldRenderer(suffix);
        Object value = _nameResolver.resolveName(node.getModelName());
        renderer.render(this, node, value, _out);
    }

    public void visit(SelectFieldNode node) throws Exception {
        String suffix = _nameResolver.determineInputfieldSuffix();
        ComponentRenderer renderer = new SelectFieldRenderer(suffix);
        Object value = _nameResolver.resolveName(node.getModelName());
        renderer.render(this, node, value, _out);
    }

    public void visit(SwitchNode node) throws Exception {
        Object value = _nameResolver.resolveName(node.getModelName());

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
