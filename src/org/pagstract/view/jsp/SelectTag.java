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
import java.util.Iterator;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.pagstract.model.ComboboxModel;
import org.pagstract.model.SelectionDescriptor;

/**
 * The Pagstract-Select tag.
 * @author Christoph Krone, Henner Zeller
 */
public class SelectTag extends FormInputFieldTag {
    private static final boolean DEBUG_SELECT = false;

    public int doStartTag() throws JspTagException { 
        Object object = getNameResolver().resolveName( getName() );
        /*
         * FIXME: Separate SelectionListModel and SingleValueModel.
         * (s. template)
         */
        if (! (object instanceof ComboboxModel)) {
            System.err.print("The PageModel property '" + getName()
                             + "' is not a ComboboxModel: "
                             + object);
            if (object != null) {
                System.err.print("; type was: " + object.getClass());
            }
            System.err.println(" - skipping...");
            return SKIP_BODY;
        }

	ComboboxModel comboboxModel = (ComboboxModel) object;
        JspWriter out = pageContext.getOut();
	try {
	    // create the select-tag
	    out.write("<select name=\"");
            String name = getFormFieldName();
            if (name != null) {
                out.write( name );
            }
            else {
                System.err.println("No mapped name found for " + getName());
            }
	    out.write("\"");
            writeInputFieldAttributes(out);
            out.write(">\n");
	    writeOptions(out, comboboxModel);
	    out.write("</select>");
        } 
        catch (IOException e) {
            throw new JspTagException("SelectTag: " + e.getMessage());
        }
	return SKIP_BODY;
    }

    private void writeOptions(JspWriter out, ComboboxModel comboboxModel) 
        throws IOException {
        if (DEBUG_PAG && DEBUG_SELECT) {
            System.err.println("--------------------------------------------");
            System.err.println("name=" + getName() +", " + comboboxModel);
            System.err.println("--------------------------------------------");
        }
	Iterator nameValues =comboboxModel.getSelectionDescriptors();
	while (nameValues.hasNext()){
	    SelectionDescriptor selectionDescriptor = (SelectionDescriptor)nameValues.next();
	    String label = selectionDescriptor.getLabel();
	    String value = selectionDescriptor.getValue();
	    out.write("\t<option value=\"");
            if (value != null) {
                out.write(value);
            }
	    out.write("\"");
	    if (value != null && value.equals(comboboxModel.getValue())){
		out.write(" selected"); // should be selected="selected" to be
                                        // XHTML-compatible. But this confuses
                                        // mozilla.
	    }
	    out.write(">");
            if (label != null) {
                encode(out, label);
            }
	    out.write("</option>\n");
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
