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
 * A Token Matcher that works on an InputStream.
 */
public class StreamTokenMatcher {
    /**
     * The pattern we match..
     */
    private final TokenMatchPattern _pattern;
    private final InputStream _stream;

    public StreamTokenMatcher(TokenMatchPattern pattern, InputStream stream) {
        _pattern = pattern;
        _stream = stream;
    }


    /**
     * Determine the next Token in the stream. Do not mark the position where
     * the token begins.
     *
     * @return the next token number or '-1' on EOF.
     */    
    public int nextToken() throws IOException {
        return nextToken(-1);
    }

    /**
     * Determine the next Token in the stream. Mark the position in the
     * stream where the token starts. The limit up to how many characters
     * should be buffered can be given in this case.
     *
     * @return the next token number or '-1' on EOF.
     */
    public int nextToken(int readAheadMark) throws IOException {
        // local variable access is faster..
        short transStates[/*state*/][/*input-char*/] = _pattern.getStates();
        InputStream in = _stream;

        int state = TokenMatchPattern.INIT_STATE;
        int lastLineChar;
        int ch;
        while ((ch = in.read()) > 0) {
            boolean previousWasInit = (state == TokenMatchPattern.INIT_STATE);
            state = transStates[state][ch];
            if (state < 0) {
                return -state - 1;
            }
            if (readAheadMark > 0 
                && previousWasInit 
                && state != TokenMatchPattern.INIT_STATE) {
                in.mark(readAheadMark);
            }
        }
        return -1;
    }

    public final static void main(String argv[]) throws IOException {
        String tokens[] = {"<pma:list ", "<pma:äöü", "<input ", "<a " };
        TokenMatchPattern pattern = TokenMatchPattern.compile(tokens);

        String testString = "hallo<pma:list <inputblu<a >b hallo<pma:äöüxyz";
        PositionInputStream in = new PositionInputStream(new java.io.ByteArrayInputStream(testString.getBytes()));
        
        System.err.println("searching tokens in " + testString);
        StreamTokenMatcher matcher = new StreamTokenMatcher(pattern, in);
        int tok;
        while ((tok = matcher.nextToken(64)) >= 0) {
            System.err.println("found match: " + tokens[tok]
                               + in.getMarkedPosition()
                               + " " + in.getStreamPosition());
        }
    }
}
