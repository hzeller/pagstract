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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.pagstract.model.ComponentModel;
import org.pagstract.model.DataModel;

/**
 * A simple Namespace that is backed by a Map. In the JSP and template
 * based implementation, the root-namespace (i.e. the namespace directly
 * coupled with the PageModel) is Map-backed; a map filled by a
 * {@link org.pagstract.view.DynamicMapPageModelProxy}.
 */
public class MapNamespace implements Namespace {
    private final Map _map;

    public MapNamespace(Map inputMap) {
        _map = inputMap;
    }

    /**
     * returns, whether a given element exists.
     */
    public boolean containsName(String name) {
        return _map.containsKey(name);
    }
    
    /**
     * return the namespace with the given Name.
     */
    public Namespace getSubNamespace(String name) {
        Object object = getNamedObject(name);
        if (object instanceof Namespace) {
            return (Namespace) object;
        }
        if (!isNamespaceObject(object)) {
            throw new NoSuchElementException("not a namespace: " + name);
        }

        try {
            return new BeanNamespace(object);
        } catch (Exception e) {
            throw new RuntimeException("Could not get subnamespace for: "+name,e);
        }
        //return new BeanNamespace(object);
    }

    /**
     * return the Object with the given Name.
     */
    public Object getNamedObject(String name) {
        return _map.get(name);
    }

    public Iterator/*<Namespace>*/ getNamespaceIterator(String name)
        throws UnsupportedOperationException {
        if (isIteratableObject(name)) {
            Object[] array = (Object[]) _map.get(name);
            // quick hack:
            List l = new ArrayList();
            for (int i=0; i < array.length; ++i) {
                Object val = array[i];
                if (isNamespaceObject(val)) {
                    l.add(new BeanNamespace(val));
                }
                else {
                    l.add(val);
                }
            }
            return l.iterator();
        }
        return null;
    }
    
    public Class getNamedObjectType(String name) {
        Object o = _map.get(name);
        if (o == null) {
            return null;
        }
        Class cls = o.getClass();
        if (cls.isArray()) {
            return cls.getComponentType();
        }
        else {
            return cls;
        }
    }

    public boolean isNamespace(String name) {
        return isNamespaceObject(getNamedObject(name));
    }

    private boolean isNamespaceObject(Object o) {
        return ! (o instanceof String
                  || o instanceof DataModel
                  || o instanceof ComponentModel);
    }

    public Set/*<String>*/ availableNames() {
        return _map.keySet();
    }

    /**
     * predicate to check, if the named object can be iterated.
     */
    public boolean isIteratableObject(String name) {
        Object o = _map.get(name);
        if (o == null) {
            return false;
        }
        Class cls = o.getClass();
        return cls.isArray();
    }

    public String toString() {
        return _map.toString();
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
