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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import java_cup.runtime.Symbol;

import org.pagstract.view.template.parser.TemplateParser;
import org.pagstract.view.template.parser.ast.TemplateNode;
import org.pagstract.view.template.parser.scanner.TemplateScanner;

/**
 * A ParsedTemplateCache caches readily parsed Templates.
 */
public class FileTemplateResolver implements TemplateResolver {
    private final Map/*<canonicalFilenameString,TemplateNode>*/ _parsed;
    private ParseListener _parseListener;
    
    public interface ParseListener {
        void beforeParsing(File file);
        void afterParsing(File file, TemplateNode rootNode);
    }

    public FileTemplateResolver() {
        _parsed = new HashMap();
    }

    public void setParseListener(ParseListener l) {
        _parseListener = l;
    }

    public TemplateNode resolveTemplate(String filename) throws Exception {
        return resolveRelativeTemplate(null, filename);
    }

    public TemplateNode resolveRelativeTemplate(String parentResource, 
                                                String relativeResource) 
        throws Exception 
    {
        File file = new File(relativeResource);
        if (!file.isAbsolute() && parentResource != null) {
            //either, this is a directory ..  /foo/
            File parent = new File(parentResource);

            // .. or another file, whose parent directory is the starting
            // point  /foo/IncludingFile.html
            if (!parent.isDirectory()) {
                parent = parent.getParentFile();
            }
            file = new File(parent, relativeResource);
        }
        String canonicalName = file.getCanonicalPath();
        FileCache cache;

        synchronized (_parsed) {
            cache = (FileCache) _parsed.get(canonicalName);
            if (cache == null || cache.isOlderThan(file.lastModified())) {
                if (_parseListener != null) {
                    _parseListener.beforeParsing(file);
                }
                InputStream templateIn = new BufferedInputStream(new FileInputStream(file));
                TemplateScanner scanner=new TemplateScanner(templateIn,
                                                            relativeResource);
                TemplateParser parser = new TemplateParser(scanner);
                Symbol symbol = null;
                try {
                    symbol = parser.parse();
                }
                catch (Exception e) {
                    System.err.println("last reached position in file: "
                                       + scanner.lastReachedPosition());
                    throw e;
                }
                TemplateNode rootNode = (TemplateNode)symbol.value;
                _parsed.put( canonicalName, new FileCache(file.lastModified(),
                                                          rootNode));
                if (_parseListener != null) {
                    _parseListener.afterParsing(file, rootNode);
                }
                return rootNode;
            }
            else {
                return cache.getTemplateRootNode();
            }
        }
    }

    private static final class FileCache {
        private final long       _modifiedTime;
        private final TemplateNode _rootNode;

        public FileCache(long modTime, TemplateNode rootNode) {
            _modifiedTime = modTime;
            _rootNode = rootNode;
        }

        public boolean isOlderThan(long otherTime) {
            return (_modifiedTime < otherTime);
        }
        public TemplateNode getTemplateRootNode() {
            return _rootNode;
        }
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
