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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
//import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.pagstract.model.ComponentModel;
import org.pagstract.model.DataModel;

/**
 * A ClassNamespace is a namespace that provides access to the
 * potential getters.
 */
public class ClassNamespace implements Namespace {
    private static final Map _getterCache;
    static {
        _getterCache = new HashMap();
    }

    protected final Class _namespaceClass;

    public ClassNamespace(Class cls) {
        _namespaceClass = cls;
    }

    /**
     * returns a set of available names within this Namespace.
     */
    public Set/*<String>*/ availableNames() {
        return getGettersFor(_namespaceClass).keySet();
    }

    /**
     * predicate to check if a given name exists within this namespace.
     */
    public boolean containsName(String name) {
        return getGettersFor(_namespaceClass).containsKey(name);
    }

    
    public Namespace getSubNamespace(String name) 
        throws NoSuchElementException 
    {
        Class namedClass = getNamedObjectType(name);
        if (namedClass == null) {
            throw new NoSuchElementException("no such element '"+name+"'");
        }

        if (namedClass == String.class
            || DataModel.class.isAssignableFrom(namedClass)
            || ComponentModel.class.isAssignableFrom(namedClass)) {
            throw new NoSuchElementException("no such element '"+name+"'");
        }

        return new ClassNamespace(namedClass);
    }
    
    public Class getNamedObjectType(String name) {
        Method getter = (Method) getGettersFor(_namespaceClass).get(name);
        if (getter == null) {
            return null;
        }
        Class namedClass = getter.getReturnType();
        if (namedClass.isArray()) {
            return namedClass.getComponentType();
        }
        return namedClass;
    }

    /**
     * returns the object with the given Name. If this namespace
     * does not handle concrete object, this throws an 
     * UnsupportedOperationException.
     */
    public Object getNamedObject(String name) 
        throws UnsupportedOperationException {
        throw new UnsupportedOperationException("no concrete object accessible in ClassNamespace");
    }

    public Iterator/*<Namespace>*/ getNamespaceIterator(String name)
        throws UnsupportedOperationException {
        throw new UnsupportedOperationException("no concrete object accessible in ClassNamespace to access namespace iterator");
    }

    /**
     * checks if a named object within this namespace is a
     * namespace. If so, they can be accessed with 
     * {@link #getSubNamespace(String)}.
     */
    public boolean isNamespace(String name) {
        Class namedClass = getNamedObjectType(name);
        if (namedClass == null) {
            return false;
        }
        if (namedClass == String.class
            || DataModel.class.isAssignableFrom(namedClass)
            || ComponentModel.class.isAssignableFrom(namedClass)
            || long.class.isAssignableFrom(namedClass)
            || int.class   == namedClass
            || short.class == namedClass
            || char.class  == namedClass
            || byte.class  == namedClass
            || boolean.class == namedClass) {
            return false;
        }
        return true; // anything else will be handled as a namespace
    }

    /**
     * predicate to check, if the named object can be iterated.
     */
    public boolean isIteratableObject(String name) {
        Method getter = (Method) getGettersFor(_namespaceClass).get(name);
        if (getter == null) {
            return false;
        }
        return getter.getReturnType().isArray();
    }

    /**
     * returns a Map of getters for a certain bean.
     * FIXME: nicht mit property-descriptor bauen, sondern 
     *    direkt mit introspection (vielleicht bekommt man dann gleich die
     *    richtige Reihenfolge ??)
     */
    protected static Map getGettersFor(Class c) {
        Map getters = null;
        if (!_getterCache.containsKey(c)) {
            getters = new /*Linked*/HashMap();
            
            Method methods[] = c.getMethods();
            for( int i=0; i < methods.length; ++i) {
                Method m = methods[i];
                String name = m.getName();
                String getterName;
                
                if (m.getReturnType() == void.class) {
                    continue;
                }
                
                if ((m.getReturnType() == boolean.class
                     || m.getReturnType() == Boolean.class)
                    && name.length() > 2 
                    && name.startsWith("is")) {
                    getterName = name.substring(2);
                }
                else if (name.length() > 3 && name.startsWith("get")) {
                    getterName = name.substring(3);
                }
                else {
                    continue;
                }
                
                getterName = (getterName.substring(0, 1).toLowerCase()
                              + getterName.substring(1));
                if ("class".equals(getterName)) {
                    continue;
                }
                getters.put(getterName , m);
            }
            synchronized (_getterCache) {
                _getterCache.put(c, getters);
            }
        }
        else {
            getters = (Map) _getterCache.get(c);
        }
        return getters;
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
