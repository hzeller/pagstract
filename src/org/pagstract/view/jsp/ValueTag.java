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

public class ValueTag extends PagstractTag {
    private String name = null;

    public int doStartTag() throws JspTagException { 
	Object object = getNameResolver().resolveName(name);
        if (object == null) {
            if (DEBUG_PAG) {
                System.err.println("value is 'null' for " + name);
            }
            return SKIP_BODY;
        }

        JspWriter out = pageContext.getOut();
	try {
            if (object instanceof SingleValueModel) {
                encode(out, ((SingleValueModel)object).getValue());
            }
            else {
                encode(out, object.toString());
            }
        } catch (IOException e) {
            throw new JspTagException(e.getMessage());
        }
	return SKIP_BODY;
    }
    
    public String getName(){
	return this.name;
    }

    public void setName(String name){
	this.name = name;
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
