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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * The case tag within a switch body.
 * @author Henner Zeller
 */
public class CaseTag extends BodyTagSupport {
    private String  _caseValue;
    private boolean _isDefaultVal;

    public CaseTag() {
        reset();
    }

    private void reset() {
        _caseValue = null;
        _isDefaultVal = false;
    }

    public int doStartTag() throws JspTagException { 
        final SwitchTag switchTag = (SwitchTag) getParent();

        if (switchTag.isEvaluated()) {
            return SKIP_BODY;            // nothing do do anymore.
        }

        if (_caseValue == null) {        // we are the default Value; store it.
            _isDefaultVal = true;
            return EVAL_BODY_BUFFERED;
        }

        if (_caseValue.equals(switchTag.getMatchValue())) {
            switchTag.setEvaluated(true);
            return EVAL_BODY_INCLUDE;
        }
        
        return SKIP_BODY;
    }

    public int doAfterBody() throws JspTagException {
        /*
         * if we are the default value, then we have to give the
         * string to 'our' SwitchTag, that might want to store
         * it.
         */
        if (_caseValue == null && _isDefaultVal) {
            SwitchTag switchTag = (SwitchTag) getParent();
            BodyContent bodyContent = getBodyContent();
            switchTag.setDefaultBody(bodyContent.getString());
            bodyContent.clearBody();
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

    public void setCase(String c) {
        _caseValue = c;
    }
    public String getCase() {
        return _caseValue;
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
