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

import org.pagstract.view.template.parser.scanner.FilePosition;

public final class ConstantNode implements TemplateNode {
    private byte[]  _content;

    public ConstantNode(byte[] content) {
        _content = content;
    }
    
    public byte[] getContentBuffer() {
        return _content;
    }
    
    public void replaceContentBuffer(byte[] newBuf) {
        _content = newBuf;
    }

    public FilePosition getPosition() {
        return null;
    }

    public void accept(Visitor visitor) throws Exception {
        visitor.visit(this);
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
