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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;

import org.pagstract.view.template.FileTemplateResolver;
import org.pagstract.view.template.TemplateResolver;

/**
 * document me.
 */
public class PageModelIde {
    public static void main(String argv[]) throws Exception {
        if (argv.length < 2 || argv.length > 3) {
            usage();
            return;
        }

        File modelJarFile = new File(argv[0]);
        ModelContainer models = new JarModelContainer(modelJarFile);
                                                                     
        File templateDir = new File(argv[1]);
        File docRoot = (argv.length == 3) ? new File(argv[2]) : templateDir;

        if (!templateDir.isDirectory()) {
            usage();
            System.err.println(templateDir + ": not a directory");
        }

        if (!docRoot.isDirectory()) {
            usage();
            System.err.println(docRoot + ": not a directory");
        }

        TemplateResolver cache = new FileTemplateResolver();
        TemplateHttpServer server = new TemplateHttpServer(templateDir,
                                                           docRoot, cache,
                                                           1234);

        JFrame frame = new PageModelIdeFrame(900, 700, models, templateDir,
                                             cache, server);
        frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {System.exit(0);}
            });
        frame.show();
        server.serve();
    }

    private static void usage() {
        System.err.println("usage: PageModelIde <model.jar> <template-directory> [<document-root>]");
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
