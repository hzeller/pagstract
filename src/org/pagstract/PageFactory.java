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
package org.pagstract;

/**
 * A PageFactory creates Pages.
 * The PageFactory creates {@link Page}s that are compounds of a 
 * {@link PageModel} and page content to be rendered. Different implementations
 * exist that utilize template mechanisms or Java Server Pages to create
 * appropriate pages.
 *
 * @author Henner Zeller
 */
public interface PageFactory {
    /**
     * creates a page for the given page model class.
     */
    Page createPageFor(Class pageModelClass) throws Exception;
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
