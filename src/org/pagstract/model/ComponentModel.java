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

import java.io.Serializable;

/**
 * Tagging interface for every component model used in Pagstract. A Component
 * is something that can be displayed.
 *
 * @author Henner Zeller
 */
public interface ComponentModel extends Serializable {
    /**
     * A boolean that conceptually enables/disables the function of this
     * component. For links, this could be that the link is not clickable
     * or a text input field is not editable.
     *
     * @return boolean that
     */
    boolean isEnabled();

    /**
     * a boolean switching the visibility of this component.
     */
    boolean isVisible();
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
