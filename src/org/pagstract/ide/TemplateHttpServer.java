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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.pagstract.io.Device;
import org.pagstract.io.OutputStreamDevice;
import org.pagstract.view.namespace.NameResolver;
import org.pagstract.view.namespace.Namespace;
import org.pagstract.view.template.TemplatePageEmitter;
import org.pagstract.view.template.TemplateResolver;
import org.pagstract.view.template.ResourceResolver;
import org.pagstract.view.template.RendererResolver;

/**
 * A super-simple HTTP-Server that answers any request
 * to '/' with the current Template; other requests are answered
 * with files resolved relative to the document root..
 */
public class TemplateHttpServer {
    private final static Map mimeMappings;
    static {
        mimeMappings = new HashMap();
        mimeMappings.put(".png",  "image/png");
        mimeMappings.put(".gif",  "image/gif");
        mimeMappings.put(".css",  "text/css");
        mimeMappings.put(".js",   "text/javascript");
        mimeMappings.put(".txt",  "text/plain");
        mimeMappings.put(".java", "text/plain");
        mimeMappings.put(".html", "text/html");
    }
    private final int _listenPort;
    private final File _docRoot;
    private final ResourceResolver _resourceResolver;
    private final String _templatePath;

    private TemplateSource _currentTemplate;
    private TemplateResolver _templateResolver;

    public TemplateHttpServer(File docRoot, String templatePath,
                              TemplateResolver templateResolver,
                              ResourceResolver resourceResolver,
                              int listenPort) 
    {
        _docRoot = docRoot;
        _listenPort = listenPort;
        _templateResolver = templateResolver;
        _resourceResolver = resourceResolver;
        _templatePath = templatePath;
    }

    public void serve() throws IOException {
        byte requestBuffer[] = new byte[ 8192 ];
        ServerSocket listenSocket = new ServerSocket(_listenPort);
        for (;;) {
            Socket clientConnection = listenSocket.accept();

            // read incoming stuff, but only extract query path.
            int reqLen = clientConnection.getInputStream().read(requestBuffer);

            String path = extractRequestPath(requestBuffer, reqLen);
            OutputStream os = clientConnection.getOutputStream();

            Device out = new OutputStreamDevice(os);
            try {
                if ("/".equals(path)) {
                    if (_currentTemplate != null) {
                        renderTemplate(out);
                    }
                    else {
                        out.print("<H2>No Template</H2>");
                    }
                }
                else {
                    serveFile(path, out);
                }
            }
            catch (Exception e) {
                out.print("Problem serving Template<br/>");
                out.print(e.getMessage());
                e.printStackTrace();
            }
            os.close();
        }
    }

    private String extractRequestPath(byte[] requestBuffer, int len) {
        StringBuffer path = new StringBuffer();
        char c;
        int pos = 0;
        while (pos < len) {
            c = (char) requestBuffer[pos++];
            if (c == ' ') {
                break;
            }
        }
        while (pos < len) {
            c = (char) requestBuffer[pos++];
            if (c == ' ' || c == '\n' || c == '\r') {
                break;
            }
            path.append(c);
        }
        //System.out.println("path is: " + path);
        return path.toString();
    }
    
    private void renderTemplate(Device out) throws Exception {
        out.print("HTTP/1.0 200 OK\r\n");
        out.print("Server: Pagstract-IDE\r\n");
        out.print("Connection: close\r\n");
        out.print("Content-Type: text/html\r\n");
        out.print("\r\n"); // close headers.

        Namespace rootNs = _currentTemplate.getRootNamespace();
        NameResolver resolver = new NameResolver(rootNs);
        
        TemplatePageEmitter emitter = 
            new TemplatePageEmitter(_docRoot.getCanonicalPath() 
                                    + "/" + _templatePath + "/",
                                    out, resolver, _templateResolver,
                                    null, _resourceResolver,
                                    new RendererResolver(), 
                                    null);
        long start = System.currentTimeMillis();
        _currentTemplate.getTemplateRootNode().accept(emitter);
        System.err.println("RenderTime: " 
                           + (System.currentTimeMillis()-start)
                           + "ms");
    }
    
    private void serveFile(String file, Device out) throws IOException {
        System.out.print(file);
        File serveFile = new File(_docRoot, file);
        if (!serveFile.exists()) {
            out.print("HTTP/1.0 404 Not Found\r\n");
            out.print("Content-Type: text/html\r\n");
            out.print("\r\n");
            out.print("<em>" + file + "</em>: not found.\n");
            System.out.println(" (NOT FOUND)");
            return;
        }
        
        out.print("HTTP/1.0 200 OK\r\n");
        out.print("Server: Pagstract-IDE\r\n");
        out.print("Connection: close\r\n");
        
        // some simple mime-mapping scheme.
        boolean foundContentType = false;
        Iterator it = mimeMappings.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry mimeEntry = (Map.Entry) it.next();
            String suffix = (String) mimeEntry.getKey();
            if (file.endsWith(suffix)) {
                out.print("Content-Type: " + mimeEntry.getValue() + "\r\n");
                foundContentType = true;
                break;
            }
        }
        if (!foundContentType) {
            out.print("Content-Type: application/octet-stream\r\n");
        }

        out.print("\r\n"); // close headers.
        
        byte[] buffer = new byte[ 8192 ];
        InputStream in = new FileInputStream(serveFile);
        int inBytes;
        while ((inBytes = in.read(buffer)) >= 0) {
            out.write(buffer, 0, inBytes);
        }

        System.out.println(" (delivered)");
    }

    public void setCurrentTemplate(TemplateSource ts) {
        _currentTemplate = ts;
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
