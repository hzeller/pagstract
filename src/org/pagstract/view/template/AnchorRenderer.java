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
import java.net.URLEncoder;
import java.util.Iterator;

import org.pagstract.io.Device;
import org.pagstract.model.ActionModel;
import org.pagstract.model.ComponentModel;
import org.pagstract.model.SingleValueModel;
import org.pagstract.view.template.parser.ast.AnchorNode;
import org.pagstract.view.template.parser.ast.TemplateNode;
import org.pagstract.view.template.parser.ast.Visitor;
import org.pagstract.view.template.parser.scanner.TemplateToken;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Component Renderer is used in the TemplatePageEmitter to write
 * out components.
 */
public class AnchorRenderer implements ComponentRenderer {
    private static final Log _log = LogFactory.getLog(AnchorRenderer.class);

    private static final byte[] s_a_href     = "<a href=\"".getBytes();
    private static final byte[] s_questmark  = "?".getBytes();
    private static final byte[] s_equals     = "=".getBytes();
    private static final byte[] s_ampersand  = "&amp;".getBytes();
    private static final byte[] s_closebracket = ">".getBytes();
    private static final byte[] s_end_a_tag  = "</a>".getBytes();
    private ActionUrlProvider _urlProvider;

    public AnchorRenderer(ActionUrlProvider urlProvider) {
        _urlProvider = urlProvider;
    }
    
    public void render(Visitor renderVisitor,
                       TemplateNode n, 
                       Object value, Device out) 
        throws IOException 
    {
        AnchorNode node = (AnchorNode) n;
        TemplateToken origTag = node.getTemplateToken();

        TemplateNode templateContent = node.getTemplateContent();
        if (templateContent == null) {
            return;
        }
        
        boolean enabled = (value != null);

        ComponentModel compModel = null;
        if (value instanceof ComponentModel) {
            compModel = (ComponentModel) value;
            if (!compModel.isVisible()) {
                return;
            }
            enabled = compModel.isEnabled();
        }
        
        String actionUrl = null;
        
        /*
         * If this is an action model: build the URL right
         * from it.
         */
        if (value instanceof ActionModel) {
            ActionModel action = (ActionModel) value;
            StringBuffer result = new StringBuffer();
            Iterator it= action.getParameterNames();
            String url = action.getUrl();
            if (_urlProvider != null) {
                url = _urlProvider.resolveUrl(url);
            }
            boolean requireQuestMark = true;
            if (url != null) {
                result.append(url);
                requireQuestMark = (url.indexOf('?') < 0);
            }
            if( it.hasNext()) {
                result.append(requireQuestMark ? "?" : "&amp;");
            }
            while( it.hasNext()) {
                final String pname= (String) it.next();
                if (pname == null) {
                    _log.error("<null> parameter name in action " + action);
                    continue;
                }
                final String pvalue = action.getParameter(pname);
                result.append(pname).append("=");
                if (pvalue != null) {
                    result.append( URLEncoder.encode(pvalue));
                }
                if( it.hasNext()) {
                    result.append( "&amp;"  );
                }
            }

            if (action.getAnchor() != null) {
                result.append("#").append(action.getAnchor());
            }

            actionUrl = result.toString();
        }
        
        else if (value instanceof SingleValueModel) {
            actionUrl = ((SingleValueModel) value).getValue();
        }

        else if (value != null) {
            actionUrl = value.toString();
        }

        if (enabled) {
            out.write( s_a_href );
            out.print( actionUrl );
            out.print("\"");

            Iterator origAttrIt = origTag.getAttributeNames();
            while( origAttrIt.hasNext()) {
                String attributeName= (String) origAttrIt.next();
                if ("pma:name".equals(attributeName) 
                    || "name".equalsIgnoreCase(attributeName)
                    || "href".equalsIgnoreCase(attributeName)) {
                    continue;
                }
                out.print( " " );
                out.print( attributeName );
                out.print( "=\"" );
                out.print( origTag.getAttribute(attributeName) );
                out.print( "\"" );
            }
            out.write( s_closebracket);
        }
        
        try {
            templateContent.accept(renderVisitor);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RenderException(e);
        }

        if (enabled) {
            out.write(s_end_a_tag);
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
