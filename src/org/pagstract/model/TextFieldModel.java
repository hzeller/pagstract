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

/**
 * Model for any kind of text fields. The actual rendering as
 * Textfield or TextArea is left to the renderer.
 *
 * @author Henner Zeller
 */
public class TextFieldModel implements ComponentModel, SingleValueModel 
{
    private String  _value;
    private boolean _enabled;
    private boolean _visible;

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
