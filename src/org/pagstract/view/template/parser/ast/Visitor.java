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
package org.pagstract.view.template.parser.ast;

/**
 * Visitor for all nodes.
 */
public interface Visitor {
    //-- content
    void visit(NodeSequence node) throws Exception;
    void visit(ConstantNode node) throws Exception;
    void visit(TileNode node) throws Exception;

    //-- actions
    void visit(AnchorNode node) throws Exception;
    void visit(FormNode node) throws Exception;

    //-- values
    void visit(ValueNode node) throws Exception;
    void visit(BeanNode node) throws Exception;
    void visit(ResourceNode node) throws Exception;

    //-- inputs
    void visit(InputFieldNode node) throws Exception;
    void visit(SelectFieldNode node) throws Exception;

    //-- control structures
    void visit(IteratorNode node) throws Exception;
    void visit(SwitchNode node) throws Exception;
    void visit(IfVisibleNode node) throws Exception;

    //-- debugging
    void visit(DebugNode node) throws Exception;
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
