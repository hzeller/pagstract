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

import org.pagstract.view.template.parser.ast.list.ListContent;

/**
 * A Container for all the Elements that can be part of a list container.
 * Basically header, footer, separator and content. These elements
 * can come in any sequence, however, not twice.
 */
public final class ListContentContainer {
    private TemplateNode _header;
    private TemplateNode _separator;
    private TemplateNode _content;
    private TemplateNode _footer;
    private TemplateNode _emptyContent;

    public ListContentContainer(TemplateNode content) {
        _content = content;
    }

    public ListContentContainer(ListContent content) {
        addListContent(content);
    }
    
    public void addListContent(ListContent content) {
        content.addToContainer(this); // accept(this)
    }

    public void setHeader(TemplateNode node) {
        checkExists(_header, node, "header");
        _header = node;
    }
    public TemplateNode getHeader() {
        return _header;
    }

    public void setSeparator(TemplateNode node) {
        checkExists(_separator, node, "separator");
        _separator = node;
    }
    public TemplateNode getSeparator() {
        return _separator;
    }

    public void setContent(TemplateNode node) {
        checkExists(_content, node, "content");
        _content = node;
    }
    public TemplateNode getContent() {
        return _content;
    }

    public void setEmptyContent(TemplateNode node) {
        checkExists(_emptyContent, node, "no-content");
        _emptyContent = node;
    }
    public TemplateNode getEmptyContent() {
        return _emptyContent;
    }

    public void setFooter(TemplateNode node) {
        checkExists(_footer, node, "footer");
        _footer = node;
    }
    public TemplateNode getFooter() {
        return _footer;
    }

    private void checkExists(TemplateNode origNode, TemplateNode newNode,
                             String msg) throws IllegalArgumentException {
        if (origNode != null) {
            throw new IllegalArgumentException("cannot set list " + msg 
                                               + " at "
                                               + newNode.getPosition()
                                               + " since " + msg 
                                               + " already set at "
                                               + origNode.getPosition());
        }
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
