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

/**
 * An OutputStream, that writes to an Device.
 *
 * @author <a href="mailto:H.Zeller@acm.org">Henner Zeller</a>
 * @version $Revision$
 */
public final class DeviceOutputStream extends OutputStream {
    final Device sink;

    public DeviceOutputStream(Device d) {
	sink = d;
    }
    
    public void close() {}
    public void flush() throws IOException {
	sink.flush();
    }
    public void write(byte b[],
			    int off,
			    int len) throws IOException {
	sink.write (b, off, len);
    }
    public void write(byte b[]) throws IOException {
	sink.write (b);
    }
    public void write(int b) throws IOException {
	sink.write (b);
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
