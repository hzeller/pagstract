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

import java.io.IOException;

import org.pagstract.io.Device;
import org.pagstract.model.SingleValueModel;
import org.pagstract.view.template.parser.ast.TemplateNode;
import org.pagstract.view.template.parser.ast.ValueNode;
import org.pagstract.view.template.parser.ast.Visitor;

/**
 * A Component Renderer is used in the TemplatePageEmitter to write
 * out components.
 */
public class ValueRenderer implements ComponentRenderer {
    public void render(Visitor renderVisitor,
                       TemplateNode node, 
                       Object value, Device out) 
        throws IOException 
    {
        ValueNode valNode = (ValueNode) node;
        if (value == null) {
            out.print("<!-- no value given for " 
                      + valNode.getModelName() + " -->");
            return;
        }
        String renderText;
        if (value instanceof SingleValueModel) {
            SingleValueModel model = (SingleValueModel) value;
            renderText = model.getValue();
        }
        else {
            renderText = value.toString();
        }

        if (renderText == null) {
            out.print("<!-- " + valNode.getModelName() + ": null -->");
        }

        if (valNode.writeAsRaw()) {
            out.print(renderText);
        }
        else {
            Utils.quote(out, renderText);
        }
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
