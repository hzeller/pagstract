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

public final class IteratorNode extends AbstractNamedNode {
    private final TemplateToken    _tag;
    private final ListContentContainer _contentElements;
    private final Integer _startPos;
    private final Integer _count;

    public IteratorNode( TemplateToken tag, ListContentContainer content) {
        super(tag.getModelName(), tag.getFilePosition());
        _contentElements = content;
        _tag = tag;
        _startPos = parseInt(tag, "start");
        _count = parseInt(tag, "count");
    }

    private Integer parseInt(TemplateToken tag, String name) {
        String attr = tag.getAttribute(name);
        if (attr == null) return null;
        Integer result = null;
        try {
            result = new Integer(attr);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(tag.getFilePosition() + ": invalid number given in '" + name + "' attribute; got '" + attr + "' but expected a number");
        }
        return result;
    }

    public IteratorNode( TemplateToken tag, TemplateNode content )
    {
        this(tag, new ListContentContainer(content));
    }

    public IteratorNode( TemplateToken tag ) {
        this(tag, (TemplateNode) null);
    }

    public Integer getStartPos() {
        return _startPos;
    }

    public Integer getCount() {
        return _count;
    }

    public TemplateNode getHeader() {
        return _contentElements.getHeader();
    }
    public TemplateNode getFooter() {
        return _contentElements.getFooter();
    }
    public TemplateNode getContent() {
        return _contentElements.getContent();
    }
    public TemplateNode getSeparator() {
        return _contentElements.getSeparator();
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
