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

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.pagstract.model.ActionModel;
import org.pagstract.model.SelectionListModel;
import org.pagstract.model.TextFieldModel;
import org.pagstract.view.namespace.Namespace;

/**
 * document me.
 */
public final class NamespaceTreeCellRenderer 
    extends DefaultTreeCellRenderer
    implements ComponentIcons {

    public Component getTreeCellRendererComponent(JTree tree, 
                                                  Object value,
                                                  boolean sel,
                                                  boolean expand, 
                                                  boolean leaf, 
                                                  int row,
                                                  boolean focus) 
    {
        JLabel label = (JLabel) super.getTreeCellRendererComponent(tree,
                                                                   value,
                                                                   sel,
                                                                   expand,
                                                                   leaf,
                                                                   row,
                                                                   focus);
        NamespaceTreeModelAdapter.NamespaceNode node;
        node = (NamespaceTreeModelAdapter.NamespaceNode) value;
        Namespace ns = node.getNamespace();
        if (ns == null) {
            label.setIcon( VALUE_ICON );
            Class cls = node.getModelClass();
            if (cls != null) {
                if (SelectionListModel.class.isAssignableFrom(cls)) {
                    label.setIcon( SELECT_ICON );
                }
                else if (TextFieldModel.class.isAssignableFrom(cls)) {
                    label.setIcon( INPUT_ICON );
                }
                else if (ActionModel.class.isAssignableFrom(cls)) {
                    label.setIcon( ACTION_ICON );
                }
            }
        }
        else {
            if (node.isIteratable()) {
                label.setIcon( ITERATE_ICON );
            }
            else {
                label.setIcon( BEAN_ICON );
            }
        }
        return label;
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
