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
package org.pagstract.view.namespace;

import java.util.Iterator;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A NameResolver is able to resolve absolute and relative names.
 * 
 * This NameResolver resolves fully qualified names starting from a root
 * namespace and relative names according to a current namespace.
 *
 * Fully qualified names are of the form <code>foo.bar.baz</code> and are
 * resolved by asking the root-namespace for the namespace <code>foo</code>,
 * containing <code>bar</code> which in turn contains the leaf-object 
 * <code>baz</code>.</p>
 *
 * Relative names are of the form <code>.baz</code>. They are resolved relative
 * to the last namespace pushed to the Stack.
 *
 * @author Henner Zeller
 */
public final class NameResolver {
    private static final Log _log = LogFactory.getLog(NameResolver.class);

    private static final boolean DEBUG_PAG = true;

    private final Namespace _rootNamespace;
    private final Stack/*<Namespace>*/ _namespaceStack;
    private final Stack/*<Integer>*/ _iteratorIndexStack;

    public NameResolver(Namespace rootNamespace) {
        _rootNamespace = rootNamespace;
        _namespaceStack = new Stack();
        _iteratorIndexStack = new Stack();
    }

    public Object resolveName(String name) {
        //System.err.println("RESOLVING: " + name);
        return resolveNameContext(name).getLeafObject();
    }

    /**
     * resolve a dot-separated name starting from the root namespace.
     * This can be absolute names like <cod>foo.bar.baz</code> or relative
     * names like <code>.baz</code>
     */
    public NamingContext resolveNameContext(String name) {
        /*
         * Names starting with a dot reference a name in the current
         * namespace.
         */
        if (name.startsWith(".")) {

            int depth = 0;
            while (name.startsWith("../")) {
                name = name.substring(3);
               ++depth;
            }

            /*
             * zwei Schreibweisen:
             *  .foo
             *  ./foo
             */
            if (name.startsWith("./")) {
                name = name.substring(2);
            }
            else if (name.startsWith(".")) {
                name = name.substring(1);
            }

            int stackPos = _namespaceStack.size() - depth - 1;
            Namespace currentNs = (Namespace) _namespaceStack.get(stackPos);

            return resolveName(currentNs, name);
        }
        else {
            return resolveName(_rootNamespace, name);
        }
    }

    public boolean containsName(String name) {
        /*
         * Names starting with a dot reference a name in the current
         * namespace.
         */
        if (name.startsWith(".")) {
            if (_namespaceStack.empty()) {
                return false;
            }
            Namespace currentNs = (Namespace) _namespaceStack.peek();
            return containsName(currentNs, name.substring(1));
        }
        else {
            return containsName(_rootNamespace, name);
        }
    }

    /**
     * push the current namespace: this is the namespace that is valid for
     * <dot> <name> resolution.
     */
    public void pushNamespace(Namespace ns) {
        _namespaceStack.push(ns);
    }
    
    public void pushIteratorIndex(int i) {
        _iteratorIndexStack.push(new Integer(i));
    }

    public void popIteratorIndex() {
        _iteratorIndexStack.pop();
    }
    
    public String determineInputfieldSuffix() {
        Iterator it = _iteratorIndexStack.iterator();
        StringBuffer buf = new StringBuffer();
        while (it.hasNext()) {
            buf.append("_").append(it.next());
        }
        return buf.toString();
    }

    /**
     * pop the current namespace: this is the namespace that was valid for
     * <dot> <name> resolution.
     */
    public Namespace popNamespace() {
        return (Namespace) _namespaceStack.pop();
    }

    /**
     * resolve a name given in the name-tag.
     */
    private NamingContext resolveName(Namespace startNamespace, String name) {
        Namespace currentNamespace = startNamespace;
        String previousName = "[root-namespace]";
        final int dotPos = name.indexOf(".");
        if (dotPos == 0) {
            throw new IllegalArgumentException("superfluous '.' for name "
                                               + name);
        }
        if (dotPos > 0) {
            /*
             * go through namespaces until we have the final, right-most 
             * dotless name.
             */
            StringTokenizer tok = new StringTokenizer(name, ".");
            name = tok.nextToken(); // we have at least this token: root-ns
            do {
                if (DEBUG_PAG && !currentNamespace.containsName(name)) {
                    _log.info("there is no namespace named " + name
                              + " in namespace " + previousName);
                }
                currentNamespace = currentNamespace.getSubNamespace(name);
                previousName = name;
                name = tok.nextToken();
            }
            while (tok.hasMoreElements());
        }
        if (DEBUG_PAG && !currentNamespace.containsName(name)) {
            _log.info("there is no element named " + name
                      + " in namespace " + previousName);
        }
        /*
         * get LeafObject of last namespace.
         */
        return new NamingContext(currentNamespace, name);
    }

    /**
     * resolve a name given in the name-tag.
     */
    private boolean containsName(Namespace startNamespace, String name) {
        Namespace currentNamespace = startNamespace;
        String previousName = "[root-namespace]";
        final int dotPos = name.indexOf(".");
        if (dotPos == 0) {
            throw new IllegalArgumentException("superfluous '.' for name "
                                               + name);
        }
        if (dotPos > 0) {
            /*
             * go through namespaces until we have the final, right-most 
             * dotless name.
             */
            StringTokenizer tok = new StringTokenizer(name, ".");
            name = tok.nextToken(); // we have at least this token: root-ns
            do {
                if (DEBUG_PAG && !currentNamespace.containsName(name)) {
                    _log.info("there is no namespace named " + name
                              + " in namespace " + previousName);
                }
                currentNamespace = currentNamespace.getSubNamespace(name);
                previousName = name;
                name = tok.nextToken();
            }
            while (tok.hasMoreElements());
        }
        if (DEBUG_PAG && !currentNamespace.containsName(name)) {
            _log.info("there is no element named " + name
                      + " in namespace " + previousName);
        }
        return currentNamespace.containsName(name);
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
