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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.pagstract.model.SingleValueModel;

/**
 * The Pagstract-Switch tag.
 * @author Henner Zeller
 */
public class SwitchTag extends PagstractTag {
    private String  _matchValue;
    private boolean _isEvaluated;
    private String  _defaultBody;

    public SwitchTag() {
        reset();
    }

    private void reset() {
        _matchValue = null;
        _isEvaluated = false;
        _defaultBody = null;
    }

    public int doStartTag() throws JspTagException { 
        Object object = getNameResolver().resolveName( getName() );
        _matchValue = null;
        if ( object instanceof SingleValueModel ) {
            _matchValue = ((SingleValueModel)object).getValue();
        }
        else if (object != null) {
            _matchValue = object.toString();
        }
	return EVAL_BODY_INCLUDE;
    }
    
    public int doAfterBody() throws JspTagException {
        /*
         * if we are not yet evalutated, then print out the
         * default body, if any.
         */
        if (! _isEvaluated && _defaultBody != null) {
            try {
                JspWriter out = pageContext.getOut();
                out.write(_defaultBody);
            }
            catch(IOException e) {
		throw new JspTagException("IO: " + e.getMessage());
	    }
        }
        return SKIP_BODY;
    }

    /**
     * reset our properties so that this tag can be re-used.
     */
    public int doEndTag() throws JspException {
        super.doEndTag();
        reset();
        return EVAL_PAGE;
    }

    /*
     * the following methods are only used within this package by the
     * case tag.
     */
    String getMatchValue() {
        return _matchValue;
    }

    /*
     * only evaluate once.
     */
    void setEvaluated(boolean b) {
        _isEvaluated = b;
    }
    boolean isEvaluated() {
        return _isEvaluated;
    }

    void setDefaultBody(String b) {
        if (_defaultBody != null) {
            System.err.println(getName() + ": multiple default Bodies!!");
        }
        _defaultBody = b;
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
