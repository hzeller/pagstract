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
import javax.servlet.jsp.tagext.BodyContent;

import org.pagstract.view.namespace.IteratorNamespace;
import org.pagstract.view.namespace.ListCursor;

/**
 * The Pagstract-List iterator tag.
 * @author Christoph Krone, Henner Zeller
 */
public class IteratorTag extends PagstractTag implements ListCursor {
    private Object   _objectArray[];
    private int      _currentIndex;    

    public IteratorTag() {
        reset();
    }

    private void reset() {
        _objectArray = null;
        _currentIndex = 0;
    }

    /**
     ** 
     */
    public int doStartTag() throws JspTagException { 
	Object object = getNameResolver().resolveName( getName() ) ;
	if (object == null) {
	    return SKIP_BODY;
	}
        
        _objectArray = (Object[]) object;
        if (_objectArray.length == 0) {
            return SKIP_BODY;
        }
        _currentIndex = 0;
        Class arrayClass = _objectArray.getClass().getComponentType();
        getNameResolver().pushNamespace(new IteratorNamespace(arrayClass,
                                                              this));
	return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspTagException {
	BodyContent bodyContent = getBodyContent();
        JspWriter out = getPreviousOut();
        try {
            out.write(bodyContent.getString());
            bodyContent.clearBody();
        } 
        catch(IOException e) {
            throw new JspTagException("IteratorTag (name=" + getName() 
                                      + ": " + e.getMessage());
        }

	if (_currentIndex < _objectArray.length ){
            ++_currentIndex;
	    return EVAL_BODY_BUFFERED;
	}
        getNameResolver().popNamespace();
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
    
    //-- implementation of the ListCursor Interface
    public Object getCurrentObject(){
	return _objectArray[_currentIndex];
    }
    public int getCurrentCount() {
        return _currentIndex + 1; // count starts with '1'
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
