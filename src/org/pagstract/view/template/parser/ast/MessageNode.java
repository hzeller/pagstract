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
import org.pagstract.view.template.parser.scanner.SimpleTemplateToken;

public final class MessageNode implements TemplateNode {
    private final static int REMOVE_FIRST = "msg:".length();
    private final FilePosition _pos;
    private final String _resource;

    public MessageNode(SimpleTemplateToken token) {
        _pos = token.getFilePosition();
        String resource = token.getValue().substring(REMOVE_FIRST);
        if (resource.endsWith("/")) {
            resource = resource.substring(resource.length()-2);
        }
        _resource = resource;
    }

    public String getResourceValue() {
        return _resource;
    }
    
    public FilePosition getPosition() {
        return _pos;
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
