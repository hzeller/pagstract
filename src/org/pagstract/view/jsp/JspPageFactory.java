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
package org.pagstract.view.jsp;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pagstract.Page;
import org.pagstract.PageFactory;
import org.pagstract.PageModel;
import org.pagstract.view.DynamicMapPageModelProxy;
import org.pagstract.view.namespace.MapNamespace;
import org.pagstract.view.namespace.Namespace;

/**
 * A Factory for JSP-backed pages.
 */
public class JspPageFactory implements PageFactory {
    protected final String _basePath;
    protected final HttpServletRequest _request;
    protected final HttpServletResponse _response;

    public JspPageFactory(HttpServletRequest request,
                          HttpServletResponse response,
                          String basePath) {
        _request = request;
        _response = response;
        _basePath = basePath;
    }

    public Page createPageFor(Class pageModelClass) {
        Map valueMap = new HashMap();
        PageModel model = (DynamicMapPageModelProxy
                           .createInstance(pageModelClass, valueMap));
        Namespace rootNamespace = new MapNamespace(valueMap);
        return new JspPage(rootNamespace, model, 
                           getJspNameFor(pageModelClass),
                           _request, _response);
    }
    
    /**
     * returns the name of the JSP-Page, derived from the given class. 
     * By default, the name is determined by the basePath + the name of
     * the class + ".jsp".
     * Override this, if you have a more detailed way to determine the name.
     */
    protected String getJspNameFor(Class c) {
        String name = c.getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        return _basePath + "/" + name + ".jsp";
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
