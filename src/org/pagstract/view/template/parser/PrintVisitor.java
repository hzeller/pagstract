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
package org.pagstract.view.template.parser;

import java.io.PrintStream;
import java.util.Iterator;

import org.pagstract.view.template.parser.ast.AbstractNamedNode;
import org.pagstract.view.template.parser.ast.AbstractTemplateContentNode;
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
import org.pagstract.view.template.parser.ast.MessageNode;
import org.pagstract.view.template.parser.ast.DebugNode;
import org.pagstract.view.template.parser.ast.Visitor;

/**
 * A Visitor that just prints the abstract syntax tree
 */
public class PrintVisitor implements Visitor {
    private PrintStream _out;

    /**
     * print to given stream.
     */
    public PrintVisitor(PrintStream out) {
        _out = out;
    }
    
    /**
     * prints to stdout.
     */
    public PrintVisitor() {
        this(System.out);
    }

    public void visit(NodeSequence node) throws Exception {
        Iterator it = node.getElements();
        while (it.hasNext()) {
            TemplateNode subnode = (TemplateNode) it.next();
            subnode.accept(this);
        }
    }

    public void visit(TileNode node) throws Exception {
        _out.print("<pma:tile>");
        _out.print(node.getPosition());
    }

    public void visit(AnchorNode node) throws Exception {
        printContent("ANCHOR", node);
    }

    public void visit(BeanNode node) throws Exception {
        printContent("BEAN", node);
    }

    public void visit(IfVisibleNode node) throws Exception {
        printContent("IF-VISIBLE", node);
    }

    public void visit(ConstantNode node) {
        _out.print(new String(node.getContentBuffer()));
    }

    public void visit(FormNode node) throws Exception {
        printContent("FORM", node);
    }

    public void visit(InputFieldNode node) {
        printContent("INPUT", node);
    }

    public void visit(IteratorNode node) throws Exception {
        printContent("ITERATOR", node);
    }

    public void visit(SelectFieldNode node) {
        printContent("SELECT", node);
    }

    public void visit(SwitchNode node) throws Exception {
        _out.print("<SWITCH");
        _out.println(" ps:name='" + node.getModelName() + "'>");
        TemplateNode defaultContent = node.getDefaultContent();
        if (defaultContent != null) {
            _out.println("<DEFAULT>");
            defaultContent.accept(this);
            _out.println("</DEFAULT>");
        }
        Iterator it = node.getAvailableNames().iterator();
        while (it.hasNext()) {
            String value = (String) it.next();
            _out.println("<CASE value='" + value + "'>");
            TemplateNode content = node.getNamedContent(value);
            content.accept(this);
            _out.println("</CASE>");
        }
        _out.println("</SWITCH>");
    }

    public void visit(ValueNode node) {
        printContent("VALUE", node);
    }

    public void visit(DebugNode node) {
        printContent("DEBUG", node);
    }

    public void visit(ResourceNode node) {
        _out.print("resource: " + node.getResourceValue());
    }

    public void visit(MessageNode node) {
        _out.print("msg: " + node.getResourceValue());
    }

    private void printContent(String name, AbstractNamedNode n) {
        _out.print(n.getPosition()); 
        _out.print("<" + name);
        _out.print(" pma:name='" + n.getModelName() + "'/>");
    }

    private void printContent(String name, AbstractTemplateContentNode n) 
        throws Exception {
        _out.print(n.getPosition());
        _out.print("<" + name);
        _out.print(" pma:name='" + n.getModelName() + "'>");
        TemplateNode content = n.getTemplateContent();
        if (content != null) {
            content.accept(this);
        }
        _out.print("</" + name + ">");
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
