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
import java.util.HashMap;
import java.util.Map;

import org.pagstract.Page;
import org.pagstract.PageModel;
import org.pagstract.io.Device;
import org.pagstract.view.namespace.NameResolver;
import org.pagstract.view.namespace.Namespace;
import org.pagstract.view.template.parser.ast.TemplateNode;
import org.pagstract.view.template.parser.ast.Visitor;

/**
 * 
 */
class TemplatePage implements Page {
    //private final Visitor _templatePageEmitter1;
    //private final Visitor _templatePageEmitter2;
    private final Visitor _templatePageEmitter;
    private final PageModel _model;
    private final TemplateNode _rootNode;
    private final Device _out;

    TemplatePage(String resourceName,
                 Namespace rootNamespace, TemplateResolver templateResolver,
                 PageModel model,
                 TemplateNode rootNode,
                 Device out, ActionUrlProvider urlProvider,
                 ResourceResolver resourceResolver) 
    {
        NameResolver resolver = new NameResolver(rootNamespace);
        //_templatePageEmitter = new TemplatePageEmitter(out, resolver);
        StringBuffer code = new StringBuffer();
        Map functionMappings = new HashMap();
        _templatePageEmitter= new TemplatePageEmitter(resourceName,out,
                                                      resolver,
                                                      templateResolver,
                                                      urlProvider,
                                                      resourceResolver);
        /*
         FIXME: this must be optimized; the JS does not change.
        _templatePageEmitter1= new JavaScriptGenerator(code, functionMappings);
        _templatePageEmitter2 = new JSTemplatePageEmitter(resourceName, out, 
                                                          resolver, 
                                                          templateResolver,
                                                          code, 
                                                          functionMappings,
                                                          urlProvider);
        */
        _out = out;
        _rootNode = rootNode;
        _model = model;
    }

    /**
     * returns the Model the data to be rendered can be written to. Setting
     * Fields in the Model stores data in this page.
     */
    public PageModel getModel() {
        return _model;
    }

    /**
     * Render the page to the configured output. The implementation of
     * this Page is already configured for the sink to write to.
     */
    public void render() throws IOException {
        try {
            //_rootNode.accept(_templatePageEmitter1);
            //_rootNode.accept(_templatePageEmitter2);
            _rootNode.accept(_templatePageEmitter);
            _out.flush();
        }
        catch (IOException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RenderException(e);
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
