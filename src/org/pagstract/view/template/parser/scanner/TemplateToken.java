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
package org.pagstract.view.template.parser.scanner;

import java.util.Iterator;
import java.util.Map;
import org.pagstract.model.AttributeSet;

/**
 * Ein TemplateToken hat eine Position im File. (AttributedTemplateToken)
 */
public class TemplateToken implements AttributeSet {
    private final FilePosition _position;
    private final Map _attributeMap;

    public TemplateToken(FilePosition pos, Map attributes) {
        _attributeMap = attributes;
        _position = pos;
    }

    public Iterator/*<String>*/ getAttributeNames() { 
        return _attributeMap.keySet().iterator();
    }

    public String getAttribute( String name) { 
        return (String) _attributeMap.get(name);
    }

    public String getName() { 
        return getAttribute("name");
    }

    public String getModelName() {
        return getAttribute("pma:name");
    }

    public String getTagCaseValue() {
        return getAttribute("pma:case");
    }

    /**
     * filename attribute used by the TileNode.
     */
    public String getFilename() {
        return getAttribute("filename");
    }
    
    // FIXME: should not be here..
    public String getRaw() {
        return getAttribute("raw");
    }

    public FilePosition getFilePosition() {
        return _position;
    }
}
