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
    protected final ResourceResolver _templatePathResolver;
    protected final TemplateResolver _resolver;
    protected final ActionUrlProvider _urlProvider; 
    protected final ResourceResolver _resourceResolver;

    /**
     * @deprecated call with explicit resolver instead.
     */
    public TemplatePageFactory(Device out, String basePath) {
        if (cache == null) {
            cache = new FileTemplateResolver();
        }
        _out = out;
        _templatePathResolver = new PrefixResourceResolver(basePath);
        _resolver = cache;
        _urlProvider = null;
        _resourceResolver = null;
    }

    public TemplatePageFactory(Device out, String basePath,
                               TemplateResolver resolver) {
        this(out, basePath, resolver, null, null);
    }

    public TemplatePageFactory(Device out, String basePath, 
                               TemplateResolver resolver,
                               ActionUrlProvider urlProvider) {
        this(out, basePath, resolver, urlProvider, null);
    }
    
    /**
     * Create a template page factory that creates pages that
     * write to device 'out', find their templates relative to
     * 'basePath' and resolve them via the given given TemplateResolver
     * 'resolver'.
     * URLs found in the page are treated by the 'urlProvider' and
     * with 'resource://' referenced resources are resolved by the
     * 'resourceResolver'.
     */
    public TemplatePageFactory(Device out, String basePath, 
                               TemplateResolver resolver,
                               ActionUrlProvider urlProvider,
                               ResourceResolver resourceResolver) {
        this(out, new PrefixResourceResolver(basePath),
             resolver, urlProvider, resourceResolver);
    }

    /**
     * Create a template page factory that creates pages that
     * write to device 'out', and finds the filesystem path of the
     * templates with the 'templatePathResolver'. This resolver gets
     * an absolute filename derived from the name of the page model: the
     * non-qualified class name name is prefixed by '/' and sufficed by
     * '.html'. A class foo.bar.MyStandardPage would translate to a
     * template path '/MyStandardPage.html'. That in turn is resolved
     * by the templatePathResolver.
     * 
     * The resulting template path is resolved to a template
     * via the given given TemplateResolver 'resolver'.
     *
     * URLs found in the page are treated by the 'urlProvider' and
     * with 'resource://' referenced resources are resolved by the
     * 'resourceResolver'.
     */
    public TemplatePageFactory(Device out, 
                               ResourceResolver templatePathResolver,
                               TemplateResolver resolver,
                               ActionUrlProvider urlProvider,
                               ResourceResolver resourceResolver)
        
    {
        _out = out;
        _templatePathResolver = templatePathResolver;
        _resolver = resolver;
        _urlProvider = urlProvider;
        _resourceResolver = resourceResolver;
    }
    
    /**
     * returns the device this page factory writes to.
     */
    public Device getOutputDevice() {
        return _out;
    }

    /**
     * creates a Page that does have the same content but writes
     * to another output-Device
     */
    public Page clonePageContent(Page otherPage) throws Exception {
        TemplatePage page = (TemplatePage) otherPage;
        return page.createCopy(_resolver, _out, _urlProvider,
                               _resourceResolver);
    }

    public Page createPageFor(Class pageModelClass) throws Exception {
        Map valueMap = new HashMap();
        PageModel model = (DynamicMapPageModelProxy
                           .createInstance(pageModelClass, valueMap));
        Namespace rootNamespace = new MapNamespace(valueMap);
        String filename = getTemplateNameFor(pageModelClass);
        TemplateNode rootNode = _resolver.resolveTemplate(filename);
        return new TemplatePage(filename, rootNamespace, _resolver, model, 
                                rootNode, _out, _urlProvider, 
                                _resourceResolver);
    }
    
    /**
     * returns the name of the template-Page, derived from the given class. 
     * By default, the name is determined by the basePath + the name of
     * the class + ".html".
     * Override this, if you have a more detailed way to determine the name.
     */
    protected String getTemplateNameFor(Class c) throws Exception {
        String name = c.getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        return _templatePathResolver.resolveResource("/" + name + ".html");
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
