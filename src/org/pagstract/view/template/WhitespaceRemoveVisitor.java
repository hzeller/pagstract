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
 * removes all unnecessary whitespace in constant nodes.
 */
public class WhitespaceRemoveVisitor extends RecursiveDescendVisitor {

    private final boolean isNewline(byte b) {
        return (b == '\n' || b == '\r');
    }
    private final boolean isWhitespace(byte b) {
        return (b == ' ' || b == '\t' || isNewline(b));
    }

    public void visit(ConstantNode node) {
        byte[] source = node.getContentBuffer();
        boolean containsNewline = false;
        int outPos = 0;
        int state = 0;
        for (int i=0; i < source.length; ++i) {
            switch (state) {
            case 0: // text
                if (isWhitespace(source[i])) {
                    containsNewline = isNewline(source[i]);
                    state = 1;
                }
                else {
                    source[outPos++] = source[i];
                }
                break;
            case 1: // in whitespace
                if (!isWhitespace(source[i])) {
                    // write a single space
                    source[outPos++] = (byte) (containsNewline ? '\n' : ' ');
                    source[outPos++] = source[i];
                    state = 0;
                }
                else {
                    containsNewline |= isNewline(source[i]);
                }
                break;
            }
        }

        /* collapse all whitespaces to one. If there has been any
         * newline in the sequence of whitespaces, then use a newline.
         * Thus, the general structure of the file still looks almost good.
         */
        if (state == 1) {
            source[outPos++] = (byte) (containsNewline ? '\n' : ' ');
        }

        byte[] dest = new byte [ outPos ];
        System.arraycopy(source, 0, dest, 0, outPos);
        node.replaceContentBuffer(dest);
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

