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
package org.pagstract.view.namespace;

/**
 * A NamingContext
 */
public final class NamingContext {
    private final String _name;
    private final Namespace _namespace;

    NamingContext(Namespace ns, String name) {
        _namespace = ns;
        _name = name;
    }
    
    public Namespace getNamespace() {
        return _namespace;
    }

    public String getName() {
        return _name;
    }

    public Object getLeafObject() {
        return _namespace.getNamedObject(_name);
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
