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

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.pagstract.model.SingleValueModel;

/**
 * An Pagstract-Input tag.
 * @author Henner Zeller
 */
public class InputTag extends FormInputFieldTag {
    private static final boolean DEBUG_INPUT = false;
    private String _type;
    private String _value;
    private String _maxlength;
    
    public InputTag() {
        reset();
    }

    private void reset() {
        _type = null;
        _value = null;
        _maxlength = null;
    }

    public int doStartTag() throws JspTagException { 
        Object object = getNameResolver().resolveName( getName() );

        /*
         * the value is either directly a String or comes from
         * a SingleValueModel.
         */
        String value = null;
        if ( object instanceof SingleValueModel ) {
            value = ((SingleValueModel)object).getValue();
        }
        else if (object != null) {
            value = object.toString();
        }

        JspWriter out = pageContext.getOut();
	try {
	    out.write("<input name=\"");
            String name = getFormFieldName();
            if (name != null) {
                out.write( name );
            }
            else {
                out.write( getName() );
                System.err.println("No mapped name found for " + getName());
            }
	    out.write("\"");
            String type = getType();
            if ("checkbox".equals(type) || "radio".equals(type)) {
                String tagValue = getValue();
                if (tagValue != null) {
                    out.write(" value=\"");
                    encode(out, tagValue);
                    out.write("\"");
                }
                if (value == tagValue 
                    || (value != null && value.equals(tagValue))) {
                    out.write(" checked");
                }
            }
            else {
                out.write(" value=\"");
                encode(out, value);
                out.write("\"");
            }
            if (type == null) {
                type="text";
            }
            out.write(" type=\"");
            encode(out, type);
            out.write("\"");
            writeInputFieldAttributes(out);
            out.write("/>");
        } 
        catch (IOException e) {
            throw new JspTagException("InputTag: " + e.getMessage());
        }
	return SKIP_BODY;
    }
    
    /**
     * reset our properties so that this tag can be re-used.
     */
    public int doEndTag() throws JspTagException {
        super.doEndTag();
        reset();
        return EVAL_PAGE;
    }

    protected void writeInputFieldAttributes(JspWriter out) 
        throws IOException {
        super.writeInputFieldAttributes(out);
        if (getMaxlength() != null) {
            out.write(" maxlength=\"");
            out.write( getMaxlength() );
            out.write("\"");
        }
    }

    /**
     * the type of the tag. By default, this is 'text', but
     * it can be as well 'password' or 'checkbox' or 'radio'
     */
    public void setType(String type) {
        _type = type;
    }
    public String getType() {
        return _type;
    }

    /**
     * maxsize. For text-input tags.
     */
    public void setMaxlength(String maxlength) {
        _maxlength = maxlength;
    }
    public String getMaxlength() {
        return _maxlength;
    }

    /**
     * some type of tags, esp. radiobuttons and checkboxes, have
     * a value set. Use this.
     */
    public void setValue(String value) {
        _value = value;
    }
    public String getValue() {
        return _value;
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
