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
package org.pagstract.ide;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Icons used for the components.
 */
public interface ComponentIcons {
    final ClassLoader cl = ComponentIcons.class.getClassLoader();
    
    final Icon ITERATE_ICON = new ImageIcon(cl.getResource("org/pagstract/ide/icons/loop.png"));
    final Icon ACTION_ICON = new ImageIcon(cl.getResource("org/pagstract/ide/icons/action.png"));
    final Icon INPUT_ICON = new ImageIcon(cl.getResource("org/pagstract/ide/icons/inputfield.png"));
    final Icon SELECT_ICON = new ImageIcon(cl.getResource("org/pagstract/ide/icons/selectfield.png"));
    final Icon VALUE_ICON = new ImageIcon(cl.getResource("org/pagstract/ide/icons/value.png"));
    final Icon BEAN_ICON = new ImageIcon(cl.getResource("org/pagstract/ide/icons/bean.png"));
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
