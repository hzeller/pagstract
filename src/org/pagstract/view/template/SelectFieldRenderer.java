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
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import org.pagstract.io.Device;
import org.pagstract.model.ComponentModel;
import org.pagstract.model.AttributeSet;
import org.pagstract.model.MultipleValueModel;
import org.pagstract.model.SelectionDescriptor;
import org.pagstract.model.SelectionListModel;
import org.pagstract.model.SingleValueModel;
import org.pagstract.view.template.parser.ast.SelectFieldNode;
import org.pagstract.view.template.parser.ast.TemplateNode;
import org.pagstract.view.template.parser.ast.Visitor;
import org.pagstract.view.template.parser.scanner.TemplateToken;

/**
 *  Writes an HTML selection box.
 */
public class SelectFieldRenderer implements ComponentRenderer {
    private static final byte[] s_select     = "<select".getBytes();
    private static final byte[] s_name_eq    = " name=\"".getBytes();
    private static final byte[] s_not_enabled =" readonly=\"readonly\"".getBytes();
    private static final byte[] s_equals_quot   = "=\"".getBytes();
    private static final byte[] s_quote_space= "\" ".getBytes();
    private static final byte[] s_multiple= " multiple".getBytes();
    private static final byte[] s_bracketclose= ">".getBytes();
    private static final byte[] s_option_value="\n<option value=\"".getBytes();
    private static final byte[] s_selected= " selected".getBytes();
    private static final byte[] s_close_option= "</option>".getBytes();
    private static final byte[] s_select_close= "\n</select>".getBytes();
    private static final byte[] s_span_open = "<span ".getBytes();
    private static final byte[] s_span_close = "</span>".getBytes();

    private final String _suffix;

    public SelectFieldRenderer(String suffix) {
        _suffix = suffix;
    }

    public void render(Visitor renderVisitor,
                       TemplateNode n, 
                       Object value, Device out) 
        throws IOException 
    {
        AttributeSet attributeSet = null;

        if (value == null) {
            return;
        }

        boolean isEnabled = true;

        if (value instanceof ComponentModel) {
            ComponentModel model = (ComponentModel) value;
            if (!model.isVisible()) {
                return;
            }
            isEnabled = model.isEnabled();
        }

        if (value instanceof AttributeSet) {
            attributeSet = (AttributeSet) value;
        }

        SelectFieldNode node = (SelectFieldNode) n;
        if (!(value instanceof SelectionListModel)) {
            throw new IllegalArgumentException("SelectionField " 
                                               + node.getModelName() 
                                               + ": no SelectionListModel");
        }
        SelectionListModel selectionModel = (SelectionListModel) value;

        /*
         * Der Name ist der pma:name, wenn es keinen entsprechenden 
         * Namen im HTML gibt. In beiden Fällen wird hinterher der Suffix
         * aus der aktuellen Iteration 'drangehängt.
         * Ein Attribute 'name' überschreibt alles.
         * --- vereinheitlichen in InputField und Select
         */
        TemplateToken origTag = node.getTemplateToken();
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

        if (isEnabled) {
            out.write( s_select );
            
            out.write( s_name_eq );
            out.print( inputName );
            out.write( s_quote_space );
        }
        else {
            out.write( s_span_open );
        }

        final Set alreadyWritten = new HashSet();
        alreadyWritten.add("pma:name");
        alreadyWritten.add("name");
        appendAttributes(out, alreadyWritten, attributeSet);
        appendAttributes(out, alreadyWritten, origTag);

        String singleSelected = null;
        if (value instanceof SingleValueModel) {
            singleSelected = ((SingleValueModel) value).getValue();
        }

        Collection multipleSelected = null;
        if (value instanceof MultipleValueModel) {
            multipleSelected = ((MultipleValueModel) value).getValues();
            out.write( s_multiple );
        }
        
        out.write( s_bracketclose );
        
        Iterator it = selectionModel.getSelectionDescriptors();
        String currentValue = null;
        while (it.hasNext()) {
            SelectionDescriptor desc = (SelectionDescriptor) it.next();
            String selVal = desc.getValue();
            boolean matchValue = false;
            if ( singleSelected != null && singleSelected.equals(selVal) ) {
                matchValue = true;
            }
            if ( multipleSelected != null&&multipleSelected.contains(selVal)) {
                matchValue = true;
            }

            if (isEnabled) {
                /*
                 * write <option></option>
                 */
                out.write( s_option_value );
                Utils.quote( out, selVal);
                out.write( s_quote_space );
                if (matchValue) {
                    out.write( s_selected );
                }
                out.write( s_bracketclose );
                Utils.quote( out, desc.getLabel().toString());
                out.write( s_close_option );
            }
            else if (matchValue) {
                /*
                 * otherwise write only the matching value ..
                 */
                currentValue = selVal;
                Utils.quote( out, desc.getLabel().toString());
            }
        }
        if (isEnabled) {
            out.write( s_select_close );
        }
        else {
            out.write( s_span_close );
        }

        // HACK:
        if (!isEnabled && currentValue != null) {
            out.print("<input type='hidden' name='");
            out.print(inputName);
            if (_suffix != null) {
                out.print(_suffix);
            }
            out.print("'" + " value='");
            Utils.quote(out, currentValue);
            out.print("'/>");
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
            if (alreadyWritten.contains(attributeName)) {
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
