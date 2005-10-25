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
package org.pagstract.test;

import junit.framework.TestCase;

import java.io.FileWriter;

import org.pagstract.Page;
import org.pagstract.PageFactory;
import org.pagstract.io.Device;
import org.pagstract.io.StringBufferDevice;

import org.pagstract.model.ActionModelAdapter;

import org.pagstract.view.template.FileTemplateResolver;
import org.pagstract.view.template.TemplatePageFactory;
import org.pagstract.view.template.PrefixResourceResolver;
import org.pagstract.view.template.RendererResolver;
import org.pagstract.view.template.ResourceResolver;

public class PagstractTest extends TestCase {

    private String executeTemplate(String template) {
        return "bal";
    }

    public void test_value() throws Exception {
        TemplateExecutor exec = new TemplateExecutor("xy<pma:value pma:name='stringValue'/>z");
        exec.getModel().setStringValue("foo");        
        assertEquals("xyfooz", exec.render());
    }

    public void test_message() throws Exception {
        TemplateExecutor exec = new TemplateExecutor("xmsg://foo ");
        assertEquals("xMESSAGE ", exec.render());

        exec = new TemplateExecutor("xmsg://foo"); // EOF-handling
        assertEquals("xMESSAGE", exec.render());

        exec = new TemplateExecutor("xmsg://foo/");
        assertEquals("xMESSAGE", exec.render());

        exec = new TemplateExecutor("xmsg://foo/ab");
        assertEquals("xMESSAGEab", exec.render());

        exec = new TemplateExecutor("xmsg://foo//");
        assertEquals("xMESSAGE/", exec.render());
    }

    public void test_resource() throws Exception {
        TemplateExecutor exec = new TemplateExecutor("<img src='resource://foo/bar/baz'/>");
        assertEquals("<img src='/foo/bar/baz'/>", exec.render());

        exec = new TemplateExecutor("<img src='resource://foo/bar/baz/'/>");
        assertEquals("<img src='/foo/bar/baz/'/>", exec.render());
    }

    private void listTemplateTest(String listTemplate, String expect) throws Exception {
        TemplateExecutor exec = new TemplateExecutor(listTemplate);
        FooBean[] list = new FooBean[2];
        list[0] = new FooBean("H");
        list[1] = new FooBean("Z");
        exec.getModel().setListValue(list);
        assertEquals(expect, exec.render());
    }

    public void test_list() throws Exception {
        listTemplateTest("<pma:list pma:name='listValue'></pma:list>", "");
        listTemplateTest("<pma:list pma:name='listValue'><pma:value pma:name='.foo'/></pma:list>", "HZ");
        listTemplateTest("<pma:list pma:name='listValue'><pma:content><pma:value pma:name='.foo'/></pma:content></pma:list>", "HZ");

        listTemplateTest("<pma:list pma:name='listValue'>"
                         +"<pma:header>HEAD</pma:header>"
                         +"<pma:content><pma:value pma:name='.foo'/></pma:content> "
                         +"<pma:separator>,</pma:separator>"
                         +"<pma:footer>FOOT</pma:footer></pma:list>", 
                         "HEADH,ZFOOT");

        listTemplateTest("<pma:list pma:name='listValue'>"
                         +"<pma:header></pma:header>"
                         +"<pma:content><pma:value pma:name='.foo'/></pma:content>"
                         +"<pma:separator></pma:separator>"
                         +"<pma:footer></pma:footer></pma:list>", 
                         "HZ");

        // separator inbetween
        listTemplateTest("<pma:list pma:name='listValue'>"
                         +"<pma:header></pma:header>"
                         +"<pma:content><pma:value pma:name='.foo'/></pma:content>"
                         +"<pma:separator>,</pma:separator>"
                         +"<pma:footer></pma:footer></pma:list>", 
                         "H,Z");

        // no separator at end of list
        listTemplateTest("<pma:list pma:name='listValue' count='1'>"
                         +"<pma:header></pma:header>"
                         +"<pma:content><pma:value pma:name='.foo'/></pma:content>"
                         +"<pma:separator>,</pma:separator>"
                         +"<pma:footer></pma:footer></pma:list>", 
                         "H");
    }

    public void test_action() throws Exception {
        TemplateExecutor exec = new TemplateExecutor("<a pma:name='actionValue'>foo</a>");

        String url = "http://foo.bar/";
        ActionModelAdapter action = new ActionModelAdapter(url, "get");
        exec.getModel().setActionValue(action);
        assertEquals("<a href=\"" + url + "\">foo</a>", exec.render());

        action.setEnabled(false);
        action.setVisible(true);
        assertEquals("foo", exec.render());

        action.setEnabled(true);
        action.setVisible(false);
        assertEquals("", exec.render());

        action.setEnabled(true);
        action.setVisible(true);

        /* with slash */
        exec = new TemplateExecutor("<a pma:name='actionValue' title='msg://foo/'>foo</a>");
        exec.getModel().setActionValue(action);
        assertEquals("<a href=\"" + url + "\" title=\"MESSAGE\">foo</a>", exec.render());

        /* without slash */
        exec = new TemplateExecutor("<a pma:name='actionValue' title='msg://foo'>foo</a>");
        exec.getModel().setActionValue(action);
        assertEquals("<a href=\"" + url + "\" title=\"MESSAGE\">foo</a>", exec.render());
    }

    public void test_comment() throws Exception {
        TemplateExecutor exec = new TemplateExecutor("foo<%-- bar --%>baz");
        assertEquals("foobaz", exec.render());
    }

    public void test_nestedObject() throws Exception {
        TemplateExecutor exec = new TemplateExecutor("<pma:switch pma:name='stringValue'>bar<object pma:case='foo-p'><object x='y'>foo</object></object></pma:switch>");
        exec.getModel().setStringValue("foo-p");
        assertEquals("<object x='y'>foo</object>", exec.render());

        exec = new TemplateExecutor("<pma:switch pma:name='stringValue'>bar<object pma:case='foo-p'><object>foo</object></object></pma:switch>");
        exec.getModel().setStringValue("foo-p");
        assertEquals("<object>foo</object>", exec.render());
    }

    /**
     * a Resource-Resolver, that resolves the only resource 'foo' to
     * 'MESSAGE'
     */
    private static final class DummyMessageResolver 
        implements ResourceResolver {
        public String resolveResource(String resourceName) throws Exception {
            if (!"foo".equals(resourceName)) {
                Thread.dumpStack();
                throw new Exception("invalid message; should be named 'foo', but got '"
                                     + resourceName + "'");
            }
            return "MESSAGE";
        }
    }

    private static final class DummyResourceResolver 
        implements ResourceResolver {
        public String resolveResource(String resourceName) throws Exception {
            if (!resourceName.startsWith("/")) {
                Thread.dumpStack();
                throw new Exception("invalid resource; got '" + resourceName + "' (does not start with '/')");
            }
            return resourceName;
        }
    }

    private static final class TemplateExecutor {
        private final StringBufferDevice _sbDev;
        private final Page _page;

        public TemplateExecutor(String template) throws Exception {
            FileWriter out = new FileWriter("/tmp/TestPage.html");
            out.write(template);
            out.close();

            _sbDev = new StringBufferDevice();
            FileTemplateResolver resolver = new FileTemplateResolver();
            ResourceResolver resourceResolver = new DummyResourceResolver();
            ResourceResolver messageResolver = new DummyMessageResolver();

            PageFactory factory 
                = new TemplatePageFactory(_sbDev, 
                                          new PrefixResourceResolver("/tmp"),
                                          resolver,
                                          null, resourceResolver,
                                          new RendererResolver(),
                                          messageResolver);
            
            _page = factory.createPageFor(TestPage.class);
        }

        public TestPage getModel() {
            return (TestPage) _page.getModel();
        }

        public String render() throws Exception {
            _sbDev.reset();
            _page.render();
            return _sbDev.toString();
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
