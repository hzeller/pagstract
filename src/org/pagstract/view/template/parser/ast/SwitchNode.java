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

import java.util.Map;
import java.util.Set;

import org.pagstract.view.template.parser.scanner.TemplateToken;

public final class SwitchNode extends AbstractNamedNode {
    private final TemplateToken _tag;
    private final TemplateNode	m_default_content;
    private final Map           m_case_map;

    public SwitchNode( String model_name, TemplateNode default_content,
                       Map case_map, TemplateToken tag) 
    {
        super(model_name, tag.getFilePosition());
        m_default_content= default_content;
        m_case_map= case_map;
        _tag = tag;
    }

    public SwitchNode( String model_name, Map case_map, TemplateToken tag) {
        super(model_name, tag.getFilePosition());
        m_default_content= null;
        m_case_map= case_map;
        _tag = tag;
    }

    public TemplateToken getTemplateToken() {
        return _tag;
    }

    public TemplateNode getDefaultContent() {
        return m_default_content;
    }

    public TemplateNode getNamedContent(String name) {
        if (m_case_map.containsKey(name)) {
            return (TemplateNode) m_case_map.get(name);
        }
        return m_default_content;
    }

    public Set/*<String>*/ getAvailableNames() {
        return m_case_map.keySet();
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
