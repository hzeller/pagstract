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

import java.awt.Dimension;
import java.io.File;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.pagstract.view.template.TemplateResolver;

/**
 * document me.
 */
public class PageModelIdeFrame extends JFrame {
    private final JTabbedPane _templateSelector;

    public PageModelIdeFrame(int width, int height, 
                             ModelContainer models,
                             File baseDir, 
                             TemplateResolver cache,
                             final TemplateHttpServer server)
    {
        super("Page Model IDE");
        setSize(new Dimension(width, height));

        _templateSelector = new JTabbedPane();
        Iterator it = models.getModelNames();
        while (it.hasNext()) {
            String name = (String) it.next();
            Class cls = models.getPageModel(name);
            String templateFile = getTemplateNameFor(cls, baseDir);
            _templateSelector.add(name, new TemplatePanel(name, cls, cache,
                                                          templateFile));
        }
        server.setCurrentTemplate((TemplateSource)_templateSelector.getSelectedComponent());

        _templateSelector.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    TemplateSource source;
                    source = (TemplateSource) _templateSelector.getSelectedComponent();
                    server.setCurrentTemplate(source);
                }
            });
        getContentPane().add(_templateSelector);
    }

    private String getTemplateNameFor(Class c, File basePath) {
        String name = c.getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        return basePath.getAbsolutePath() + "/" + name + ".html";
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
