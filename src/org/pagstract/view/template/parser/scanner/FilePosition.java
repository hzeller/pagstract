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

/**
 * Represents a Position in a File. This will be really implemented,
 * when the Scanner is working correctly.
 */
public class FilePosition {
    private final String _filename;
    private final StreamPosition _position;

    public FilePosition(String filename, StreamPosition pos) {
        _filename = filename;
        _position = pos;
    }

    public String getFilename() {
        return _filename;
    }

    public StreamPosition getPosition() {
        return _position;
    }

    public String toString() {
        return getFilename() + ":" + getPosition();
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
