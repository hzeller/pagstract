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
package org.pagstract.view.template.parser.scanner;

public class SimpleTemplateToken {
    private final FilePosition _position;
    private final String _value;

    public SimpleTemplateToken(FilePosition pos, String value) {
        _position = pos;
        _value = value;
    }

    public String getValue() {
        return _value;
    }

    public FilePosition getFilePosition() {
        return _position;
    }
}
