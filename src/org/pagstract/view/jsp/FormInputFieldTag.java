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

/**
 * Base class for all input tags. They all have in common, that
 * they have the same set of properties.
 * @author Henner Zeller
 */
public abstract class FormInputFieldTag extends PagstractTag {
    private String _size;
    private String _cssClass;
    private String _onChange;
    private String _id;

    public abstract int doStartTag() throws JspTagException;

    /**
     * reset our properties so that this tag can be re-used.
     */
    public int doEndTag() throws JspTagException {
        _size = null;
        _cssClass = null;
        _onChange = null;
        _id = null;
        return EVAL_PAGE;
    }

    /**
     * for derived Tags. Gets the name of the actual form fields.
     */
    protected String getFormFieldName() {
        String name = getName();
        int dotPos = name.lastIndexOf('.');
        if (dotPos >= 0) {
            name = name.substring(dotPos+1);
        }
        Object parent = getParent();
        /*
         * if this input-field is within the iterator, then append
         * the number of the current iteration to be able to distinguish
         * the fields.
         */
        if (parent instanceof IteratorTag) {
            IteratorTag itTag = (IteratorTag) parent;
            /*
             * prefix: name of the iterator, suffix _(number)
             */
            name = itTag.getName() + "." + name 
                +  "_" + (String.valueOf(itTag.getCurrentCount()));
        }
        return name;
    }

    /**
     * write out the additional attributes that have been collected
     * here.
     */
    protected void writeInputFieldAttributes(JspWriter out) 
        throws IOException {
        if (getSize() != null) {
            out.write(" size=\"");
            out.write( getSize() );
            out.write("\"");
        }
        if (getCssClass() != null) {
            out.write(" class=\"");
            out.write( getCssClass() );
            out.write("\"");
        }
        if (getOnChange() != null) {
            out.write(" onChange=\"");
            out.write( getOnChange() );
            out.write("\"");
        }
        if (getId() != null) {
            out.write(" id=\"");
            out.write( getId() );
            out.write("\"");
        }
    }
    
    /**
     * get the CSS-Class. This is the CSS-'class' property ..
     * but we cannot name it 'class' for obvious reasons.
     */
    public String getCssClass() {
        return _cssClass;
    }

    /**
     * set the CSS-Class. This is the CSS-'class' property ..
     * but we cannot name it 'class' for obvious reasons.
     */
    public void setCssClass(String cssClass) {
        _cssClass = cssClass;
    }
    
    /**
     * get the size of this input element. Depending on the Element itself,
     * this might mean different things.
     */
    public String getSize() {
        return _size;
    }
    public void setSize(String size) {
        _size = size;
    }

    public String getId() {
        return _id;
    }
    public void setId(String id) {
        _id = id;
    }

    /**
     * Java script stuff.
     */
    public String getOnChange() {
        return _onChange;
    }
    public void setOnChange(String onChange) {
        _onChange = onChange;
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
