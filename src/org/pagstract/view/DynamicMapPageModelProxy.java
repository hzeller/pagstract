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
package org.pagstract.view;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.pagstract.PageModel;

/**
 * A factory for Map-backed dynamic proxies for page models. Page Models
 * created by this factory store the data written in the setter-methods
 * of the Page Model to the map given.
 *
 * This Proxy is used in the template and JSP implementations.
 *
 * @author Henner Zeller
 */
public final class DynamicMapPageModelProxy implements InvocationHandler {
    /**
     * create a {@link PageModel} that implements the given interface and
     * stores its values to the given Map.
     * @param pageModelClass the class of which an instance is requested
     * @param storageMap the map the data written to the page model is written
     *                   to
     */
    public static PageModel createInstance(Class pageModelClass,
                                           Map storageMap) {
        InvocationHandler handler = new DynamicMapPageModelProxy(storageMap);
        ClassLoader targetClassLoader = pageModelClass.getClassLoader();
        return (PageModel)(Proxy.newProxyInstance(targetClassLoader,
                                                  new Class[] {pageModelClass},
                                                  handler));
    }

    //-- the Invocation Handler
    private final Map _storageMap;

    private DynamicMapPageModelProxy(Map storageMap) {
        _storageMap = storageMap;
    }

    public Object invoke(Object proxy, Method method, Object[] args) {
        String name = method.getName();
        /*
         * standard methods.
         */
        if ("toString".equals(name)) {
            return _storageMap.toString();
        }
        if ("hashCode".equals(name)) {
            return new Integer(_storageMap.hashCode());
        }
        if ("equals".equals(name)) {
            DynamicMapPageModelProxy otherProxy;
            otherProxy = (DynamicMapPageModelProxy) args[0];
            return new Boolean(otherProxy._storageMap.equals(_storageMap));
        }
        
        /*
         * setting the actual value. This setter writes the value to the
         * map supplied.
         */
        if (name.startsWith("set")) {
            name = decapitalize(name.substring(3));
            _storageMap.put(name, args[0]);
            //System.out.println( "proxy("+name+"): "+args[0]);
        }

        // convenient getter..
        if (name.startsWith("get")) {
            name = decapitalize(name.substring(3));
            return _storageMap.get(name);
        }

        return null;
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
