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


/**
 * Device, that discards everything. For debugging purposes.
 * Counts the number of bytes written (not exactly, since for print() methods,
 * it counts the number of characters, that might not be the same as
 * bytes).
 *
 * @author <a href="mailto:H.Zeller@acm.org">Henner Zeller</a>
 * @version $Revision$
 */
public final class NullDevice implements Device
{
    private long byteCount;

    public NullDevice() {
        byteCount = 0;
    }

    public boolean isSizePreserving() { return true; }

    /**
     * Flush this Device.
     */
    public void flush () { }
    public void close() { }
    
    /**
     * returns the number of bytes written to this data sink.
     */
    public long getSize() { return byteCount; }

    /**
     * reset the number of bytes to zero.
     */
    public void resetSize() { byteCount = 0; }

    /**
     * Print a character.
     */
    public Device print (char c) { ++byteCount; return this; }

    /**
     * Print a character array.
     */
    public Device print (char[] c) { 
        if (c != null) byteCount += c.length;
        return this; 
    }

    /**
     * Print len characters from the specified char array starting at offset
     * off to this Device.
     */
    public Device print (char[] c, int start, int len) { 
        byteCount += len;
        return this; 
    }

    //-- print basic objects --

    /**
     * Print a String.
     */
    public Device print (String s) { 
        if (s != null) byteCount += s.length();
        return this; 
    }

    /**
     * Print an integer.
     */
    public Device print (int i) { 
        byteCount += String.valueOf(i).length();
        return this; 
    }

    /**
     * Print any Object
     */
    public Device print (Object o) { 
        if (o != null) byteCount += o.toString().length();
        return this; 
    }

    /*-------------*
     ** Methods which write raw bytes to the Device. Much like an OutputStream.
     **-------------*/

    /**
     * Writes the specified byte to this data output stream.
     */
    public Device write (int c) { 
        ++byteCount;
        return this; 
    }

    /**
     * Writes b.length bytes from the specified byte array to this
     * output stream.
     */
    public Device write(byte b[]) { 
        if (b != null) byteCount += b.length;
        return this; 
    }

    /**
     * Writes len bytes from the specified byte array starting at offset
     * off to this Device.
     */
    public Device write(byte b[], int off, int len) {
        if (b != null) byteCount += len;
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
