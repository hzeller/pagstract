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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.pagstract.view.template.parser.scanner.FilePosition;

public final class NodeSequence implements TemplateNode {
    private final List _output_elements;
    private final FilePosition _filePosition;

    public NodeSequence(TemplateNode initialNode) {
        _output_elements= new LinkedList();
        _output_elements.add(initialNode);
        _filePosition = initialNode.getPosition();
    }

    public NodeSequence(List els, FilePosition fpos) {
        _output_elements = els;
        _filePosition = fpos;
    }

    public void addElement( TemplateNode el) {
        _output_elements.add( el);
    }

    public Iterator/*<TemplateNode>*/ getElements() {
        return _output_elements.iterator();
    }

    //-- interface TemplateNode

    public void accept(Visitor visitor) throws Exception {
        visitor.visit(this);
    }

    public FilePosition getPosition() {
        return _filePosition;
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
