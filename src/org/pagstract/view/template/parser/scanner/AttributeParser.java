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
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

/**
 * some hacky utility class. A parser for Attributes.
 */
public class AttributeParser {
    private final static char singleQuote = '\'';
    private final static char doubleQuote = '\"';
    private final Map _attr;

    /**
     * Creates an Attribute-parser that stores the attributes
     * found in the given map.
     */
    public AttributeParser(Map attributes)
        throws IOException {
	_attr = attributes;
    }
    
    public String readAttributes(String str) 
        throws IOException 
    {
        return readAttributes(new StringReader(str));
    }

    /**
     * @return ErrorMessage or null if everything is ok..
     */
    public String readAttributes(Reader input) 
        throws IOException 
    {
        String key = null, token;
        for (;;) {
            // check for valid value tag (or end delimiter)
            if (key == null)
                key = nextToken(input, true);
            
            // 'key'-part 
            if (key == null 
                || isDelimiter(key.charAt(0))
                || key.charAt(0) == doubleQuote
                || key.charAt(0) == singleQuote)
                return null;
            
            // now insure that we have an equals sign
            token = nextToken(input, true);
            if (token == null || token.charAt(0) != '=') {
                // if we don't have an equals sign, then this is the
                // next attribute.
                //_attr.put(key, null);
                key = token;
                continue;
            }
            
            // read value of tag
            token = nextToken(input, true);
            if (token == null || isDelimiter(token.charAt(0)))
                return null;
            
            // strip quotes
            if (token.charAt(0) == doubleQuote 
                || token.charAt(0) == singleQuote) {
                token = token.substring(1, token.length() - 1);
            }

            if (_attr.containsKey(key)) {
                return "got attribute twice: '" + key + "'";
            }
            _attr.put(key, token);
            key = null;
        }
    }

    /**
     * Read next token from string.
     * A token is a space-delimited word, a string in quotes
     * (returned with quotes), a delimiter such as a greater-than,
     * less-than, or equals sign.
     * Quotes marks inside quoted strings may be escaped with a
     * backslash (\) character.
     * @param string string begin parsed
     * @param index location within string to start examining
     * @return next token, or null if whitespace was encountered
     */
    public String nextToken(Reader input, boolean skipWhitespaces) 
	throws IOException {
	StringBuffer token = new StringBuffer();

	if (skipWhitespaces) 
	    skipWhiteSpace (input);
      
	input.mark(1);
	int c = input.read();

	if (c == -1) { return null; }

	// quoted string? (handle both single and double)
	if (c == doubleQuote || c == singleQuote) {
	    boolean inSingle = false;
	    boolean inDouble = false;
	    if (c == singleQuote) inSingle = true; else inDouble = true;
	    token.append ((char) c);
	    do {
		c = input.read();
		if (c == -1) {
		    String reportString = token.toString();
		    if (reportString.length() > 30) {
			reportString = reportString.substring(0, 30) + 
			    " (truncated, length is " + reportString.length() + ")";
		    }
		    throw new IOException ("EOF in String: " +  reportString);
		}
		if (c == '\\') {
		    int quoted = input.read();
		    if (quoted >= 0) token.append ((char) quoted);
		}
		else 
		    token.append((char) c);
	    } while ((inDouble && c != doubleQuote)  ||
                     (inSingle && c != singleQuote));
	}

	// parameter delimiter? read just one
	else if (isDelimiter((char) c)) { 
	    token.append((char) c);
	}
        
	// Inserted for token "-->".
	// Like a word token, but includes the delimiter ">".
	else if (c == '-') {
	    do { 
		token.append((char) c); 
		input.mark(1);
		c = input.read(); 
	    } 
	    while (c >= 0 && 
		   !Character.isWhitespace((char) c) && 
		   !isDelimiter((char) c));
	    input.reset();
	    token.append((char) input.read());
	}

	// If we did not skip Whitespaces but actually got one
	// this token is empty.
	else if (!skipWhitespaces && 
		 Character.isWhitespace((char) c)) {
	    input.reset();
	    return null;
	}
	
	// word token or />
	else {
	    do { 
		token.append((char) c); 
		input.mark(1);
		c = input.read(); 
	    } 
	    while (c >= 0 && 
		   !Character.isWhitespace((char) c) && 
		   !isDelimiter((char) c));
	    if (token.length() == 1 && token.charAt(0) == '/')
		token.append((char)c);
	    else
		input.reset();
	}
	return token.toString();
    }

    /**
     * Decide whether character is SGML delimiter or equals.
     * @param c character in question
     * @deprecated this is an internal function an should not be used
     *             and may be private some time
     * @return true if character is an SGML delimiter
     */
    private boolean isDelimiter(char c) {
        return c == '<' || c == '=' || c == '>';
    }

    private int skipWhiteSpace(Reader r) 
	throws IOException {
	int c, len=0;
	do {
	    r.mark(1);
	    c = r.read();
	    len++;
	} 
	while (c >= 0 && Character.isWhitespace((char) c));
	r.reset();
	return len - 1;
    }

}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
