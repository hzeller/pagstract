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

import java.io.IOException;
import java.io.InputStream;

/**
 * The PositionInputStream records the rows and columns. 
 * This is inaccurate for Input streams that represent multi-byte
 * characters. FIXME: create solution with Reader.
 *
 * @author Henner Zeller
 * @version $Revision$ $Date$
 */
public class PositionInputStream extends InputStream {
    protected final InputStream _in;

    protected int _col;
    protected int _row;
    private   int _lastLineChar;
    
    protected StreamPosition _save;
    private   int _saveLastLineChar;

    protected PositionInputStream(InputStream other) {
        _in = other;
        _col = 0;
        _row = 1;
    }
    
    /* ---- PositionReader ---- */
    public StreamPosition getStreamPosition () {
        return new StreamPosition(_row, _col);
    }
    
    public StreamPosition getMarkedPosition() {
        return _save;
    }

    /* ---- Implementation of InputStream ---- */

    public int read ()
        throws IOException {
        int c = _in.read();
        if (c == -1) {
            return c;
        }
        /*
         * Read all known types of line-endings.
         * Unix with '\n', Mac with '\r' and Dos with '\r\n'
         * Note, that multiple line-endings are in these
         * cases '\n\n\n', '\r\r\r' or '\r\n\r\n\r\n', all
         * of which must be counted as three lines.
         */
        if (c == '\n' || c == '\r') {
            if (c != '\n' || _lastLineChar != '\r') {
                ++_row;
            }
            _lastLineChar = c;
            _col = 0;
        }
        else {
            ++_col;
            _lastLineChar = '\0';
        }

        return c;
    }

    public boolean markSupported () {
        return _in.markSupported();
    }

    public void mark (int readAheadLimit) {
        _save = getStreamPosition();
        _saveLastLineChar = _lastLineChar;
        _in.mark(readAheadLimit);
    }

    public void reset ()
        throws IOException {
        _in.reset();
        // this should be noticed by the InputStream above
        if (_save == null) {
            throw new IOException ("mark() not called before");
        }
        _row = _save.getRow();
        _col = _save.getColumn();
        _lastLineChar = _saveLastLineChar;
        _save = null;
    }

    public void close ()
        throws IOException {
        _in.close();
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
