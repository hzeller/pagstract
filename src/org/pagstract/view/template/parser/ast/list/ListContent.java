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
package org.pagstract.view.template.parser.ast.list;

import org.pagstract.view.template.parser.ast.ListContentContainer;
import org.pagstract.view.template.parser.ast.TemplateNode;

public abstract class ListContent {
    protected final TemplateNode _node;

    protected ListContent(TemplateNode node) {
        _node = node;
    }
    
    /**
     * add myself to the given container. (Visitor pattern).
     */
    public abstract void addToContainer(ListContentContainer cont);
}
