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

/**
 * A node for an include. It allows to either include a static
 * file or a file that is given by the model names' value.
 */
public final class TileNode  implements NamedTemplateNode {
    private final String _filename;
    private final String _modelName;
    private final FilePosition _filePosition;

    public TileNode( String mname, String fname, FilePosition fpos) {
        _filename  = (fname != null && fname.length() > 0) ? fname : null;
        _modelName = (mname != null && mname.length() > 0) ? mname : null;
        _filePosition = fpos;
        if (_filename == null && _modelName == null) {
            throw new IllegalArgumentException(fpos.toString() + " : Error: "
                                               +"<pma:tile> must have a 'filename' or 'pma:name' attribute.");
        }
    }

    public FilePosition getPosition() {
        return _filePosition;
    }

    /**
     * The filename
     */
    public String getFileName() {
        return _filename;
    }

    public String getModelName() {
        return _modelName;
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
