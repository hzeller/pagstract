/*
 * $Id$
 * (c) Copyright 2001 wingS development team.
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
import java.util.zip.GZIPOutputStream;

/**
 * A Device encapsulating an OutputStream, compressing its
 * output. You can use this, if the browser states, that it can handle
 * compressed data; don't forget to set the appropriate header, then
 * (Content-Encoding).
 *
 * @author <a href="mailto:H.Zeller@acm.org">Henner Zeller</a>
 * @version $Revision$
 */
public final class GZIPCompressingDevice implements Device {
    private final OutputStreamDevice deligee;

    public GZIPCompressingDevice (OutputStream out) throws IOException {
	deligee = new OutputStreamDevice(new GZIPOutputStream(out));
    }

    public GZIPCompressingDevice(Device d) throws IOException {
        this(new DeviceOutputStream(d));
    }

    /**
     * this returns false, since the compressing device is <em>not</em> size
     * preserving. Thats what is all about, right ?
     */
    public boolean isSizePreserving() { return false; }

    // rest is just delegating..
    public void flush () throws IOException {
	deligee.flush();
    }
    public void close() throws IOException {
	deligee.close();
    }
    public Device print (char c)   throws IOException { 
	deligee.print(c); return this;
    }
    public Device print (char[] c) throws IOException { 
	deligee.print(c); return this;
    }
    public Device print (char[] c, int start, int len) throws IOException {
	deligee.print(c, start, len); return this;
    }
    public Device print (String s) throws IOException {
	deligee.print(s); return this;
    }
    public Device print (int i)    throws IOException {
	deligee.print(i); return this;
    }
    public Device print (Object o) throws IOException {
	deligee.print(o); return this;
    }
    public Device write (int c) throws IOException {
	deligee.write(c); return this;
    }
    public Device write(byte b[]) throws IOException {
	deligee.write(b); return this;
    }
    public Device write(byte b[], int off, int len) throws IOException {
	deligee.write(b, off, len); return this;
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
