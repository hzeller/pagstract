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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Namespace that handles the instance of an bean object as a namspace
 * of properties to be accessed.
 */
public class BeanNamespace extends ClassNamespace {
    private static final Log _log = LogFactory.getLog(BeanNamespace.class);

    private static final Object[] EMPTY_PARAM_LIST = new Object[]{};
    private final Object _bean;

    public BeanNamespace(Class cls, Object bean) {
        super(cls);
        _bean = bean;
    }

    public BeanNamespace(Object bean) {
        this(bean.getClass(), bean);
    }
    
    public Namespace getSubNamespace(String name)
        throws NoSuchElementException 
    {
        if (!isNamespace(name)) {
            throw new NoSuchElementException("not a namespace: " + name);
        }
        Object object = getNamedObject(name);
        if (object == null) {
            // ok, return a dummy
            Method getter = (Method) getGettersFor(_namespaceClass).get(name);
            _log.debug("dummy zurück ..");
            return new BeanNamespace(getter.getReturnType(), null);
        }
        return new BeanNamespace(object);
    }

    public Object getNamedObject(String name) 
        throws UnsupportedOperationException {
        if (_bean == null) {
            _log.info("******** leere bean .." + _namespaceClass);
            return null;
        }
        Method getter = (Method) getGettersFor(_namespaceClass).get(name);
        if (getter == null) {
            throw new NoSuchElementException("no such element '"+name+"'");
        }
        
        try {
            return getter.invoke( _bean, EMPTY_PARAM_LIST );
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Iterator/*<Namespace>*/ getNamespaceIterator(String name)
        throws UnsupportedOperationException {
        if (isIteratableObject(name)) {
            Object[] array = (Object[]) getNamedObject(name);
            if (array == null) {
                _log.debug("empty array" + name);
                return null;
            }
            // quick hack:
            List l = new ArrayList();
            for (int i=0; i < array.length; ++i) {
                l.add(new BeanNamespace(array[i]));
            }
            return l.iterator();
        }
        return null;
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
