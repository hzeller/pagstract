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
 * A named node that has a content which itself has a content.
 */
public abstract class AbstractTemplateContentNode extends AbstractNamedNode {
    private final TemplateNode _content;

    protected AbstractTemplateContentNode(String modelName, 
                                          TemplateNode content,
                                          FilePosition fpos) 
    {
        super(modelName, fpos);
        _content = content;
    }

    public TemplateNode getTemplateContent() {
        return _content;
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
