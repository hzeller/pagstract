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
package org.pagstract.view.template;

import java.awt.Color;
import java.io.IOException;

import org.pagstract.io.Device;

/**
 * Utility functions for rendering.
 * @version $Revision$
 */
public final class Utils {
    // fast conversion: translates directly into bytes (good for OutputStreams)
    private final static byte[] digits      = "0123456789ABCDEF".getBytes();

    // byte representation of special characters
    private final static byte HASH_CHAR      = (byte) '#';
    private final static byte MINUS_CHAR     = (byte) '-';
    private final static byte SPACE          = (byte) ' ';
    private final static byte[] EQUALS_QUOT  = "=\"".getBytes();
    private final static byte QUOT           = (byte) '"';

    /**
     * This is just a collection of static functions, thus not instanciable
     */
    private Utils() {}

    /**
     * writes an {X|HT}ML quoted string according to RFC 1866.
     * '"', '<', '>', '&'  become '&quot;', '&lt;', '&gt;', '&amp;'
     */
    // not optimized yet
    public static void quote(Device d, String s) throws IOException {
	if (s == null) return;
        char[] chars = s.toCharArray();
	char c;
        int last = 0;
	for (int pos = 0; pos < chars.length; ++pos) {
            c = chars[pos];
            // write special characters as code ..
            if (c < 32 || c > 127) {
                d.print(chars, last, (pos-last));
                d.print("&#");
                d.print((int) c);
                d.print(";");
                last = pos+1;
            }
	    else {
                /*
                 * RFC 1866 encoding
                 */
                switch (c) {
                case '&':
                    d.print(chars, last, (pos-last));
                    d.print("&amp;");
                    last = pos+1;
                    break;
                case '"':
                    d.print(chars, last, (pos-last));
                    d.print("&quot;");
                    last = pos+1;
                    break;
                case '<':
                    d.print(chars, last, (pos-last));
                    d.print("&lt;");
                    last = pos+1;
                    break;
                case '>':
                    d.print(chars, last, (pos-last));
                    d.print("&gt;");
                    last = pos+1;
                    break;
                }
            }
	}
        d.print(chars, last, chars.length-last);
    }

    public static void writeRaw(Device d, String s) throws IOException {
        d.print(s);
    }

    /**
     * writes the given String to the device. The string is quoted, i.e.
     * for all special characters in *ML, their appropriate entity is
     * returned.
     * If the String starts with '<html>', the content is regarded being
     * HTML-code and is written as is (without the <html> tag).
     */
    public static void write(Device d, String s) throws IOException {
        if (s == null) return;
        if ((s.length() > 5) && (s.startsWith("<html>"))) {
            writeRaw(d, s.substring(6));
        }
        else {
            quote(d, s);
        }
    }

    /**
     * writes the given integer to the device. Speed optimized; character
     * conversion avoided.
     */
    public static void write(Device d, int num) throws IOException {
	int i = 10;
	byte [] out = new byte[10];

	if (num < 0) {
	    d.write( MINUS_CHAR );
	    num = -(num);
	    if (num < 0) {
		/*
		 * still negative ? Then we had Integer.MIN_VALUE
		 */
		out[--i] = digits[ - (Integer.MIN_VALUE % 10) ];
		num = - (Integer.MIN_VALUE / 10);
	    }
	}
	do {
	    out[--i] = digits[num % 10];
	    num /= 10;
	}
	while (num > 0);
	d.write(out, i, 10-i);
    }

    /**
     * writes the given long integer to the device. Speed optimized; character
     * conversion avoided.
     */
    public static void write(Device d, long num) throws IOException {
	int i = 20;
	byte [] out = new byte[20];

	if (num < 0) {
	    d.write( MINUS_CHAR );
	    num = -(num);
	    if (num < 0) {
		/*
		 * still negative ? Then we had Long.MIN_VALUE
		 */
		out[--i] = digits[ - (int) (Long.MIN_VALUE % 10) ];
		num = - (Long.MIN_VALUE / 10);
	    }
	}
	do {
	    out[--i] = digits[(int) (num % 10) ];
	    num /= 10;
	}
	while (num > 0);
	d.write(out, i, 20-i);
    }

    /**
     * writes the given java.awt.Color to the device. Speed optimized;
     * character conversion avoided.
     */
    public static void write(Device d, Color c) throws IOException {
	d.write( HASH_CHAR );
	int rgb = (c == null) ? 0 : c.getRGB();
	int mask = 0xf00000;
	for (int bitPos=20; bitPos >= 0; bitPos -= 4) {
            d.write(digits[(rgb & mask) >>> bitPos]);
            mask >>>= 4;
	}
    }

    /*
     * testing purposes.
     */
    public static void main(String argv[]) throws Exception {
	Color c = new Color(255, 254, 7);
	Device d = new org.pagstract.io.StringBufferDevice();
	write(d, c);
	quote(d, "\nThis is a <abc> string \"; foo & sons\nmoin");
	write(d, -42);
        write(d, Integer.MIN_VALUE);

        write(d, "hello test&nbsp;\n");
        write(d, "<html>hallo test&nbsp;\n");
	System.out.println (d.toString());

        org.pagstract.io.NullDevice nd;
        nd = new org.pagstract.io.NullDevice();
        final long loops = 1000000;
        long start = System.currentTimeMillis();
        for (int i=0; i < loops; ++i) {
            quote(nd, "this is <a> little & foo");
        }
        System.err.println(loops + " testloops took: "
                           + (System.currentTimeMillis() - start)
                           + "ms.");
        System.err.println("Generated " + nd.getSize()/1024
                           + " kBytes in this time.");
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
