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

import org.pagstract.model.ActionModel;

/**
 * Resolve the abstract URL given in the ActionModel to a 'real'
 * URL that is rendered into the Page.
 */
public interface ActionUrlProvider {
    String resolveUrl(ActionModel actionModel, String url);
}
