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

import java.io.IOException;

/**
 * A Page is the compound of a PageModel and the data needed to
 * actually render it. This compound of Model and rendering data is
 * needed, since the PageModel is only a write-only model that allows
 * data to be written into, but not necessarily to read from it (getters
 * are not required). 
 *
 * @author Henner Zeller
 */
public interface Page {
    /**
     * returns the Model the data to be rendered can be written to. Setting
     * Fields in the Model stores data in this page.
     */
    PageModel getModel();

    /**
     * Render the page to the configured output. The implementation of
     * this Page is already configured for the sink to write to.
     */
    void render() throws IOException;
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
