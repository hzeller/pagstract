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

import org.pagstract.view.template.parser.scanner.TemplateToken;

public final class AnchorNode extends AbstractTemplateContentNode {
    private final TemplateToken    _tag;
    private final String _linkType;

    public AnchorNode( String linkType,
                       String model_name, TemplateNode content,
                       TemplateToken tag) 
    {
        super(model_name, content, tag.getFilePosition());
        _tag = tag;
        _linkType = linkType;
    }

    public String getLinkType() {
        return _linkType;
    }

    public TemplateToken getTemplateToken() {
        return _tag;
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
