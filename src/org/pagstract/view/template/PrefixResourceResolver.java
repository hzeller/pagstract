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

/**
 * A simple Resolver that prefices a resource with a base path.
 */
public class PrefixResourceResolver implements ResourceResolver {
    private final String _prefix;

    /**
     * creates a prefix resource resolver that prepends the given prefix
     * to to-be-resolved resources. Use this if you want to provide a
     * base path.
     */
    public PrefixResourceResolver(String prefix) {
        _prefix = prefix;
    }

    public String resolveResource(String resourceName) throws Exception {
        return _prefix + resourceName;
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
