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
 * A simple Resolver that does a two-step resolve.
 */
public class StackingResourceResolver implements ResourceResolver {
    private final ResourceResolver _baseResolver;
    private final ResourceResolver _finalResolver;

    /**
     * creates a stacking resource resolver that first 
     * resolves the resource with the base resolve, then with the
     * final resolver.
     * @param finalResolver the resolver used after the base resolver
     *            has been used.
     * @param baseResolver the resolver providing the first translation.
     */
    public StackingResourceResolver(ResourceResolver finalResolver,
                                    ResourceResolver baseResolver)
    {
        _finalResolver = finalResolver;
        _baseResolver = baseResolver;
    }

    public String resolveResource(String resourceName) throws Exception {
        resourceName = _baseResolver.resolveResource(resourceName);
        return _finalResolver.resolveResource(resourceName);
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
