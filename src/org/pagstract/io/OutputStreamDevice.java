/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
 *
 * This file is part of wingS (http://wings.mercatis.de).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package org.pagstract.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A Device encapsulating a ServletOutputStream
 *
 * @author <a href="mailto:hengels@to.com">Holger Engels</a>
 * @version $Revision$
 */
public final class OutputStreamDevice implements Device
{
    private PrintStream out;

    /**
     * Creates an Device around and outputStream.
     */
    public OutputStreamDevice(OutputStream out) {
        this.out = new PrintStream(out);
    }

    public boolean isSizePreserving() { return true; }

    /**
     * Flush this Stream.
     */
    public void flush () throws IOException {
        out.flush();
    }

    public void close() throws IOException { 
        out.close(); 
    }

    /**
     * Print a String.
     */
    public Device print (String s)  throws IOException {
        if (s == null)
            out.print ("null");
        else
            out.print (s);
        return this;
    }

    /**
     * Print an integer.
     */
    public Device print (int i)    throws IOException {
        out.print (i);
        return this;
    }

    /**
     * Print any Object
     */
    public Device print (Object o) throws IOException {
        if (o == null)
            out.print ("null");
        else
            out.print (o.toString());
        return this;
    }

    /**
     * Print a character.
     */
    public Device print (char c) throws IOException {
        out.print(c);
        return this;
    }

    /**
     * Print an array of chars.
     */
    public Device print (char[] c) throws IOException {
        return print(c, 0, c.length-1);
    }

    /**
     * Print a character array.
     */
    public Device print (char[] c, int start, int len) throws IOException {
        final int end = start+len;
        for (int i=start; i < end; ++i)
            out.print(c[i]);
        return this;
    }

    /**
     * Writes the specified byte to this data output stream.
     */
    public Device write (int c) throws IOException {
        out.write (c);
        return this;
    }

    /**
     * Writes b.length bytes from the specified byte array to this
     * output stream.
     */
    public Device write(byte b[]) throws IOException {
        out.write (b);
        return this;
    }

    /**
     * Writes len bytes from the specified byte array starting at offset
     * off to this output stream.
     */
    public Device write(byte b[], int off, int len) throws IOException {
        out.write (b, off, len);
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
