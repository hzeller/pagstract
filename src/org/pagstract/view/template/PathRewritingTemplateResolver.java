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

import org.pagstract.view.template.parser.ast.TemplateNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A TemplateResolver that rewrites the path using a ResourceResolver
 * before passing to another template resolver.
 */
public class PathRewritingTemplateResolver implements TemplateResolver {
    private static final Log _log = LogFactory.getLog(TemplatePageEmitter.class);
    private final TemplateResolver _deligee;
    private final ResourceResolver _resourceResolver;

    public PathRewritingTemplateResolver(TemplateResolver deligee,
                                         ResourceResolver resource) {
        _deligee = deligee;
        _resourceResolver = resource;
    }


    public TemplateNode resolveTemplate(String resourceName) throws Exception {
        resourceName = PathUtil.normalizePath(resourceName);
        String rewritten = _resourceResolver.resolveResource(resourceName);
        _log.debug("rewrite '" + resourceName + "' to '" + rewritten + "'");
        return _deligee.resolveTemplate(rewritten);
    }

    public TemplateNode resolveRelativeTemplate(String parentResource, 
                                                String relativeResource) 
        throws Exception 
    {
        return resolveTemplate(PathUtil.combinePath(parentResource,
                                                    relativeResource));
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
