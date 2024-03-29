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
import java.util.Iterator;
import java.util.Map;

import org.pagstract.io.Device;
import org.pagstract.io.StringBufferDevice;
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
import org.pagstract.view.template.parser.ast.ResourceNode;
import org.pagstract.view.template.parser.ast.MessageNode;
import org.pagstract.view.template.parser.ast.ValueNode;

/**
 * A TemplatePage Emitter that pretty much behaves like the normal
 * TemplatePageEmitter, but uses the JavaScript, generated by the
 * {@link JavaScriptGenerator} functions if necessary.
 */
public class JSTemplatePageEmitter extends TemplatePageEmitter {
    private final Map/*<TemplateNode,String>*/ _nodeFunctions;
    private final StringBuffer _functionCode;
    
    private final Device _origDevice;
    private int _inFunctionParameter;
    private boolean _functionsEmitted;
    private int _nestDepth;

    public JSTemplatePageEmitter(String resourceName,
                                 Device out, 
                                 NameResolver nameResolver,
                                 TemplateResolver templateResolver,
                                 StringBuffer functionCode,
                                 Map nodeFunctions, 
                                 ActionUrlProvider urlProvider,
                                 ResourceResolver resourceResolver,
                                 RendererResolver rendererResolver,
                                 ResourceResolver messageBundle)
    {
        // consider aggregation..
        super(resourceName, out, nameResolver, templateResolver,
              urlProvider, resourceResolver, rendererResolver, messageBundle);
        _origDevice = out;
        _functionCode = functionCode;
        _nodeFunctions = nodeFunctions;
        _functionsEmitted = false;
        _inFunctionParameter = -1;
    }

    public void visit(NodeSequence node) throws Exception {
        Iterator it = node.getElements();
        while (it.hasNext()) {
            TemplateNode subnode = (TemplateNode) it.next();
            subnode.accept(this);
        }
    }

    public void visit(ConstantNode node) throws Exception {
        if (_nestDepth == 0 && _inFunctionParameter >= 0) {
            if (_out == null) 
                return;
            
            if (_inFunctionParameter > 0) {
                _origDevice.print(",");
            }
            ++_inFunctionParameter;
            String param = _out.toString();
            _origDevice.print("\"");
            _origDevice.print(JavaScriptGenerator.quote(param));
            _origDevice.print("\"");
            _out = null;
        }
        else {
            _out.write(node.getContentBuffer());
        }
    }

    public void visit(IteratorNode node) throws Exception {
        String functionName = null;

        if (!(_inFunctionParameter >= 0) && _nodeFunctions.containsKey(node)) {
            _out.print("<script language='JavaScript'>");
            if (!_functionsEmitted) {
                _out.print("\n");
                _out.print(_functionCode.toString());
                _functionsEmitted = true;
            }
            functionName = (String) _nodeFunctions.get(node);
        }

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

        Integer startPos = node.getStartPos();
        int start = (startPos != null) ? startPos.intValue() : 0;
        // vorausnudeln .. unh�bsch..
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

                if (functionName != null) {
                    _out.print(functionName).print("(");
                    _out = null;
                    _inFunctionParameter = 0;
                    _nestDepth = 0;
                }

                content.accept(this);

                if (functionName != null) {
                    if (_out != null) {
                        String param = _out.toString();
                        if (_inFunctionParameter > 0) {
                            _origDevice.print(",");
                        }
                        _origDevice.print("\"");
                        _origDevice.print(JavaScriptGenerator.quote(param));
                        _origDevice.print("\"");
                    }
                    _out = _origDevice;
                    _out.print(");\n");
                }

            }
            finally {
                _nameResolver.popNamespace();
                _nameResolver.popIteratorIndex();
            }
            if (separator != null && nsIt.hasNext() && (max < 0 || index+1 < max)) {
                separator.accept(this);
            }
        }
        if (functionName != null) {
            _out.print("</script>");
            _inFunctionParameter = -1;
        }

        if (hasAnyContent && node.getFooter() != null) {
            node.getFooter().accept(this);
        }
    }

    public void visit(ValueNode node) throws Exception {
        if (_inFunctionParameter >= 0 && _out == null) {
            _out = new StringBufferDevice();
        }
        ++_nestDepth;
        super.visit(node);
        --_nestDepth;
    }

    public void visit(ResourceNode node) throws Exception {
        if (_inFunctionParameter >= 0 && _out == null) {
            _out = new StringBufferDevice();
        }
        ++_nestDepth;
        super.visit(node);
        --_nestDepth;
    }

    public void visit(MessageNode node) throws Exception {
        if (_inFunctionParameter >= 0 && _out == null) {
            _out = new StringBufferDevice();
        }
        ++_nestDepth;
        super.visit(node);
        --_nestDepth;
    }

    public void visit(BeanNode node) throws Exception {
        super.visit(node);
    }

    public void visit(AnchorNode node) throws Exception {
        if (_inFunctionParameter >= 0 && _out == null) {
            _out = new StringBufferDevice();
        }
        ++_nestDepth;
        super.visit(node);
        --_nestDepth;
    }

    public void visit(FormNode node) throws Exception {
        if (_inFunctionParameter >= 0 && _out == null) {
            _out = new StringBufferDevice();
        }
        ++_nestDepth;
        super.visit(node);
        --_nestDepth;
    }

    public void visit(InputFieldNode node) throws Exception {
        if (_inFunctionParameter >= 0 && _out == null) {
            _out = new StringBufferDevice();
        }
        ++_nestDepth;
        super.visit(node);
        --_nestDepth;
    }

    public void visit(SelectFieldNode node) throws Exception {
        if (_inFunctionParameter >= 0 && _out == null) {
            _out = new StringBufferDevice();
        }
        ++_nestDepth;
        super.visit(node);
        --_nestDepth;
    }

    public void visit(SwitchNode node) throws Exception {
        if (_inFunctionParameter >= 0 && _out == null) {
            _out = new StringBufferDevice();
        }
        ++_nestDepth;
        super.visit(node);
        --_nestDepth;
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
