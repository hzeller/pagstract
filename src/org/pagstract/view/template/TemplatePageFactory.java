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

import java.util.HashMap;
import java.util.Map;

import org.pagstract.Page;
import org.pagstract.PageFactory;
import org.pagstract.PageModel;
import org.pagstract.io.Device;
import org.pagstract.view.DynamicMapPageModelProxy;
import org.pagstract.view.namespace.MapNamespace;
import org.pagstract.view.namespace.Namespace;
import org.pagstract.view.template.parser.ast.TemplateNode;

/**
 * A Factory for Template-backed pages. FIXME.
 */
public class TemplatePageFactory implements PageFactory {
    private static TemplateResolver cache = null;

    protected final Device _out;
    protected final String _basePath;
    protected final TemplateResolver _resolver;
    protected final ActionUrlProvider _urlProvider; 
    
    /**
     * @deprecated call with explicit resolver instead.
     */
    public TemplatePageFactory(Device out, String basePath) {
        if (cache == null) {
            cache = new FileTemplateResolver();
        }
        _out = out;
        _basePath = basePath;
        _resolver = cache;
        _urlProvider = null;
    }

    public TemplatePageFactory(Device out, String basePath,
                               TemplateResolver resolver) {
        this(out, basePath, resolver, null);
    }
    
    public TemplatePageFactory(Device out, String basePath, 
                               TemplateResolver resolver,
                               ActionUrlProvider urlProvider)
    {
        _out = out;
        _basePath = basePath;
        _resolver = resolver;
        _urlProvider = urlProvider;
    }

    /**
     * returns the device this page factory writes to.
     */
    public Device getOutputDevice() {
        return _out;
    }

    public Page createPageFor(Class pageModelClass) throws Exception {
        Map valueMap = new HashMap();
        PageModel model = (DynamicMapPageModelProxy
                           .createInstance(pageModelClass, valueMap));
        Namespace rootNamespace = new MapNamespace(valueMap);
        String filename = getTemplateNameFor(pageModelClass);
        TemplateNode rootNode = _resolver.resolveTemplate(filename);
        return new TemplatePage(filename, rootNamespace, _resolver, model, 
                                rootNode, _out, _urlProvider);
    }
    
    /**
     * returns the name of the JSP-Page, derived from the given class. 
     * By default, the name is determined by the basePath + the name of
     * the class + ".jsp".
     * Override this, if you have a more detailed way to determine the name.
     */
    protected String getTemplateNameFor(Class c) {
        String name = c.getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        return _basePath + "/" + name + ".html";
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
