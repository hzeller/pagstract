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
    private final ResourceResolver _resourceResolver;

    private ParseListener _parseListener;


    public interface ParseListener {
        void beforeParsing(File file);
        void afterParsing(File file, TemplateNode rootNode);
    }

    /**
     * create a template resolver that takes the given template files
     * 'as-is'
     */
    public FileTemplateResolver() {
        this("");
    }

    /**
     * create a template resolver that prepends a prefix to each template
     * name it gets.
     */
    public FileTemplateResolver(String prefix) {
        this(new PrefixResourceResolver(prefix));
    }
    
    /**
     * create a template resolver with a user provided mapping between
     * abstract filenames and their concrete counterparts.
     */
    public FileTemplateResolver(ResourceResolver resourceResolver) {
        _parsed = new HashMap();
        _resourceResolver = resourceResolver;
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
        String resourceName = PathUtil.combinePath(parentResource, relativeResource);
        resourceName = PathUtil.normalizePath(resourceName);

        final String resource =_resourceResolver.resolveResource(resourceName);
        final File file = new File(resource);

        final String canonicalName = file.getCanonicalPath();
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

    /**
     * normalizes a path: removes all ./ and ../ and multiple slashes ///
     * If ../ go beyond root, just make it absoulte.
     */
    private String normalizePath(String file) {
        StringBuffer result = new StringBuffer();
        int lastSlash = 0;
        // if we start with './' -> remove; with '../' -> make absolute: '/'
        while (file.startsWith("./") || file.startsWith("../")) {
            file = file.substring(2);
        }
        final int len = file.length();
        int i = 0;
        while (i < len) {
            if (file.charAt(i) == '/') {
                while (i < len-1 && file.charAt(i+1) == '/')
                    ++i;
                if (i < len-2 && file.charAt(i+1) == '.') {
                    switch (file.charAt(i+2)) {
                    case '/':    /*** /./ ***/
                        i += 2;
                        continue;
                    case '.':    /*** /../ ***/
                        if (i < len-3 && file.charAt(i+3) == '/') {
                            i+=3;
                        }
                        for (int j = result.length()-1; j >= 0; --j) {
                            if (j == 0 || result.charAt(j) == '/') {
                                result.setLength(j);
                                break;
                            }
                        }
                        continue;
                    }
                }
            }
            char c = file.charAt(i);
            result.append(c);
            if (c == '/') lastSlash = i;
            ++i;
        }
        return result.toString();
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
