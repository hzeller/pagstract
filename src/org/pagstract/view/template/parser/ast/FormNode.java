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

public final class FormNode extends AbstractTemplateContentNode {
    private final TemplateToken    m_tag;

    public FormNode( String model_name, TemplateNode content, 
                     TemplateToken tag) 
    {
        super(model_name, content, tag.getFilePosition());
        m_tag= tag;
    }

    public TemplateToken getTemplateToken() {
        return m_tag;
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
