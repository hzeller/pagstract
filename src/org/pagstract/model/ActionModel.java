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
package org.pagstract.model;

import java.util.Iterator;

/**
 * An Action needs parameters to function; these are reflected in dthis
 * interface.
 */
public interface ActionModel extends ComponentModel {
    // FIXME; beide die Teile geh�ren hier nicht hin
    String getUrl();
    String getAnchor();

    Iterator/*<String>*/ getParameterNames();
    String getParameter(String name);
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
