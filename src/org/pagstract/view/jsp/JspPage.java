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

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pagstract.Page;
import org.pagstract.PageModel;
import org.pagstract.view.namespace.NameResolver;
import org.pagstract.view.namespace.Namespace;

/**
 * A Page that uses JSP to output stuff.
 */
class JspPage implements Page {
    private final PageModel _model;
    private final String _jspPath;
    private final HttpServletRequest _request;
    private final HttpServletResponse _response;

    JspPage(Namespace rootNamespace, PageModel model,
            String jspPath,
            HttpServletRequest request, HttpServletResponse response) {
        _model = model;
        request.setAttribute(PagstractTag.NAMERESOLVER, 
                             new NameResolver(rootNamespace));
        _request = request;
        _response = response;
        _jspPath = jspPath;
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
        RequestDispatcher dispatcher = _request.getRequestDispatcher(_jspPath);
        try {
            dispatcher.forward(_request,_response);
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (ServletException e) {
            throw new IOException(e.getMessage());
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
