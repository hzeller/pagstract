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
import java.util.Iterator;
//import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;

import org.pagstract.PageModel;
import org.pagstract.model.ComponentModel;
import org.pagstract.model.DataModel;

/**
 * This namespace 
 */
public class PageModelClassNamespace implements Namespace {
    private final Map _pageModelNames;

    public PageModelClassNamespace(Class cls) {
        _pageModelNames = determinePageModelNames(cls);
    }

    /**
     * returns a set of available names within this Namespace.
     */
    public Set/*<String>*/ availableNames() {
        return _pageModelNames.keySet();
    }

    /**
     * predicate to check if a given name exists within this namespace.
     */
    public boolean containsName(String name) {
        return _pageModelNames.containsKey(name);
    }

    
    public Namespace getSubNamespace(String name) 
        throws NoSuchElementException 
    {
        Class cls = getNamedObjectType(name);
        if (cls == null) {
            throw new NoSuchElementException("no such element '"+name+"'");
        }
        return new ClassNamespace(cls);
    }

    public Class getNamedObjectType(String name) {
        Class cls = (Class) _pageModelNames.get(name);
        if (cls == null) {
            return null;
        }
        if (cls.isArray()) {
            return cls.getComponentType();
        }
        else {
            return cls;
        }
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
        Class cls = (Class) _pageModelNames.get(name);
        if (cls == null) {
            return false;
        }

       if (cls == String.class
            || DataModel.class.isAssignableFrom(cls)
            || ComponentModel.class.isAssignableFrom(cls)
            || long.class.isAssignableFrom(cls)
            || int.class   == cls
            || short.class == cls
            || char.class  == cls
            || byte.class  == cls
            || boolean.class == cls) {
            return false;
        }
        return true; // anything else will be handled as a class-namespace
    }

    /**
     * predicate to check, if the named object can be iterated.
     */
    public boolean isIteratableObject(String name) {
        Class cls = (Class) _pageModelNames.get(name);
        if (cls == null) {
            return false;
        }
        return cls.isArray();
    }

    /**
     * checks, wether the class is a PageModel interface with the
     * defined properties. This does not return anything but throws
     * an Exception, if not.
     * (this should be moved in some util class or so..)
     *
     * @param iface the class that should be checked if well formed
     * @return a map of property names to the classes.
     * @throws IllegalArgumentException if the class does not denote a
     *         valid page interface.
     */
    private static Map determinePageModelNames(Class iface) 
        throws IllegalArgumentException 
    {
        final Map result = new /*Linked*/HashMap();
        if (!iface.isInterface()) {
            throw new IllegalArgumentException(iface.getName()
                                               + ": not an interface");
        }
        
        if (!PageModel.class.isAssignableFrom(iface)) {
            throw new IllegalArgumentException(iface.getName()
                                               + ": does not implement PageModel");
        }

        Method[] methods = iface.getMethods();
        for (int i=0; i < methods.length; ++i) {
            Method m = methods[i];
            String name = m.getName();
            if (!name.startsWith("set"))
                continue;

            if (m.getReturnType() != void.class) {
                throw new IllegalArgumentException(name
                                                   + ": no void return type");
            }
            
            Class[] params = m.getParameterTypes();
            if (params.length != 1) {
                throw new IllegalArgumentException(name
                                                   + ": not 1 parameter");
            }
            
            String propName = decapitalize(name.substring(3));
            result.put(propName, params[0]);
        }
        return result;
    }

    /**
     * converts from 'FooBar' to 'fooBar'
     */
    private static String decapitalize(String property) {
        return property.substring(0, 1).toLowerCase() + property.substring(1);
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
