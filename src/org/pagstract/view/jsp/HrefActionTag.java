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
import java.net.URLEncoder;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.pagstract.model.ActionModel;

public class HrefActionTag extends PagstractTag {
    private static final boolean DEBUG_HREF = true;

    /**
     * if there is not action model defined, then we just
     * write the content but no link around it.
     */
    private ActionModel _action;

    /**
     * If we don't write a link, then we still have to set the
     * possible css-attribute. So if there is an attribute, we write
     * a <span></span> around it.
     */
    private boolean     _hasSpan;

    /**
     * if we havn't got an action model: comment out the text.
     */
    private boolean     _commentedOut;

    /*
     * properties set externally.
     */
    private String _name;
    private String _onClick;
    private String _cssClass;
    private String _target;
    private String _baseUrl;
    private String _id;

    public HrefActionTag() {
        reset();
    }

    private void reset() {
        _action = null;
        _onClick = null;
        _cssClass = null;
        _target = null;
        _hasSpan = false;
        _commentedOut = false;
    }

    public int doStartTag() throws JspTagException { 
        Object object = getNameResolver().resolveName( getName() );
        _action = null;
        JspWriter out = pageContext.getOut();

        /*
         * get action model from page..
         */
        if (object == null) {
            System.err.println("ActionModel " + getName() + " is empty");
            /*
             * Wenn kein Teil gegeben ist, dann wird der gesamte Link
             * nicht dargestellt.
             */
            try {
                out.write("<!-- no action for linktext [");
            }
            catch (IOException e) {
                throw new JspTagException(e.getMessage());
            }
            _commentedOut = true;
            return EVAL_BODY_INCLUDE;
        }
        if ( object instanceof ActionModel ) {
            _action = (ActionModel) object;
        }
        else {
            System.err.println(getName() + " does not implement ActionModel");
            /*
             * Be graceful; show the text.
             */
            return EVAL_BODY_INCLUDE;
        }

        /*
         * handle this model.
         */
        try {
            /*
             * an action that is not enabled is not written as link.
             * Still, if there are cascading stylesheet attributes, we
             * have to set them via a <span class="foo"></span>
             */
            if (! _action.isEnabled()) {
                _action = null;
                if (getCssClass() != null) {
                    out.write("<span class=\"");
                    out.write(getCssClass());
                    out.write("\">");
                    _hasSpan = true;
                }
                else {
                    _hasSpan = false;
                }
                return EVAL_BODY_INCLUDE;
            }
            
	    out.write("<a href=\"");
            String url = _action.getUrl();
            if (url != null) {
                if (_baseUrl != null) {
                    out.write(_baseUrl);
                }
                out.write( url );
            }
            else {
                /*
                 * this won't work, since the value of the request URI is
                 * the JSP-Page, not the original servlet request.
                 */
                //url = ((HttpServletRequest)pageContext.getRequest()).getRequestURI();
                url=(String)pageContext.getRequest().getAttribute(CONTEXT_URI);
                out.write( url );
            }
            Iterator it = _action.getParameterNames();
            if (it.hasNext()) {
                out.write("?");
            }
            while (it.hasNext()) {
                String pname = (String) it.next();
                if (pname == null) continue;
                out.write(pname);
                out.write("=");
                String val = _action.getParameter(pname);
                if (val != null) {
                    out.write(URLEncoder.encode(val));
                }
                if (it.hasNext()) {
                    out.write("&"); // should be &amp;, but dunno, if all
                                    // old browsers will understand it..
                }
            }
            out.write("\"");
            writeInputFieldAttributes(out);
            out.write(">");
        }
        catch (IOException e) {
            throw new JspTagException("InputTag: " + e.getMessage());
        }
	return EVAL_BODY_INCLUDE;
    }
    
    public int doAfterBody() throws JspTagException {
        JspWriter out = pageContext.getOut();
        try {
            if (_commentedOut) {
                out.write("] -->");
            }
            if (_action != null) {
                out.write("</a>");
            }
            else if (_hasSpan) {
                out.write("</span>");
            }
        }
        catch (IOException e) {
            throw new JspTagException(e.getMessage());
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

    protected void writeInputFieldAttributes(JspWriter out) 
        throws IOException {
        if (getCssClass() != null) {
            out.write(" class=\"");
            out.write( getCssClass() );
            out.write("\"");
        }
        if (getOnClick() != null) {
            out.write(" onClick=\"");
            out.write( getOnClick() );
            out.write("\"");
        }
        if (getTarget() != null) {
            out.write(" target=\"");
            out.write( getTarget() );
            out.write("\"");
        }
        if (getId() != null) {
            out.write(" id=\"");
            out.write( getId() );
            out.write("\"");
        }
    }

    /**
     * optional java-script on click function.
     */
    public void setOnClick(String onClick) {
        _onClick = onClick;
    }
    public String getOnClick() {
        return _onClick;
    }

    /*
     * Target of the link.
     */
    public void setTarget(String target) {
        _target = target;
    }
    public String getTarget() {
        return _target;
    }

    /*
     * BaseUrl
     */
    public void setBaseUrl(String baseUrl) {
        _baseUrl = baseUrl;
    }
    public String getBaseUrl() {
        return _baseUrl;
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

    public String getId() {
        return _id;
    }
    public void setId(String id) {
        _id = id;
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
