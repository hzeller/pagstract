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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * A JPanel, that handles the Layout stuff properly..
 */
public class ContentEditPanel extends JPanel {
    private final GridBagConstraints _constraints;
    private int row;
    private final GridBagLayout _layout;

    public ContentEditPanel() {
        _layout = new GridBagLayout();
        _constraints = new GridBagConstraints();
        _constraints.fill = GridBagConstraints.HORIZONTAL;
        row = 0;
        setLayout( _layout );
    }

    public void addSingleComponent(JComponent comp) {
        _constraints.gridwidth = 2;
        _constraints.gridy = row;
        _constraints.anchor = GridBagConstraints.WEST;

        _constraints.gridx = 0;
        _layout.setConstraints(comp, _constraints);
        add(comp);

        ++row;
    }

    public void addTwoComponents(JComponent comp1, JComponent comp2) {
        _constraints.gridwidth = 1;
        _constraints.gridy = row;
        _constraints.anchor = GridBagConstraints.WEST;

        _constraints.gridx = 0;
        _layout.setConstraints(comp1, _constraints);
        add(comp1);
        _constraints.gridx = 1;
        _layout.setConstraints(comp2, _constraints);
        add(comp2);

        ++row;
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
