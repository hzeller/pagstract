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
 * An InputStream that Records the bytes read.
 */
public class RecordingInputStream extends InputStream {
    private final InputStream _in;

    private byte[] _buffer;
    private int _pos;
    private int _markPos;

    public RecordingInputStream(InputStream input) {
        _in = input;
        _buffer = new byte[64];
        resetRecording();
    }
    
    /**
     * Returns the Position this stream had at the last {@link mark()}-Operation
     * or '-1' if there was no mark.
     */
    public int getMarkedPosition() {
        return _markPos;
    }
    
    public int getCurrentPosition() {
        return _pos;
    }

    /**
     * reset recording. The next call to {@link getBuffer()} will return
     * bytes recorded after this call.
     */
    public void resetRecording() {
        _pos = 0;
        _markPos = -1;
    }

    public byte[] getBuffer(int fromPos) {
        return getBuffer(fromPos, _pos);
    }

    public byte[] getBuffer(int fromPos, int toPos) {
        if (fromPos < 0 || fromPos > toPos || toPos > _pos) {
            throw new IllegalArgumentException("invalid position.." 
                                               + fromPos + ".." + toPos);
        }
        byte[] result = new byte[ toPos - fromPos ];
        System.arraycopy(_buffer, fromPos, result, 0, toPos - fromPos);
        return result;
    }

    public byte[] getBuffer() {
        return getBuffer(0, _pos);
    }

    public int read() throws IOException {
        int c = _in.read();
        if (c >= 0) {
            if (_pos >= _buffer.length) {
                byte realloced[] = new byte[ _buffer.length * 2 ];
                System.arraycopy(_buffer, 0, realloced, 0, _buffer.length);
                _buffer = realloced;
            }
            _buffer[_pos++] = (byte) (c & 0xff);
        }
        return c;
    }

    public boolean markSupported () {
        return _in.markSupported();
    }

    public void mark (int readAheadLimit) {
        _markPos = _pos;
        _in.mark(readAheadLimit);
    }

    public void reset ()
        throws IOException {
        _in.reset();
        // this should be noticed by the InputStream above
        if (_markPos < 0) {
            throw new IOException ("mark() not called before");
        }
        _pos = _markPos;
        _markPos = -1;
    }

    public void close() throws IOException {
        _in.close();
    }
}
