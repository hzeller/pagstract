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

import java.util.Iterator;

import org.pagstract.view.template.parser.ast.AnchorNode;
import org.pagstract.view.template.parser.ast.BeanNode;
import org.pagstract.view.template.parser.ast.ConstantNode;
import org.pagstract.view.template.parser.ast.FormNode;
import org.pagstract.view.template.parser.ast.InputFieldNode;
import org.pagstract.view.template.parser.ast.IteratorNode;
import org.pagstract.view.template.parser.ast.NodeSequence;
import org.pagstract.view.template.parser.ast.SelectFieldNode;
import org.pagstract.view.template.parser.ast.SwitchNode;
import org.pagstract.view.template.parser.ast.TemplateNode;
import org.pagstract.view.template.parser.ast.TileNode;
import org.pagstract.view.template.parser.ast.ValueNode;
import org.pagstract.view.template.parser.ast.IfVisibleNode;
import org.pagstract.view.template.parser.ast.ResourceNode;
import org.pagstract.view.template.parser.ast.Visitor;

/**
 * Visits _all_ Nodes recursively. Use this as baseclass for a
 * visitor that wants all its leaf nodes to be visited.
 */
public class RecursiveDescendVisitor implements Visitor {
    
    public void visit(NodeSequence node) throws Exception {
        Iterator it = node.getElements();
        while (it.hasNext()) {
            TemplateNode subnode = (TemplateNode) it.next();
            subnode.accept(this);
        }
    }

    public void visit(TileNode node) throws Exception {
        // leaf node
    }

    public void visit(AnchorNode node) throws Exception {
        node.getTemplateContent().accept(this);
    }

    public void visit(BeanNode node) throws Exception {
        node.getTemplateContent().accept(this);
    }

    public void visit(ConstantNode node) {
        // leaf node
    }

    public void visit(FormNode node) throws Exception {
        node.getTemplateContent().accept(this);
    }

    public void visit(InputFieldNode node) {
        // leaf node
    }

    public void visit(IteratorNode node) throws Exception {
        acceptNonNull(node.getHeader());
        acceptNonNull(node.getContent());
        acceptNonNull(node.getSeparator());
        acceptNonNull(node.getFooter());
    }

    public void visit(SelectFieldNode node) {
        // leaf node
    }

    public void visit(SwitchNode node) throws Exception {
        acceptNonNull(node.getDefaultContent());
        Iterator it = node.getAvailableNames().iterator();
        while (it.hasNext()) {
            String value = (String) it.next();
            TemplateNode content = node.getNamedContent(value);
            content.accept(this);
        }
    }

    public void visit(ValueNode node) {
        // leaf node
    }

    public void visit(ResourceNode node) {
        // leaf node
    }

    public void visit(IfVisibleNode node) throws Exception {
        node.getTemplateContent().accept(this);
    }

    private void acceptNonNull(TemplateNode node) throws Exception {
        if (node != null) {
            node.accept(this);
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

