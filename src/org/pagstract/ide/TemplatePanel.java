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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;

import org.pagstract.view.namespace.Namespace;
import org.pagstract.view.namespace.PageModelClassNamespace;
import org.pagstract.view.template.TemplateResolver;
import org.pagstract.view.template.parser.ast.TemplateNode;

/**
 * document me.
 */
public class TemplatePanel extends JPanel implements TemplateSource {
    private final Namespace _concreteRootNamespace;
    private final TemplateResolver _cache;
    private final String _templateFileName;

    public TemplatePanel(String name,
                         Class pageModelClass, TemplateResolver cache,
                         String file) 
    {
        _cache = cache;
        _templateFileName = file;
        System.err.println(_templateFileName);

        ContentEditPanel editPane = new ContentEditPanel();
        Namespace abstractNs = new PageModelClassNamespace(pageModelClass);
        _concreteRootNamespace = new ContentNamespace(abstractNs, editPane);
        traverseNamespace( _concreteRootNamespace );

        TreeModel model = new NamespaceTreeModelAdapter(name, abstractNs);
        JTree tree = new JTree(model);
        tree.setCellRenderer(new NamespaceTreeCellRenderer());
        tree.setPreferredSize(new Dimension(200, 100));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.add(new JScrollPane(tree));
        
        //-- edit stuff not centered.
        JPanel editTop = new JPanel();
        editTop.setLayout(new BorderLayout());
        editTop.add(editPane, BorderLayout.NORTH);
        splitPane.add(new JScrollPane(editTop));

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

    private void traverseNamespace(Namespace ns) {
        Iterator names = ns.availableNames().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            if (ns.isNamespace(name)) {
                traverseNamespace(ns.getSubNamespace(name));
            }
            else {
                ns.getNamedObject(name);
            }
        }
    }

    //-- interface TemplateSource

    public Namespace getRootNamespace() {
        return _concreteRootNamespace;
    }
    public TemplateNode getTemplateRootNode() throws Exception {
        return _cache.resolveTemplate(_templateFileName);
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
