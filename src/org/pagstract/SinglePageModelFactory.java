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
package org.pagstract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The SinglePageModelFactory is a facade that helps make development
 * clean.</p>
 * <p>
 * Within your application you never want to know anything about the
 * way pages are rendered. That is part of the view-part of the application
 * (and as such, usually the task of the toplevel servlet). So you are not
 * interested in {@link Page}s, since their render()-Method is out of the scope
 * of the application providing the data to be rendered. So the applications
 * interest is to get hold of {@link PageModel} to fill -- thus passing
 * a PageModelFactor to the application is what you want. This class
 * implements a {@link PageModelFactory} to pass. It uses 
 * a {@link PageFactory} of your choice for this task.</p>
 * <p>
 * In a typical request/response application, that HTTP featured applications
 * are, only one page (=response) can be created in the course of a request.
 * This SinglePageModelFactory makes sure that this factory is only used
 * once to create a request. Later attempts will be recipocated with
 * an IllegalStateException.</p>
 * <p>
 *
 * An example of the intended use would be:
 * <pre>
   public class MyServlet {
      public doGet(HttpServletRequest req, HttpServletResponse resp) 
         throws IOException 
      {
         SinglePageModelFactory factory;
         factory= new SinglePageModelFactory(new JspPageFactory(req,resp,"/"));
         
         // pass the factory to the application
         callBusinessLogic(req, resp, factory);

         factory.render();
      }
   }
 * </pre>
 *
 * @author Henner Zeller
 */
public class SinglePageModelFactory implements PageModelFactory {
    private List/*<RenderListener>*/ _listeners;

    private final PageFactory _pageFactory;
    private final boolean _beStrict;
    private Page _page;
    private Class _modelClass;

    public SinglePageModelFactory(PageFactory pageFactory) {
        this(pageFactory, false);
    }

    public SinglePageModelFactory(PageFactory pageFactory, 
                                  boolean beStrict) 
    {
        _pageFactory = pageFactory;
        _beStrict = beStrict;
    }
    
    /**
     * returns the page factory, all factory calls are delegated.
     */
    public PageFactory getPageFactory() {
        return _pageFactory;
    }

    /**
     * creates a PageModel implementing the given class. This Factory makes
     * sure that you create a model only once.
     *
     * @param pageModelClass the interface class that is to be implemented.
     * @throws IllegalStateException if there was an attempt to create page 
     *                               more than once.
     */
    public PageModel createPageModelFor(Class pageModelClass) throws Exception{
        if (_page != null && _beStrict) {
            throw new IllegalStateException("cannot create page twice: createPageModel(" + pageModelClass.getName() + ") called; it has already been called with '"
                                            + _modelClass.getName() 
                                            + "' before.");
        }
        System.err.println("create page model for " + pageModelClass);
        _page = _pageFactory.createPageFor(pageModelClass);
        _modelClass = pageModelClass;
        return _page.getModel();
    }

    /**
     * returns the page created in the course of the lifetime of
     * this SinglePageModelFactory or 'null', of none has been created yet.
     */
    public Page getCreatedPage() {
        return _page;
    }

    /**
     * Add a render listener that is informed on rendering.
     */
    public void addRenderListener(RenderListener listener) {
        if (_listeners == null) {
            _listeners = new ArrayList();
        }
        _listeners.add(listener);
    }

    public void render() throws IOException {
        if (_page == null) {
            throw new IOException("page not created: createPageModelFor() was never called.");
        }
        if (_listeners != null) {
            Iterator it = _listeners.iterator();
            while (it.hasNext()) {
                RenderListener rl = (RenderListener) it.next();
                rl.beforeRendering(_page);
            }
        }

        _page.render();  // actual rendering.

        if (_listeners != null) {
            Iterator it = _listeners.iterator();
            while (it.hasNext()) {
                RenderListener rl = (RenderListener) it.next();
                rl.afterRendering(_page);
            }
        }
    }

    /**
     * returns the pageModel which is currently set in this factory. 
     *
     * @return the current PageModel
     * @throws IOException if no page has been created before this method was invoked.
     */
    public PageModel getCurrentPageModel() throws IOException {
	if (_page == null) {
            throw new IOException("page not created: createPageModelFor() was never called.");
	}
	return _page.getModel();
    }
    
    /**
     * return the model class or 'null', if there has no page be created
     * yet.
     */
    public Class getModelClass() {
        return _modelClass;
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
