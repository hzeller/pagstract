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
 * Many nodes have a corresponding name in the model
 */
public abstract class AbstractNamedNode implements NamedTemplateNode {
    private final String _modelName;
    private final FilePosition _filePosition;

    protected AbstractNamedNode(String modelName, FilePosition fpos) {
        _modelName= modelName;
        _filePosition = fpos;
        if (modelName == null || modelName.length() <= 0) {
            throw new IllegalArgumentException(fpos.toString() + " : Error: "
                                               +"No valid name given for Tag");
        }
    }

    public FilePosition getPosition() {
        return _filePosition;
    }

    public String getModelName() {
        return _modelName;
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
