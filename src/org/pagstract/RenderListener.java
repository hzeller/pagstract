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
 * The RenderListener is a callback that is informed on rendering a
 * page.
 *
 * @author Henner Zeller
 */
public interface RenderListener {
    /**
     * callback that is called before the page is rendered.
     * Throwing an exception here effectively inhibits page rendering.
     */
    void beforeRendering(Page page) throws IOException;

    /**
     * callback that is called after the page is rendered.
     */
    void afterRendering(Page page) throws IOException;
}
 
