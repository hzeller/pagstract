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
package org.pagstract.view.template;

import org.pagstract.view.namespace.ListCursor;

/**
 * A List Cursor that wraps around an array.
 */
final class ArrayListCursor implements ListCursor {
    private final Object[] _array;
    private int _index;
    public ArrayListCursor(Object[] array) {
        _array = array;
        _index = -1;
    }
    
    public boolean moveForwardAndCheckHasNext() {
        ++_index;
        return (_index < _array.length);
    }
    
    public Object getCurrentObject() {
        return _array[ _index ];
    }
    
    public int getCurrentCount() {
        return _index + 1;
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
