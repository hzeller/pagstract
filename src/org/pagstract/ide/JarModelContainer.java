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
package org.pagstract.ide;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.JarFile;

import org.pagstract.PageModel;

/**
 * A JarModelContainer gets its models from a par (page archive) file.
 */
public class JarModelContainer implements ModelContainer {
    private final Map _modelMap;
    
    public JarModelContainer(String file)
        throws IOException, IllegalArgumentException 
    {
        this(new File(file));
    }

    public JarModelContainer(File file) 
        throws IOException, IllegalArgumentException 
    {
        if (!file.exists()) {
            throw new IOException("not a valid file given to JarModelContainer");
        }
        ClassLoader loader = new URLClassLoader(new URL[]{ file.toURL() });
        _modelMap = new LinkedHashMap();
        JarFile jar = new JarFile(file);
        Reader reader = new InputStreamReader(jar.getInputStream(jar.getEntry("META-INF/models")));
        LineNumberReader lineReader = new LineNumberReader(reader);

        String line;
        while ((line = lineReader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0 || line.startsWith("#")) { /* comment */
                continue;
            }
            StringTokenizer lineTok = new StringTokenizer(line, ":");
            if (lineTok.countTokens() != 2) {
                throw new IllegalArgumentException("line in 'META-INF/models' errornous: " + line);
            }
            String name = lineTok.nextToken().trim();
            String className = lineTok.nextToken().trim();
            Class cls;
            try {
                cls = loader.loadClass( className );
            }
            catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("unable to load class '"
                                                   + e.getMessage() + "'");
            }
            if (!cls.isInterface() || !PageModel.class.isAssignableFrom(cls)) {
                throw new IllegalArgumentException(className 
                                                   + ": not an interface implementing a PageModel");
            }
            _modelMap.put(name, cls);
        }
    }

    public Iterator/*<String>*/ getModelNames() {
        return _modelMap.keySet().iterator();
    }

    public Class getPageModel(String name) {
        return (Class) _modelMap.get(name);
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
