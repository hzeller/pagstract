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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.pagstract.view.namespace.Namespace;

/**
 * A TreeModel Adapter for a namespace
 */
public class NamespaceTreeModelAdapter implements TreeModel {
    protected final NamespaceNode _root;
    /**
     * since the Namespace does not allow to access the elements of
     * an Namespace per index, we have to cache them. This assumes, that
     * the underlying Namespace does not change.
     */
    protected final Map/*<Namespace, Namespace[]>*/ _elementCache;

    public NamespaceTreeModelAdapter(Namespace root) {
        this("PageModel", root);
    }

    public NamespaceTreeModelAdapter(String name, Namespace root) {
        _root = new NamespaceNode(name, root, false, null);
        _elementCache = new HashMap();
    }

    public Object getRoot() {
        return _root;
    }

    protected Object[] getElements(Object elem) {
        if (elem == null) {
            return null;
        }
        if (_elementCache.containsKey(elem)) {
            return (Object[]) _elementCache.get(elem);
        }

        Object result[];
        NamespaceNode node = (NamespaceNode) elem;
        Namespace ns = node.getNamespace();

        if (ns != null) {
            List l = new ArrayList();
            Iterator it = ns.availableNames().iterator();
            while (it.hasNext()) {
                String name = (String) it.next();
                Namespace subNs = null;
                if (ns.isNamespace(name)) {
                    subNs = ns.getSubNamespace(name);
                }
                l.add(new NamespaceNode(name, subNs,
                                        ns.isIteratableObject(name),
                                        ns.getNamedObjectType(name)));
            }
            result = new Object [ l.size() ];
            result = l.toArray(result);
        }
        else {
            result = new Object[0];
        }
        _elementCache.put(elem, result);
        return result;
    }

    public Object getChild(Object parent, int index) {
        return getElements(parent)[index];
    }

    public int getChildCount(Object parent) {
        return getElements(parent).length;
    }

    public boolean isLeaf(Object node) {
        return getElements(node).length == 0;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        // TODO: update cache.
    }

    // linear complexity !
    public int getIndexOfChild(Object parent, Object child) {
        Object elements[] = getElements(parent);
        for (int i=0; i < elements.length; ++i) {
            if (elements[i].equals(child)) return i;
        }
        return -1;
    }

    public void addTreeModelListener(TreeModelListener l) {
    }
    public void removeTreeModelListener(TreeModelListener l) {
    }

    public static final class NamespaceNode {
        private final String _name;
        private final Namespace _ns;
        private final boolean   _iteratable;
        private final Class     _modelClass;

        public NamespaceNode(String name, Namespace ns,
                             boolean iteratable, 
                             Class modelClass)
        {
            _name = name;
            _ns = ns;
            _iteratable = iteratable;
            _modelClass = modelClass;
        }

        public String getName() {
            return _name;
        }
        public Namespace getNamespace() {
            return _ns;
        }
        
        public boolean isIteratable() {
            return _iteratable;
        }

        public Class getModelClass() {
            return _modelClass;
        }
        public String toString() {
            return getName();
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
