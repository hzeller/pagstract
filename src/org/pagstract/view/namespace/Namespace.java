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
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A Namespace is a bundle of names. 
 * Namespaces can be nested: they can be accessed by name.
 */
public interface Namespace {
    /**
     * returns a set of available names within this Namespace.
     */
    Set/*<String>*/ availableNames();

    /**
     * predicate to check if a given name exists within this namespace.
     */
    boolean containsName(String name);

    Namespace getSubNamespace(String name) throws NoSuchElementException;
    
    /**
     * returns the object with the given Name. If this namespace
     * does not handle concrete object, this throws an 
     * UnsupportedOperationException.
     */
    Object getNamedObject(String name) 
        throws UnsupportedOperationException;

    Iterator/*<Namespace>*/ getNamespaceIterator(String name)
        throws UnsupportedOperationException;

    /**
     * returns the type of the object that will be returned for the
     * given name. This is only defined for non-namespaces.
     */
    Class getNamedObjectType(String name);

    /**
     * checks if a named object within this namespace is a
     * namespace. If so, they can be accessed with 
     * {@link #getSubNamespace(String)}.
     */
    boolean isNamespace(String name);

    /**
     * predicate to check, if the named object can be iterated.
     */
    boolean isIteratableObject(String name);
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
