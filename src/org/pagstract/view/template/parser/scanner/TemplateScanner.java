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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;

import org.pagstract.view.template.parser.sym;

/**
 * The Scanner that delivers the tokens read from the file.
 * <p>
 * Interesting with required pma:name attribute: &lt;pma:value&gt;, &lt;pma:list&gt;,
 * &lt;pma:bean&gt;, &lt;pma:switch&gt;, &lt;pma:form&gt;
 * <p>
 * Potential tags that may contain a pma:name attribute: &lt;a&gt;, &lt;input&gt;,
 * &lt;select&gt;, &lt;textarea&gt;
 * <p>
 * Tags, that may contain a pma:case attribute: &lt;object&gt;
 * <p>
 * Unless the tag contains a 'magic' attribute like pma:name or pma:case,
 * every attribute may contain itself a special Pagstract Dollar 
 * expansion variable
 * ${variable} that is expande like &lt;pma:value pma:name="variable"/&gt;
 * Example:
 * <pre>
 * &lt;img src="${imgurl}"&gt;
 * </pre>
 */
public class TemplateScanner implements Scanner {
    private final static boolean EXPAND_HTML_COMMENTS = true;
    private final static int MAX_TAGATTRIBUTE_AREA = 8192;

    private final static int START_COMMENT = 0;
    private final static int END_COMMENT   = 1;

    private final static BasicToken TOKENS[] = {
        //-- comment tokens first (see constants above)
        new BasicToken("<!--"),        new BasicToken("-->"),
        
        /* our tags. Note, that closing tags MUST follow their opening
         * counterparts (_nestedTag logic..). Opening tags MUST be
         * on even positions, the closing part on the following odd position.
         */
        new BasicToken("<pma:value ",  sym.ValueStart, sym.ValueSimple,
                       "pma:name"),
        new BasicToken("</pma:value>",  sym.ValueEnd),
        new BasicToken("<pma:list ",   sym.ListStart,  -1, "pma:name"),
        new BasicToken("</pma:list>",  sym.ListEnd),
        new BasicToken("<pma:bean ",   sym.BeanStart,  -1, "pma:name"),
        new BasicToken("</pma:bean>",  sym.BeanEnd),
        new BasicToken("<pma:switch ", sym.SwitchStart, -1, "pma:name"),
        new BasicToken("</pma:switch>",sym.SwitchEnd),
        new BasicToken("<pma:form ",   sym.FormStart, -1, "pma:name"),
        new BasicToken("</pma:form>",  sym.FormEnd),
        new BasicToken("<pma:tile ",  sym.TileStart,sym.TileSimple, null),
        new BasicToken("</pma:tile>",  sym.TileEnd),
        new BasicToken("<a ",          sym.LinkStart, -1, "pma:name"),
        new BasicToken("</a>",         sym.LinkEnd),
        new BasicToken("<input ",      sym.InputStart, sym.InputSimple, 
                       "pma:name"),
        new BasicToken("</input>",     sym.InputEnd),
        new BasicToken("<select ",     
                       sym.SelectFieldStart, sym.SelectFieldSimple,"pma:name"),
        new BasicToken("</select>",    sym.SelectFieldEnd),
        /*
        new BasicToken("<textarea ",   sym.TextareaStart, sym.TextareaSimple, 
                       "pma:name"),
        new BasicToken("</textarea>",  sym.TextaraEnd),
        */
        new BasicToken("<object ",     sym.ObjectStart, -1, "pma:case"),
        new BasicToken("</object>",    sym.ObjectEnd),
        new BasicToken("<pma:header",  sym.ListHeaderStart, -1, null),
        new BasicToken("</pma:header>",sym.ListHeaderEnd),
        new BasicToken("<pma:content",  sym.ListContentStart, -1, null),
        new BasicToken("</pma:content>",sym.ListContentEnd),
        new BasicToken("<pma:footer",  sym.ListFooterStart, -1, null),
        new BasicToken("</pma:footer>",sym.ListFooterEnd),
        new BasicToken("<pma:separator",  sym.ListSeparatorStart, -1, null),
        new BasicToken("</pma:separator>",sym.ListSeparatorEnd),
        // FIXME: form missing.

        // these are handled special..
        new BasicToken("${",         sym.ValueSimple, true),
        new BasicToken("}",          -1, true)
    };

    private final static TokenMatchPattern MATCH_PATTERN;
    static {
        String[] keywords = new String[ TOKENS.length ];
        for (int i=0; i < TOKENS.length; ++i) {
            keywords[i] = TOKENS[i].getKeyword();
        }
        MATCH_PATTERN = TokenMatchPattern.compile(keywords);
    }

    /*
     * stacked input streams that allow to query different
     * aspects.
     */
    /**
     * the Position-InputStream allows to query the current position
     * in rows and columns.
     */
    private final PositionInputStream _positionStream;

    /**
     * The Recording InputStream records the text between tags
     * to be output as constant byte-array elements.
     */
    private final RecordingInputStream _recordingStream;

    /**
     * The Input Stream we do all reading from. It is the reference
     * to the top of the stack of input streams.
     */
    private final InputStream _in;

    private final StreamTokenMatcher _tokenMatcher;

    /**
     * The name of the resource we are parsing. This is to fill 
     * FilePositions with the correct resource name.
     */
    private final String _resourceName;

    /**
     * count nesting of tags to determine if a closing non-special
     * tag (like </a>) corresponds to an tokenized opening tag or if this
     * has been ignored because it did not contain a pma:name attribute.
     * And: report errors on closing </pma:*> tags that have not been
     * opened.
     */
    private final int _nestedTag[];

    /**
     * Calling next_token() usually generates two tokens: the
     * actual next token based on a tag and the content before
     * it as byte-array.
     * The next_token() then returns first the byte-array-content,
     * in the next call the determined token.
     * This is the temporary storage of the next determined
     * token.
     */
    private Symbol _nextSymbol;

    /**
     * create a template scanner from the input stream.
     *
     * @param in the InputStream to read the tokens from.
     */
    public TemplateScanner(InputStream in) {
        this(in, "<input-stream>");
    }

    /**
     * create a template scanner from the input stream with the
     * given resource name. The ResourceName ist something like the
     * filename and is part of the FilePositions.
     *
     * @param in the InputStream to read the tokens from.
     * @param resourceName the name of the resource for informational
     *                     purposes.
     */
    public TemplateScanner(InputStream in, String resourceName) {
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        _positionStream = new PositionInputStream(in);
        _recordingStream = new RecordingInputStream( _positionStream );
        _in = _recordingStream;
        _tokenMatcher = new StreamTokenMatcher(MATCH_PATTERN, _in);
        _recordingStream.resetRecording();
        _resourceName = resourceName;
        _nextSymbol = null;
        if (TOKENS.length % 2 != 0) { // assert would be cool here.
            throw new IllegalArgumentException("PAGSTRACT DEVELOPER: there must be an even number of tokens..");
        }
        _nestedTag = new int[ TOKENS.length / 2 ];
    }

    /**
     * return the current position of the parsing process. Might
     * be useful to determine the current position when parsing fails.
     */
    public FilePosition lastReachedPosition() {
        return new FilePosition(_resourceName, 
                                _positionStream.getStreamPosition());
    }

    private void debug(StreamPosition pos, String debugMsg) {
        /*
        System.err.println(new FilePosition(_resourceName, pos) 
                           + " : DEBUG: " + debugMsg);
        */
    }

    private void warning(StreamPosition pos, String warning) {
        System.err.println(new FilePosition(_resourceName, pos) +" : Warning: "
                           + warning);
    }

    private void error(StreamPosition pos, String error) {
        System.err.println(new FilePosition(_resourceName, pos) + " : Error: "
                         + error);
    }
    
    /**
     * Scanner implementation - return the next token.
     */
    public Symbol next_token() throws Exception {

        if (_nextSymbol != null) {
            // flush stored token..
            Symbol out = _nextSymbol;
            _nextSymbol = null;
            return out;
        }

        Map attributeMap = new HashMap();
        int recordedTagBeginPos = -1;
        do {
            attributeMap.clear();
            int tokenNum;
            
            if ( EXPAND_HTML_COMMENTS ) {
                do {
                    tokenNum = _tokenMatcher.nextToken( 256 );
                }
                while (tokenNum == START_COMMENT || tokenNum == END_COMMENT);
            }
            else {
                do { // skip any comments.
                    tokenNum = _tokenMatcher.nextToken( 256 );
                    if (tokenNum == START_COMMENT) {
                        debug(_positionStream.getMarkedPosition(), 
                              "ignore comment");
                        do {
                            tokenNum = _tokenMatcher.nextToken( 256 );
                        }
                        while (tokenNum >= 0 && tokenNum != END_COMMENT);
                    }
                }
                while (tokenNum == END_COMMENT);
            }

            if (tokenNum < 0) {
                recordedTagBeginPos = _recordingStream.getCurrentPosition()+1;
                _nextSymbol = new Symbol( sym.EOF );
                break;
            }
            
            BasicToken token = TOKENS[ tokenNum ];
            StreamPosition tagStartPos = _positionStream.getMarkedPosition();
            debug(tagStartPos, "position von " + token.getKeyword());

            recordedTagBeginPos = _recordingStream.getMarkedPosition();
            
            if (token.isStartToken() && token.isDollarExpansion()) {
                tokenNum = _tokenMatcher.nextToken();
                if (tokenNum == -1 
                    || !TOKENS[tokenNum].isDollarExpansion()
                    || TOKENS[tokenNum].isStartToken()) {
                    warning(tagStartPos, "dollar expansion not closed");
                    if (tokenNum >= 0) {
                        warning(_positionStream.getStreamPosition(),
                                "Found '" 
                                + TOKENS[tokenNum].getKeyword() 
                                + "' instead");
                    }
                    _in.reset();
                }
                else {
                    byte[] range = _recordingStream
                        .getBuffer(recordedTagBeginPos+1,
                                   _recordingStream.getCurrentPosition()-1);
                    attributeMap.put("pma:name", new String(range));
                    TemplateToken templateToken;
                    templateToken = 
                        new TemplateToken(new FilePosition(_resourceName,
                                                           tagStartPos),
                                          attributeMap);
                    _nextSymbol = new Symbol( sym.ValueSimple,templateToken );
                }
            }
            else {
                /*
                 * now check, if the tag has a pma:name attribute.
                 */
                _in.mark( MAX_TAGATTRIBUTE_AREA );
                if (token.isStartToken()) {
                    final String magicAttribute = token.getMagicAttribute();
                    final boolean closeTag = readAttributes(tagStartPos,
                                                            attributeMap);
                    final boolean magicAttributeFound
                        = ((magicAttribute == null) 
                           || attributeMap.containsKey(magicAttribute));
                    TemplateToken templateToken;
                    
                    int symbol = ((closeTag)
                                  ? token.getClosedSymbol()
                                  : token.getSymbol());
                    if (symbol == -1) {
                        // <pma:bean />
                        error(tagStartPos, "not a closing token for "
                              + token.getKeyword() + " allowed");
                        _in.reset(); // start right after tag..
                    }
                    else if (magicAttributeFound || token.isPmaSpecial()) {
                        if (token.isPmaSpecial() && !magicAttributeFound) {
                            error(tagStartPos, 
                                  "tag '" + token.getKeyword()
                                  + "' does not have required " 
                                  + magicAttribute
                                  + " attribute");
                        }
                        templateToken = 
                            new TemplateToken(new FilePosition(_resourceName,
                                                               tagStartPos),
                                              attributeMap);
                        _nextSymbol = new Symbol( symbol, templateToken );
                        if (!closeTag) { 
                            // remember, that we delivered the start tag..
                            _nestedTag[ tokenNum / 2 ]++;
                        }
                    }
                    else {
                        /*
                         * found some token like <a href=""> that does
                         * not contain any pma:name; so no action required.
                         * Reset searching right after '<a ' so that we
                         * find dollar expansions in attributes.
                         */
                        _in.reset();
                    }
                }
                else { // end token..
                    int startCount = _nestedTag[tokenNum / 2];
                    if (startCount > 0) {
                        _nextSymbol = new Symbol( token.getSymbol() );
                        _nestedTag[tokenNum / 2]--;
                    }
                    if (token.isPmaSpecial() && startCount == 0) {
                        error(tagStartPos, "closing special tag '"
                              + token.getKeyword() 
                              + "' that has not been opened");
                    }
                }
            }
        }
        while (_nextSymbol == null);

        /*
         * ok, now we have the next symbol. Up to that token, just output
         * the buffered static content so far.
         */
        Symbol out;
        if (recordedTagBeginPos > 1) {
            out = new Symbol(sym.Bytes, 
                             _recordingStream
                             .getBuffer(0,recordedTagBeginPos-1));
        }
        else {
            out = _nextSymbol;
            _nextSymbol = null;
        }
        _recordingStream.resetRecording();

        return out;
    }

    /**
     * Reads all attributes starting at the current Position at the
     * Reader. The name/values-pairs are written to the given map.
     *
     * @return 'true', if this is a closed tag, 'false' otherwise.
     */
    private boolean readAttributes(StreamPosition pos, Map attMap) 
        throws IOException 
    {
        int c;
        int prev = 0;
        
        int startPos = _recordingStream.getMarkedPosition();
        while (( c = _in.read()) > 0) {
            if (c == '>') {
                AttributeParser attrParser = new AttributeParser(attMap);
                byte[] range = _recordingStream.getBuffer(startPos);
                String errStr = attrParser.readAttributes(new String(range));
                if (errStr != null) {
                    error(pos, errStr);
                }
                return (prev == '/');
            }
            prev = c;
        }
        throw new IllegalArgumentException("EOF while reading attributes");
    }

    private static class BasicToken {
        private final String  _keyword;
        private final int  _symbol;
        private final int  _closedSymbol;
        private final String  _magicAttribute;
        private final boolean _isPmaSpecial;
        private final boolean _isStartSymbol;
        private final boolean _dollarExp;
        
        BasicToken(String keyword) {
            this(keyword, -1, false);
        }

        BasicToken(String keyword, int end) {
             this(keyword, end, false);
        }

        BasicToken(String keyword, int end, boolean dollarExp) {
            _keyword = keyword;
            _symbol = end;
            _closedSymbol = -1;
            _magicAttribute = null;
            _isPmaSpecial = keyword.startsWith("</pma:");

            /*
             * all tokens initialized with this constructor are
             * no starsymbols, unless it is the dollar expansion..
             */
            _isStartSymbol = (dollarExp && end != -1);
            _dollarExp = dollarExp;

        }
        
        /**
         * for start tags..
         */
        BasicToken(String keyword, int start, int simple,
                   String magicAttribute) 
        {
            _keyword = keyword;
            _symbol = start;
            _closedSymbol = simple;
            _magicAttribute = magicAttribute;
            _isPmaSpecial = keyword.startsWith("<pma:");
            _isStartSymbol = true;
            _dollarExp = false;
        }
        
        public boolean isStartToken()   { return _isStartSymbol; }
        public String getKeyword()      { return _keyword; }
        public int getSymbol()       { return _symbol; }
        public int getClosedSymbol() { return _closedSymbol; }
        /**
         * only if this magic attribute is given, this
         * Basic token is actually a token for our purpose;
         */
        public String getMagicAttribute() { return _magicAttribute; }
        public boolean isPmaSpecial() { return _isPmaSpecial; }

        public boolean isDollarExpansion() { return _dollarExp; }
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
