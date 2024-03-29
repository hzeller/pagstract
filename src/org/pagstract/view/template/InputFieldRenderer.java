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
import java.util.Set;
import java.util.HashSet;

import org.pagstract.io.Device;
import org.pagstract.model.ComponentModel;
import org.pagstract.model.AttributeSet;
import org.pagstract.model.ActionModel;
import org.pagstract.model.SingleValueModel;
import org.pagstract.view.template.parser.ast.InputFieldNode;
import org.pagstract.view.template.parser.ast.TemplateNode;
import org.pagstract.view.template.parser.ast.Visitor;
import org.pagstract.view.template.parser.scanner.TemplateToken;

/**
 * Writes an HTML input field.
 */
public class InputFieldRenderer implements ComponentRenderer {
    private static final byte[] s_textarea      = "<textarea".getBytes();
    private static final byte[] s_end_textarea  = "</textarea>".getBytes();
    private static final byte[] s_input         = "<input".getBytes();
    private static final byte[] s_name_eq       = " name=\"".getBytes();
    private static final byte[] s_value_eq      = " value=\"".getBytes();
    private static final byte[] s_type_eq       = " type=\"".getBytes();
    private static final byte[] s_quot_space    = "\" ".getBytes();
    private static final byte[] s_equals_quot   = "=\"".getBytes();
    private static final byte[] s_close_tag     = ">".getBytes();
    private static final byte[] s_simple_close_tag = ">".getBytes();
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
        AttributeSet attributeSet = null;

        boolean isEnabled = true;
        boolean isVisible = true;

        if (inputValue instanceof ComponentModel) {
            ComponentModel model = (ComponentModel) inputValue;
            isVisible = model.isVisible();
            isEnabled = model.isEnabled();
        }

        if (inputValue instanceof AttributeSet) {
            attributeSet = (AttributeSet) inputValue;
        }

        String value = null;
        if (inputValue instanceof SingleValueModel) {
            value = ((SingleValueModel) inputValue).getValue();
            if (value == null) {
                value = "";
            }
        }
        else if (inputValue instanceof ActionModel) {
            final String origValue = origTag.getAttribute("value");
            final String attribValue = ((attributeSet != null)
                                        ? attributeSet.getAttribute("value")
                                        : null);
            if (attribValue != null) {
                value = attribValue;
            }
            else {
                value = (origValue != null) ? origValue : "1";
            }
        }
        else {
            value = (inputValue != null) ? inputValue.toString() : "";
        }
        
        /*
         * Der Name ist der pma:name, wenn es keinen entsprechenden 
         * Namen im HTML gibt. In beiden Fällen wird hinterher der Suffix
         * aus der aktuellen Iteration 'drangehängt.
         * Ein Attribute 'name' überschreibt alles.
         * --- vereinheitlichen in InputField und Select
         */
        String	inputName = origTag.getName();
        inputName = (inputName == null) ? node.getModelName() : inputName;
        if (_suffix != null) {
            inputName += _suffix;
        }
        
        if (attributeSet != null) {
            String overrideName = attributeSet.getAttribute("name");
            if (overrideName != null) {
                inputName = overrideName;
            }
        }

        if (renderVisitor instanceof TemplatePageEmitter) {
            ((TemplatePageEmitter)renderVisitor).addUsedInputFieldName(inputName);
        }

        final String inputType = (isVisible 
                                  ? origTag.getAttribute("type") 
                                  : "hidden");
        final boolean isTextarea = "textarea".equals(inputType);

        out.write( isTextarea ? s_textarea : s_input );
        
        if (true || isEnabled) { // for now, we always need the name
            out.write( s_name_eq );
            out.print( inputName );
            out.write( s_quot_space );
        }

        /*
         * checkbox/radio-buttons
         */
        final boolean checkableInput = ("checkbox".equals(inputType) 
                                        || "radio".equals(inputType));
        
        /*
         * FIXME: if we rewrite this to hidden, then only _one_
         * variable for checkableInputs must be emitted.
         */
        // input type..
        out.write(s_type_eq);
        out.print(inputType);
        out.write(s_quot_space);

        if (!isTextarea) {
            if (checkableInput) {
                // schauen, ob der Wert dasselbe ist wie im Attribut.
                // ob das Attribut nun aus dem attribut-Set kommt oder
                // dem Template ist egal. Attribute-Set hat vorrang.
                final String attribValue = ((attributeSet != null)
                                            ? attributeSet.getAttribute("value")
                                            : null);
                final String selectionRelevant = (attribValue != null) 
                    ? attribValue
                    : origTag.getAttribute("value");
                
                if (value.equals(selectionRelevant)) {
                    out.write( s_checked );
                }
            } 
            else {  /* text input */
                out.write( s_value_eq );
                if (value != null) {
                    Utils.quote( out, value.toString());
                }
                out.write( s_quot_space );
            }
        }
        
        if (!isEnabled) {
            out.write( s_readonly );
            if (checkableInput) {
                out.write( s_disabled );
            }
        }

        final Set alreadyWritten = new HashSet();
        if (checkableInput) {
            alreadyWritten.add("checked");
        }
        else {
            alreadyWritten.add("value");
        }
        alreadyWritten.add("pma:name");
        alreadyWritten.add("name");
        alreadyWritten.add("type");
        appendAttributes(out, alreadyWritten, attributeSet);
        appendAttributes(out, alreadyWritten, origTag);

        if (!isTextarea) {
            out.write( s_close_tag );
        }
        else {
            out.write( s_simple_close_tag );
            if (value != null) {
                Utils.quote( out, value.toString());
            }
            out.write( s_end_textarea );
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
            out.write( s_quot_space );
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
