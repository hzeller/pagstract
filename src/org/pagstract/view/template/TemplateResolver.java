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

/**
 * A Resolver that resolve abstract template names to its parsed
 * content. Templates may, for instance, be in the filesystem, then the
 * resource name is the filename.
 */
public interface TemplateResolver {
    /**
     * resolve a named template and return its parsed content.
     */
    TemplateNode resolveTemplate(String resourceName) throws Exception;

    /**
     * resolve a named template relative to another template.
     */
    TemplateNode resolveRelativeTemplate(String parentResource, 
                                         String relativeResource)
        throws Exception;

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
