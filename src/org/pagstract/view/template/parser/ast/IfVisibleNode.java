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

public final class IfVisibleNode extends AbstractTemplateContentNode {
    private final TemplateToken    _tag;
    private final boolean _posBooleanCondition;
    
    public IfVisibleNode( TemplateToken tag, TemplateNode content)
    {
        super(tag.getModelName(), content, tag.getFilePosition());
        _tag = tag;
        String cond = tag.getAttribute("pma:condition");
        if (cond != null && !"true".equals(cond) && !"false".equals(cond)) {
            throw new IllegalArgumentException(tag.getFilePosition() + ": if-visible takes only 'true' or 'false' in pma:condition");
        }
        _posBooleanCondition = ((cond == null) || "true".equals(cond));
    }

    public TemplateToken getTemplateToken() {
        return _tag;
    }

    public boolean isPositiveBooleanCondition() {
        return _posBooleanCondition;
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
