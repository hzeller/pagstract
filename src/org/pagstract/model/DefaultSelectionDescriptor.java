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
 * A Default implementation of a SelectionDescriptor.
 * @author Henner Zeller
 */
public class DefaultSelectionDescriptor implements SelectionDescriptor {
    private final String _value;
    private final String _label;
    
    public DefaultSelectionDescriptor(String value, String label) {
	_value = value;
	_label = label;
    }
    public DefaultSelectionDescriptor(String value) {
	this(value, value);
    }
    
    //-- interface SelectionDescriptor:
    public String getValue() { return _value; }
    public String getLabel() { return _label; }
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
