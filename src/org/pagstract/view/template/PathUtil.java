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

/**
 * utility classes to handle pathes.
 */
final class PathUtil {
    /**
     * combines a parent resource with its relative resource.
     * If the parent resource is null or the relative resource is an
     * absolute resource (starting with '/'), the relative resource
     * is returned as-is.
     * Otherwise recombination of /foo/bar.html + baz.html will
     * yield /foo/baz.html, but /foo/bar/ + baz.html yields /foo/bar/baz.html.
     */
    static String combinePath(String parentResource, String relativeResource) {
        String resourceName;
        if (parentResource == null || relativeResource.startsWith("/")) {
            resourceName = relativeResource;
        }
        else {
            if (!parentResource.endsWith("/")) { 
                // this is not a directory, so chop of resource
                int slashPos = parentResource.lastIndexOf("/");
                if (slashPos >= 0) {
                    parentResource = parentResource.substring(0, slashPos+1);
                }
                else {
                    parentResource = "";
                }
            }
            resourceName = parentResource + relativeResource;
        }
        return resourceName;
    }
    
    /**
     * normalizes a path: removes all ./ and ../ and multiple slashes ///
     * If ../ go beyond root, just make it absoulte.
     */
    static String normalizePath(String file) {
        if (file == null) return null;
        final int len = file.length();
        StringBuffer result = new StringBuffer(len);
        int lastSlash = 0;
        // if we start with './' -> remove; with '../' -> make absolute: '/'
        while (file.startsWith("./") || file.startsWith("../")) {
            file = file.substring(2);
        }
        int i = 0;
        while (i < len) {
            if (file.charAt(i) == '/') {
                while (i < len-1 && file.charAt(i+1) == '/')
                    ++i;
                if (i < len-2 && file.charAt(i+1) == '.') {
                    switch (file.charAt(i+2)) {
                    case '/':    /*** /./ ***/
                        i += 2;
                        continue;
                    case '.':    /*** /../ ***/
                        if (i < len-3 && file.charAt(i+3) == '/') {
                            i+=3;
                        }
                        for (int j = result.length()-1; j >= 0; --j) {
                            if (j == 0 || result.charAt(j) == '/') {
                                result.setLength(j);
                                break;
                            }
                        }
                        continue;
                    }
                }
            }
            char c = file.charAt(i);
            result.append(c);
            if (c == '/') lastSlash = i;
            ++i;
        }
        return result.toString();
    }
}
