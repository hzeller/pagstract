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

import org.pagstract.view.ObjectRenderer;
import java.util.Map;
import java.util.HashMap;

/**
 * A RendererResolver contains a number of renderers for different
 */
public class RendererResolver {
    private final ObjectRenderer _defaultRenderer;
    private final Map _renderMap;

    public RendererResolver(ObjectRenderer renderer) {
        _renderMap = new HashMap();
        _defaultRenderer = renderer;
    }

    public RendererResolver() {
        this(new SimpleStringRenderer());
    }
    
    public void addRenderer(Class matchClass, ObjectRenderer renderer) {
        _renderMap.put(matchClass, renderer);
    }

    /**
     * returns if there is a registered renderer for elements of the
     * given class.
     */
    public boolean hasRendererFor(Class renderClass) {
        return (findMatchingRendererFor(renderClass) != null);
    }

    /**
     * Returns the renderer for the given class or the default renderer.
     */
    public ObjectRenderer findRendererFor(Class renderClass) {
        final ObjectRenderer renderer = findMatchingRendererFor(renderClass);
        return (renderer != null) ? renderer : _defaultRenderer;
    }

    private ObjectRenderer findMatchingRendererFor(Class renderClass) {
        if (renderClass == null) {
            return null;
        } 
        else {
            Object r = _renderMap.get(renderClass);
            if (r != null) {
                return (ObjectRenderer)r;
            } 
            else {
                return findMatchingRendererFor(renderClass.getSuperclass());
            }
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
