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
package org.pagstract.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A Device encapsulating a StringBuffer
 *
 * @author Henner Zeller
 * @version $Revision$
 */
public final class StringBufferDevice implements Device
{
    private StringBuffer buffer;
    private ByteArrayOutputStream byteStream = null;

    public StringBufferDevice () {
        buffer = new StringBuffer();
    }

    public String toString() {
        flush();
        return buffer.toString();
    }

    public boolean isSizePreserving() { return true; }

    /**
     * Flush this Stream.
     */
    public void flush() {
        if (byteStream != null) {
            buffer.append (byteStream.toString());
            byteStream = null;
        }
    }

    public void close() { 
        flush();
    }

    public void reset() {
        flush();
        buffer.setLength(0);
    }

    private OutputStream getStream() {
        if (byteStream != null)
            return byteStream;
        byteStream = new ByteArrayOutputStream();
        return byteStream;
    }

    /**
     * Print a String.
     */
    public Device print (String s) {
        if (byteStream != null) flush();
        buffer.append (s);
        return this;
    }

    /**
     * Print a character.
     */
    public Device print (char c) {
        if (byteStream != null) flush();
        buffer.append (c);
        return this;
    }

    /**
     * Print a character array.
     */
    public Device print (char[] c) throws IOException {
        if (byteStream != null) flush();
        buffer.append (c);
        return this;
    }

    /**
     * Print a character array.
     */
    public Device print (char[] c, int start, int len) throws IOException {
        if (byteStream != null) flush();
        buffer.append(c, start, len);
        return this;
    }

    /**
     * Print an integer.
     */
    public Device print (int i) {
        if (byteStream != null) flush();
        buffer.append (i);
        return this;
    }

    /**
     * Print any Object
     */
    public Device print (Object o) {
        if (byteStream != null) flush();
        buffer.append (o);
        return this;
    }

    /**
     * Writes the specified byte to this data output stream.
     */
    public Device write (int c) throws IOException {
        getStream().write (c);
        return this;
    }

    /**
     * Writes b.length bytes from the specified byte array to this
     * output stream.
     */
    public Device write(byte b[]) throws IOException {
        getStream().write (b);
        return this;
    }

    /**
     * Writes len bytes from the specified byte array starting at offset
     * off to this output stream.
     */
    public Device write(byte b[], int off, int len) throws IOException {
        getStream().write (b, off, len);
        return this;
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
