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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.pagstract.io.Device;
import org.pagstract.model.ActionModel;
import org.pagstract.model.AttributeSet;
import org.pagstract.view.template.parser.ast.FormNode;
import org.pagstract.view.template.parser.ast.TemplateNode;
import org.pagstract.view.template.parser.ast.Visitor;
import org.pagstract.view.template.parser.scanner.TemplateToken;


/**
 * A Component Renderer is used in the TemplatePageEmitter to write
 * out components.
 */
public class FormRenderer implements ComponentRenderer {

    private static final byte[] s_form= "<form ".getBytes();
    private static final byte[] s_action= "action=\"".getBytes();
    private static final byte[] s_quote = "\" ".getBytes();
    private static final byte[] s_questmark  = "?".getBytes();
    private static final byte[] s_equals     = "=".getBytes();
    private static final byte[] s_ampersand  = "&amp;".getBytes();
    private static final byte[] s_closebracket = ">".getBytes();
    private static final byte[] s_end_form_tag = "</form>".getBytes();
    private static final byte[] s_quote_space= "\" ".getBytes();
    private static final byte[] s_equals_quot   = "=\"".getBytes();
 
    private static final byte[] s_input = "<input ".getBytes();
    private static final byte[] s_type_hidden = "type=\"hidden\" ".getBytes();
    private static final byte[] s_name_equals_quot = "name=\"".getBytes();
    private static final byte[] s_quot_value_equals_quot = "\" value=\"".getBytes();
    private static final byte[] s_quot_end_tag = "\" >".getBytes();

    private final ActionUrlProvider _urlProvider;
    
    public FormRenderer(ActionUrlProvider urlProvider) {
        _urlProvider = urlProvider;
    }
    
    public void render(Visitor renderVisitor,
                       TemplateNode n, 
                       Object value, Device out) 
        throws IOException 
    {
        FormNode node = (FormNode) n;
        TemplateNode templateContent = node.getTemplateContent();
        if (templateContent == null) {
            return;
        }

        if (value == null || !(value instanceof ActionModel)) {
            out.print("<!-- no action -->");
            return;
        }

        ActionModel action = (ActionModel) value;
        
        if (action != null && action.isEnabled()) {
            // start form tag
            out.write( s_form );
	
            // print all parameters of the tag, except pma:name
            final Set alreadyWritten = new HashSet();
            TemplateToken origTag = node.getTemplateToken();
            Iterator it = origTag.getAttributeNames();
            while( it.hasNext()) {
                String attributeName= (String)it.next();
                if ("pma:name".equals(attributeName)) {
                    continue;
                }
                out.print( attributeName );
                out.write( s_equals_quot );
                out.print( origTag.getAttribute(attributeName) );
                out.write( s_quote_space );
                alreadyWritten.add(attributeName);
            }

            if (value instanceof AttributeSet) {
                AttributeSet givenSet = (AttributeSet) value;
                appendAttributes(out, alreadyWritten, givenSet);
            }

            // write out the action
            String url = action.getUrl();
            if (_urlProvider != null) {
                url = _urlProvider.resolveUrl(url);
            }           
            if (url != null) {
                out.write( s_action );
                out.print( url );
                out.write( s_quote );
            } 
            
            // close the form tag.
            out.write( s_closebracket );

            Set handledParamNames = new HashSet();
            if (renderVisitor instanceof TemplatePageEmitter) {
                ((TemplatePageEmitter)renderVisitor)
                    .setFormParameterCollector(handledParamNames, 
                                               node);
            }

            /*
             * render content of the form.
             */
            try {
                templateContent.accept(renderVisitor);
            }
            catch (IOException e) {
                throw e;
            }
            catch (Exception e) {
                throw new RenderException(e, "at " + n.getPosition());
            }

            if (renderVisitor instanceof TemplatePageEmitter) {
                ((TemplatePageEmitter)renderVisitor)
                    .setFormParameterCollector(null, null);
            }
            
            /*
             * write all parameters that have not been handled by any
             * Input fields.
             */
            // write all parameters to the action as hidden input fields
            it= action.getParameterNames();
            while( it.hasNext()) {
                String pname= (String) it.next();
                if (handledParamNames.contains(pname)) {
                    out.print("<!-- '" + pname + "' in form -->\n");
                    continue; // already handled in some input
                }
                String pvalue = action.getParameter(pname);

                // only create hidden input field, if parameter ist set
                if ((pvalue != null) && (pvalue.length()>0)) {
                    out.write( s_input);
                    out.write( s_type_hidden);
                    out.write( s_name_equals_quot);
                    out.print( pname );
                    out.write( s_quot_value_equals_quot);
                    Utils.quote(out, pvalue );
                    out.write( s_quot_end_tag);
                    out.print("\n");
                }
            }
           
        }
        
        if (action.isEnabled()) {
            out.write(s_end_form_tag);
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
