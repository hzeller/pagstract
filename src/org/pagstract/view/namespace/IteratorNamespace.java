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
import java.util.NoSuchElementException;

/**
 * An IteratorNamespace is a special kind of a BeanNamespace that changes
 * the bean to be accessed with each iteration of the loop. This kind of
 * Namespace is used within lists.
 * A special name '#number' is provided to access the current position of
 * the iteration.
 */
public class IteratorNamespace extends ClassNamespace {
    private static final Object[] EMPTY_PARAM_LIST = new Object[]{};
    private final ListCursor _listCursor;
    private static final String COUNT_VARIABLE = "#number";

    /**
     * create an Iterator Namespace. 
     */
    public IteratorNamespace(Class cls, ListCursor listCursor) {
        super(cls);
        _listCursor = listCursor;
    }
    
    public Namespace getSubNamespace(String name)
        throws NoSuchElementException 
    {
        if (!isNamespace(name)) {
            throw new NoSuchElementException("not a namespace: " + name);
        }
        return new BeanNamespace(getNamedObject(name));
    }

    /**
     * return the Object with the given Name.
     */
    public Object getNamedObject(String name) 
        throws UnsupportedOperationException {
        if ( COUNT_VARIABLE.equals(name) ) {
            return String.valueOf( _listCursor.getCurrentCount() );
        }
        else {
            Method getter = (Method) getGettersFor(_namespaceClass).get(name);
            if (getter == null) {
                throw new NoSuchElementException("no such element '"+name+"'");
            }
            
            try {
                return getter.invoke( _listCursor.getCurrentObject(),
                                      EMPTY_PARAM_LIST );
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
       }
    }

    /**
     * returns, whether a given element exists.
     */
    public boolean containsName(String name) {
        if ( COUNT_VARIABLE.equals(name) ) {
            return true;
        }
        else {
           return super.containsName(name);
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
