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

/**
 * A general interface for a Output-Device.
 * A Device is the destination, where the HTML-code is written to. This is
 * like the 'Graphics' - device in GUI applications.
 *
 * <p>All the printing methods return the Device itself, to allow simple
 * chaining:
 * <hr /><pre>
 *    someDevice.print("foo").print("bar");
 * </pre><hr />
 *
 * <p>Usually, the underlying data sink of a Device would be some OutputStream,
 * as it finally writes through some socket to the client browser.
 * The Device, however, offers basically two methods for writing to the 
 * output: with print() and write() like methods. The print() like 
 * methods get character input that has to be converted to a byte-stream
 * before it actually can be written to the underlying OutputStream, while 
 * the write() like methods directly handle arrays of bytes to do this. So 
 * if possible, try to always use the write() methods, if you can 
 * pre-calculate the byte-representation of the output (if you have some
 * static Strings, for instance, consider using String.getBytes()).
 *
 * @author <a href="mailto:H.Zeller@acm.org">Henner Zeller</a>
 * @version $Revision$
 */
public interface Device
{
    /**
     * Flush this Device.
     */
    void flush () throws IOException;
    
    /**
     * close the Device.
     */
    void close() throws IOException;

    /**
     * returns, whether this the size of data put into this device is
     * the same as comes out. This is necessary to know if we want to send the
     * content size: if we know the content size, but this device changes
     * the size, we must not send it.
     *
     * @return 'true', if this device leaves the size of the data going through
     *         it, untouched. This is usually true.
     */
    boolean isSizePreserving();

    // ------------*
    // Methods which deal with characers using the platform's
    // default character encoding to convert characters into bytes.
    // much like a PrintWriter
    // ------------*/
    
    // -- print basic characters --

    /**
     * Print a character.
     */
    Device print (char c)   throws IOException;

    /**
     * Print a character array.
     */
    Device print (char[] c) throws IOException;

    /**
     * Print len characters from the specified char array starting at offset
     * off to this Device.
     */
    Device print (char[] c, int start, int len) throws IOException;

    //-- print basic objects --

    /**
     * Print a String.
     */
    Device print (String s) throws IOException;

    /**
     * Print an integer.
     */
    Device print (int i)    throws IOException;

    /**
     * Print any Object
     */
    Device print (Object o) throws IOException;

    /*-------------*
     ** Methods which write raw bytes to the Device. Much like an OutputStream.
     **-------------*/

    /**
     * Writes the specified byte to this data output stream.
     */
    Device write (int c) throws IOException;

    /**
     * Writes b.length bytes from the specified byte array to this
     * output stream.
     */
    Device write(byte b[]) throws IOException;

    /**
     * Writes len bytes from the specified byte array starting at offset
     * off to this Device.
     */
    Device write(byte b[], int off, int len) throws IOException;
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
