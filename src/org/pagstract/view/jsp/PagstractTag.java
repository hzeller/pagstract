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

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.pagstract.view.namespace.NameResolver;

public abstract class PagstractTag extends BodyTagSupport {
    protected static final boolean DEBUG_PAG = true;

    public static final String NAMERESOLVER= "pagstract-nameresolver";
    public static final String CONTEXT_URI = "pagstract-servlet-uri";

    /**
     * all PagstractTags have a name that references the element given
     * in the PageModel.
     */
    private String _name;

    /*
     * properties used in all PagstractTags.
     */

    /**
     * get the Name of this Tag. This is the abstract name that is
     * used as Property name in the PageModel. Internally, this name
     * might be mapped to some other (shorter ?) name.
     */
    public String getName(){
	return _name;
    }

    /**
     * the name of the Property in the PageModel.
     */
    public void setName(String name){
        _name = name;
    }

    /**
     * reset name.
     */
    public int doEndTag() throws JspException {
        super.doEndTag();
        _name = null;
        return EVAL_PAGE;
    }

    /*
     * support methods used in all PagstractTags.
     */

    protected NameResolver getNameResolver() {
        ServletRequest request = pageContext.getRequest();
        return(NameResolver) request.getAttribute(NAMERESOLVER);
    }

    /**
     * RFC 1866 encode the value given in the string. This encodes
     * al XML/HTML entities that otherwise might confuse the browser ;-)
     */
    protected void encode(JspWriter writer, String toEncode) 
        throws IOException {
	if (toEncode == null) return;
        char[] chars = toEncode.toCharArray();
	char c;
        int last = 0;
	for (int pos = 0; pos < chars.length; ++pos) {
            c = chars[pos];
            /*
	     * RFC 1866 encoding
	     */
	    switch (c) {
	    case '&': 
		writer.write(chars, last, (pos-last));
		writer.write("&amp;");
		last = pos+1;
		break;
	    case '"': 
		writer.write(chars, last, (pos-last));
		writer.write("&quot;");
		last = pos+1;
		break;
	    case '<': 
		writer.write(chars, last, (pos-last));
		writer.write("&lt;");
		last = pos+1;
		break;
	    case '>':
		writer.write(chars, last, (pos-last));
		writer.write("&gt;");
		last = pos+1;
		break;
	    }
	}
        writer.write(chars, last, chars.length-last);
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
