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

import org.pagstract.io.Device;
import org.pagstract.model.ComponentModel;
import org.pagstract.model.SingleValueModel;
import org.pagstract.view.template.parser.ast.InputFieldNode;
import org.pagstract.view.template.parser.ast.TemplateNode;
import org.pagstract.view.template.parser.ast.Visitor;
import org.pagstract.view.template.parser.scanner.TemplateToken;

/**
 * Writes an HTML input field.
 */
public class InputFieldRenderer implements ComponentRenderer {
    private static final byte[] s_input         = "<input".getBytes();
    private static final byte[] s_name_eq       = " name=\"".getBytes();
    private static final byte[] s_value_eq      = " value=\"".getBytes();
    private static final byte[] s_type_eq       = " type=\"".getBytes();
    private static final byte[] s_quot          = "\" ".getBytes();
    private static final byte[] s_equals_quot   = "=\"".getBytes();
    private static final byte[] s_close_tag     = "/>".getBytes();
    private static final byte[] s_checked       = " checked=\"checked\" ".getBytes();
    private static final byte[] s_readonly   = " readonly=\"readonly\" ".getBytes();
    private static final byte[] s_disabled   = " disabled=\"disabled\" ".getBytes();

    private final String _suffix;

    public InputFieldRenderer(String suffix) {
        _suffix = suffix;
    }

    public void render(Visitor renderVisitor,
                       TemplateNode n, 
                       Object inputValue, Device out) 
        throws IOException 
    {
        InputFieldNode node = (InputFieldNode) n;
        TemplateToken origTag = node.getTemplateToken();
        
        boolean isEnabled = true;
        boolean isVisible = true;

        if (inputValue instanceof ComponentModel) {
            ComponentModel model = (ComponentModel) inputValue;
            isVisible = model.isVisible();
            isEnabled = model.isEnabled();
        }

        String value = null;
        if (inputValue instanceof SingleValueModel) {
            value = ((SingleValueModel) inputValue).getValue();
        }
        else {
            value = (inputValue != null) ? inputValue.toString() : "";
        }
        
        String	inputName = origTag.getName();
        inputName = (inputName == null) ? node.getModelName() : inputName;

        if (renderVisitor instanceof TemplatePageEmitter) {
            ((TemplatePageEmitter)renderVisitor).addUsedInputFieldName(inputName);
        }

        out.write( s_input );
        
        if (true || isEnabled) {
            out.write( s_name_eq );
            // FIXME: make this work identical in JSP and Template..
            out.print( inputName );
            if (_suffix != null) {
                out.print(_suffix);
            }
            out.write( s_quot );
        }

        /*
         * checkbox/radio-buttons
         */
        String inputType = isVisible ? origTag.getAttribute("type") : "hidden";
        boolean checkableInput = ("checkbox".equals(inputType) 
                                  || "radio".equals(inputType));
        /*
         * FIXME: if we rewrite this to hidden, then only _one_
         * variable for checkableInputs must be emitted.
         */
        // input type..
        out.write(s_type_eq);
        out.print(inputType);
        out.write(s_quot);

        if (checkableInput) {
            String templateValue = origTag.getAttribute("value");
            if (value.equals(templateValue)) {
                out.write( s_checked );
            }
        } 
        else {  /* text input */
            out.write( s_value_eq );
            if (value != null) {
                Utils.quote( out, value.toString());
            }
            out.write( s_quot );
        }
        
        if (!isEnabled) {
            out.write( s_readonly );
            if (checkableInput) {
                out.write( s_disabled );
            }
        }

        Iterator it= origTag.getAttributeNames();
        while( it.hasNext()) {
            String attributeName= (String)it.next();
            if ("pma:name".equals(attributeName) 
                || "name".equalsIgnoreCase(attributeName)
                || "type".equalsIgnoreCase(attributeName)) {
                continue;
            }
            out.print( attributeName );
            out.write( s_equals_quot );
            out.print( origTag.getAttribute(attributeName) );
            out.write( s_quot );
        }
        out.write( s_close_tag );
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
