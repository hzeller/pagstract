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

/**
 * A RenderException is an IO-Exception that encapsulates another
 * exception.
 */
public class RenderException extends IOException {
    public RenderException(Throwable cause) {
        super(cause.getMessage());
        initCause(cause);
    }

    public RenderException(Throwable cause, String message) {
        super(cause.getMessage() + "; " + message);
        initCause(cause);
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
