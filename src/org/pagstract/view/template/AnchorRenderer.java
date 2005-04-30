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
import java.util.Set;
import java.util.HashSet;

import org.pagstract.io.Device;
import org.pagstract.model.ActionModel;
import org.pagstract.model.AttributeSet;
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
    private static final byte[] s_area_href  = "<area href=\"".getBytes();
    private static final byte[] s_questmark  = "?".getBytes();
    private static final byte[] s_equals     = "=".getBytes();
    private static final byte[] s_ampersand  = "&amp;".getBytes();
    private static final byte[] s_closebracket = ">".getBytes();
    private static final byte[] s_end_a_tag  = "</a>".getBytes();
    
    private static final byte[] s_quote_space= "\" ".getBytes();
    private static final byte[] s_slash      = "/".getBytes();
    private static final byte[] s_equals_quot   = "=\"".getBytes();

    private final ActionUrlProvider _urlProvider;
    private final ResourceResolver _messageResolver;

    public AnchorRenderer(ActionUrlProvider urlProvider, ResourceResolver msgResolver) {
        _urlProvider = urlProvider;
        _messageResolver = msgResolver;
    }
    
    static String buildActionUrl(ActionModel action, 
                                 ActionUrlProvider urlProvider) 
    {
        StringBuffer result = new StringBuffer();
        Iterator it= action.getParameterNames();
        String url = action.getUrl();
        if (urlProvider != null) {
            url = urlProvider.resolveUrl(action, url, false);
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

            if (it.hasNext()) {
                result.append( "&amp;"  );
            }
        }

        if (action.getAnchor() != null) {
            result.append("#").append(action.getAnchor());
        }

        return result.toString();
    }

    public void render(Visitor renderVisitor,
                       TemplateNode n, 
                       Object value, Device out) 
        throws IOException 
    {
        AnchorNode node = (AnchorNode) n;
        TemplateToken origTag = node.getTemplateToken();

        TemplateNode templateContent = node.getTemplateContent();
        
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
        final boolean isArea = "area".equals(node.getLinkType());

        /*
         * If this is an action model: build the URL right
         * from it.
         */
        if (value instanceof ActionModel) {
            actionUrl = buildActionUrl((ActionModel) value, _urlProvider);
        }
        
        else if (value instanceof SingleValueModel) {
            actionUrl = ((SingleValueModel) value).getValue();
        }

        else if (value != null) {
            actionUrl = value.toString();
        }

        /*
         * das mit den styles und classes ist ein
         * Versuch. Wenn das gut ist, dann kommt es mit ins
         * modell
         */
        final Set alreadyWritten = new HashSet();
        boolean wroteSpan = false;
        if (enabled) {
            out.write( isArea ? s_area_href : s_a_href );
            out.print( actionUrl );
            out.print("\"");
            
            // FIXME: copy-paste
            Iterator origAttrIt = origTag.getAttributeNames();
            while( origAttrIt.hasNext()) {
                final String attributeName= (String) origAttrIt.next();
                if ("pma:name".equals(attributeName) 
                    || "href".equalsIgnoreCase(attributeName)) {
                    continue;
                }
                
                String tagAttribute = attributeName;
                /* ein Versuch: wenn das gut ist, muss das mit ins
                 * model. */
                if (tagAttribute.startsWith("pma:")) {
                    if (attributeName.equals("pma:enabled-class")) {
                        tagAttribute = "class";
                    }
                    else if (attributeName.equals("pma:enabled-style")) {
                        tagAttribute = "style";
                    }
                    // kein rewrite stattgefunden: weiter ..
                    if (tagAttribute.startsWith("pma:")) {
                        continue;
                    }
                }
                out.print( " " );
                out.print( tagAttribute );
                out.print( "=\"" );
                out.print( resolveAttribute(origTag.getAttribute(attributeName)) );
                out.print( "\"" );
                alreadyWritten.add(tagAttribute);
            }
            if (value instanceof AttributeSet) {
                AttributeSet givenSet = (AttributeSet) value;
                appendAttributes(out, alreadyWritten, givenSet);
            }
            if (isArea) {
                out.write( s_slash );
            }
            out.write( s_closebracket);
        }
        else {
            Iterator origAttrIt = origTag.getAttributeNames();
            while( origAttrIt.hasNext()) {
                final String attributeName= (String) origAttrIt.next();
                if ("pma:name".equals(attributeName) 
                    || "href".equalsIgnoreCase(attributeName)) {
                    continue;
                }
                
                String tagAttribute = attributeName;
                if (tagAttribute.startsWith("pma:")) {
                    if (tagAttribute.equals("pma:disabled-class")) {
                        tagAttribute = "class";
                    }
                    else if (tagAttribute.equals("pma:disabled-style")) {
                        tagAttribute = "style";
                    }
                    // kein rewrite stattgefunden: weiter ..
                    if (tagAttribute.startsWith("pma:")) {
                        continue;
                    }
                }
                
                if (!wroteSpan) {
                    out.print("<span");
                    wroteSpan = true;
                }
                out.print(" ");
                out.print( tagAttribute );
                out.print( "=\"" );
                out.print( resolveAttribute(origTag.getAttribute(attributeName)) );
                out.print( "\"" );
            }
            if (wroteSpan) {
                out.print(">");
            }
        }
        
        if (templateContent != null) {
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
        }
        
        if (enabled && !isArea) {
            out.write(s_end_a_tag);
        }
        else if (wroteSpan) {
            out.print("</span>");
        }
    }

    private void appendAttributes(Device out, Set alreadyWritten, 
                                  AttributeSet attributes) 
        throws IOException
    {
        if (attributes == null) return;
        Iterator it = attributes.getAttributeNames();
        while (it.hasNext()) {
            String attributeName= (String)it.next();
            if (alreadyWritten.contains(attributeName.toLowerCase())) {
                continue;
            }
            out.print( attributeName );
            out.write( s_equals_quot );
            out.print( attributes.getAttribute(attributeName) );
            out.write( s_quote_space );
            alreadyWritten.add(attributeName);
        }
    }
    
    /**
     * 
     */
    private String resolveAttribute(String attr) {
        if (_messageResolver != null && attr.startsWith("msg://")) {
            try {
                attr = attr.substring(6);
                if (attr.endsWith("/")) {
                    attr = attr.substring(0, attr.length()-1);
                }
                return _messageResolver.resolveResource(attr);
            }
            catch (Exception e) {
                return "could-not-resolve-resource " + attr;
            }
        }
        return attr;
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
