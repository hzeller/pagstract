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

public final class ValueNode extends AbstractNamedNode {
    private final boolean _raw;

    public ValueNode( String model_name, String rawFlag, FilePosition fpos) {
        super(model_name, fpos);
        boolean raw;
        if (rawFlag == null) {
            raw = false;
        }
        else {
            rawFlag = rawFlag.toLowerCase();
            if ("true".equals(rawFlag) 
                || "1".equals(rawFlag) 
                || "on".equals(rawFlag)) {
                raw = true;
            }
            else if ("false".equals(rawFlag) 
                || "0".equals(rawFlag) 
                || "off".equals(rawFlag)) {
                raw = false;
            }
            else {
                throw new IllegalArgumentException(fpos + 
                                                   ": Error: boolean flag expected for attribute 'raw' (instead of '" + rawFlag + "'");
            }
        }
        _raw = raw;
    }

    public void accept(Visitor visitor) throws Exception {
        visitor.visit(this);
    }

    public boolean writeAsRaw() {
        return _raw;
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
