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
package org.pagstract.model;

import java.util.Map;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;

/**
 * Model for any kind of text fields. The actual rendering as
 * Textfield or TextArea is left to the renderer.
 *
 * @author Henner Zeller
 */
public class TextFieldModel 
    implements ComponentModel, SingleValueModel, AttributeSet
{
    private String  _value;
    private boolean _enabled;
    private boolean _visible;
    private Map/*<String,String>*/ _attributes;

    public TextFieldModel() {
        this(null);
    }
    
    public TextFieldModel(String value) {
        _value = value;
        _visible = true;
        _enabled = true;
    }

    /**
     * set the text of this model.
     */
    public void setText(String val) {
	_value = val;
    }

    public String getText() {
	return _value;
    }

    public void setEnabled(boolean e) { 
        _enabled = e; 
    }
    public void setVisible(boolean v) { 
        _visible = v; 
    }
    public void addAttribute(String name, String value) {
        if (_attributes == null) {
            _attributes = new HashMap();
        }
        _attributes.put(name, value);
    }

    //-- interface ComponentModel 
    public boolean isEnabled() { 
       return _enabled; 
    }

    public boolean isVisible() { 
        return _visible; 
    }

    //-- interface SingleValueModel
    public String getValue() { return getText(); }
    public void   setValue(String s) { setText(s); }

    //-- interface AttributeSet
    public Iterator/*<String>*/ getAttributeNames() {
        if (_attributes == null) return Collections.EMPTY_LIST.iterator();
        return _attributes.keySet().iterator();
    }

    public String getAttribute(String name) {
        if (_attributes == null) return null;
        return (String) _attributes.get(name);
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
