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
import java.util.Iterator;
import java.util.Map;

import org.pagstract.view.template.parser.ast.AnchorNode;
import org.pagstract.view.template.parser.ast.BeanNode;
import org.pagstract.view.template.parser.ast.ConstantNode;
import org.pagstract.view.template.parser.ast.FormNode;
import org.pagstract.view.template.parser.ast.InputFieldNode;
import org.pagstract.view.template.parser.ast.IteratorNode;
import org.pagstract.view.template.parser.ast.NodeSequence;
import org.pagstract.view.template.parser.ast.SelectFieldNode;
import org.pagstract.view.template.parser.ast.SwitchNode;
import org.pagstract.view.template.parser.ast.TemplateNode;
import org.pagstract.view.template.parser.ast.TileNode;
import org.pagstract.view.template.parser.ast.ValueNode;
import org.pagstract.view.template.parser.ast.IfVisibleNode;
import org.pagstract.view.template.parser.ast.ResourceNode;
import org.pagstract.view.template.parser.ast.Visitor;
import org.pagstract.view.template.parser.ast.DebugNode;
import org.pagstract.view.template.parser.scanner.FilePosition;
import org.pagstract.view.template.parser.scanner.TemplateToken;

/**
 * The JavaScript Generator visits the parse tree and extracts the elements a
 * JavaScript function should be generated for.
 *<p>
 It is possible in many browsers to genereate HTML with javascript methods
 that directly write() text as part of the to-be-rendered HTML. This requires
 to define basically a method like
<hr/><pre>
   fuction a() { document.write("&lt;b&gt;myText&lt;/b&gt;"); }
</pre><hr/>
 that later can be invoked <code>a()</code> to generate the 
 bold "myText" string.
 <p>
 Dynamic content can be embedded by using parameters that are internally
 string concatenated:
 <hr/><pre>
   fuction b(p) { document.write("the "+p+" is dynamic"); }
</pre><hr/>
 <p>
 These functions can be included in an external JavaScript resouce as well
 to be re-used later and only loaded once by the browser due to caching.
 <p>
 Basically using functions makes only sense if that method can be used 
 to compress the content to be transferred. If a function is only 
 used once in a document, using this technique is worse than writing the
 text directly: the definition of the function and the function call is more
 data than the text alone. 
 However, if the part generated by the javascript function can be re-used
 more than once in a session, the javascript function can loaded 
 from an external file; since the javascript file is cached by 
 the browser, this saves bandwith.
 <p>
 This method is most effective if used within iterators: the inner content
 of an iterator may be repeated many times so calling a function saves space
 each time:
<hr/><pre>
&lt;table&gt;
 &lt;script language="JavaScript"&gt;
   function a(p0, p1, p2) { 
     document.write("&lt;tr&gt;&lt;td&gt;"+p0+"&lt;/td&gt;&lt;td&gt;"+p1+"&lt;/td&gt;&lt;td&gt;"+p2+"&lt;/td&gt;&lt;/tr&gt;");
   }
   a("1", "Henner", "Zeller");
   a("2", "Manfred", "Hein");
   a("3", "J�rg", "Kirchof");
   a("4", "Stefan", "Richter");
 &lt;/script&gt;
&lt;/table&gt;
</pre><hr/>
 The dynamic parts are passed as parameter.
<p>
 If a javascript optimized iterator contains another, inner iterator,
 that should be itself
 represented as javascript function; otherwise the content generated by that
 iterator is just a long string passed to the first one which is not 
 optimal:
 <hr/><pre>
  b("1", "Henner", 
    &quot;&lt;table&gt;&lt;tr&gt;&lt;td&gt;Java&lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;C&lt;/td&gt;&lt;/tr&gt;&lt;td&gt;PostScript&lt;/td&gt;&lt;/tr&gt;&quot;, "foo", "bar");
</pre><hr/>
 Another function that represents the inner loop saves more. 
 However, in that case the outer-loop-function must be splitted; with 
 appropriate function definitions for <code>b_begin()</code>, 
 <code>b_rest()</code> and <code>c()</code> this would be:
<hr/><pre>
  b_begin("1", "Henner");
  c("Java");
  c("C");
  c("PostScript");
  b_rest("foo", "bar");
</pre><hr/>
So, within a loop, all inner loops should be generated as function as well.

 */
public class JavaScriptGenerator implements Visitor {
    private static final String FUNCTION_PREFIX = "pagout";

    /**
     * maps Templates nodes to the name of the java script function
     * that genreates its content.
     */
    private final Map/*<TemplateNode,String>*/ _nodeFunctions;

    /**
     * The definitions of all functions.
     */
    private final StringBuffer _functionCode;

    private int _functionCount;
    private StringBuffer _currentBuffer;
    private int     _paramCount;
    private boolean _inIterator;

    /**
     * Generate JavaScript in the given functionCode buffer. Maps nodes
     * to function names.
     */
    public JavaScriptGenerator(StringBuffer functionCode, Map nodeFunctions) {
        _functionCode = functionCode;
        _functionCount = 0;
        _currentBuffer = null;
        _paramCount = 0;
        _nodeFunctions = nodeFunctions;
    }

    public void visit(NodeSequence node) throws Exception {
        Iterator it = node.getElements();
        while (it.hasNext()) {
            TemplateNode subnode = (TemplateNode) it.next();
            subnode.accept(this);
        }
    }

    public void visit(TileNode node) throws Exception {
        // FIXME: implement.
    }

    public void visit(ConstantNode node) throws Exception {
        if (_currentBuffer != null) {
            _currentBuffer.append(quote(node.getContentBuffer()));
        }
    }

    public void visit(IteratorNode node) throws Exception {
        TemplateToken tag = node.getTemplateToken();
        boolean createFunction = false;
        
        /*
         * avoid too long String arguments to functions. For example: not the
         * content of an inner loop. So warn about it.
         * FIXME: later, this should generate a subfunction like
         * pagout1_1().
         */
        if (_currentBuffer != null) {
            System.err.println(tag.getFilePosition()
                               + ": warning: nesting Iterator in JavasSript compressed area!");
        }
        
        if ("true".equals(tag.getAttribute("javascript"))) {
            createFunction = initializeFunction(tag.getFilePosition());
        }
        else {
            if (writeFunctionParam()) { // the whole iterator in one param ..
                return;
            }
        }

        try {
            TemplateNode content = node.getContent();
            if (content == null) {
                return;
            }
            
            content.accept(this);
        }
        finally {
            if (createFunction) {
                _nodeFunctions.put(node, finalizeFunction());
            }
        }
    }


    public void visit(ValueNode node) throws Exception {
        writeFunctionParam();
    }

    public void visit(ResourceNode node) throws Exception {
        writeFunctionParam();
    }

    public void visit(BeanNode node) throws Exception {
        TemplateToken tag = node.getTemplateToken();
        boolean createFunction = false;

        if ("true".equals(tag.getAttribute("javascript"))) {
            createFunction = initializeFunction(tag.getFilePosition());
        }

        try {
            TemplateNode content = node.getTemplateContent();
            if (content != null) {
                content.accept(this);
            }
        }
        finally {
            if (createFunction) {
                _nodeFunctions.put(node, finalizeFunction());
            }
        }
    }

    public void visit(FormNode node) throws Exception {
        TemplateNode content = node.getTemplateContent();
        if (content != null) {
            content.accept(this);
        }
    }

    public void visit(AnchorNode node) throws Exception {
        writeFunctionParam();
    }

    public void visit(InputFieldNode node) throws Exception {
        writeFunctionParam();
    }

    public void visit(SelectFieldNode node) throws Exception {
        writeFunctionParam();
    }

    public void visit(IfVisibleNode node) throws Exception {
        writeFunctionParam();
    }

    public void visit(DebugNode node) throws Exception {
    }

    public void visit(SwitchNode node) throws Exception {
        TemplateToken tag = node.getTemplateToken();
        boolean createFunctions = false;

        if ("true".equals(tag.getAttribute("javascript"))) {
            createFunctions = true;
        }
        else {
            /*
             * if we are within a function, then the result of the whole
             * switch statement must be embedded in a parameter. This is
             * because the different case-tags might genereate a different
             * number of parameters.
             */
            if (writeFunctionParam()) {
                return;
            }
        }

        /*
         * go throguh all contents. Start with the default node, then
         * go through all available named contents.
         */
        TemplateNode currentNode = node.getDefaultContent();
        Iterator nodeNameIt = node.getAvailableNames().iterator();
        boolean isFirst = true;
        boolean localFunctionCreated = false;

        while (isFirst || nodeNameIt.hasNext()) {
            if (!isFirst) {
                currentNode = node.getNamedContent((String) nodeNameIt.next());
            }
            if (createFunctions) {
                localFunctionCreated=initializeFunction(tag.getFilePosition());
            }
            try {
                if (currentNode != null) {
                    currentNode.accept(this);
                }
            }
            finally {
                if (createFunctions && localFunctionCreated) {
                    _nodeFunctions.put(currentNode, finalizeFunction());
                }
            }
            isFirst = false;
        }
    }

    /**
     * creates a function parameter from this value if we are currently
     * within a function.
     */
    private boolean writeFunctionParam() {
        if (_currentBuffer != null) {
            _currentBuffer.append("\"+p").append(_paramCount).append("+\"");
            ++_paramCount;
            return true;
        }
        return false;
    }

    /**
     * initializes a function; returns true if it worked..
     */
    private boolean initializeFunction(FilePosition pos) {
        if (_currentBuffer != null) {
            System.err.println(pos + ": attempt nest JavaScript optimized areas!");
            return false;
        }
        _currentBuffer = new StringBuffer();
        _paramCount = 0;
        return true;
    }

    private String finalizeFunction() throws IOException {
        if (_currentBuffer == null || _currentBuffer.length() == 0) {
            return null;
        }
        String functionName = FUNCTION_PREFIX + _functionCount; 
        _functionCode.append("function " + functionName + "(");
        for (int i=0; i < _paramCount; ++i) {
            _functionCode.append("p").append(i);
            if (i < _paramCount-1) {
                _functionCode.append(", ");
            }
        }
        _functionCode.append("){\n\tpagtext=\"");
        _functionCode.append(_currentBuffer.toString());
        _functionCode.append("\";\n");
        _functionCode.append("\tdocument.write(pagtext);\n}\n");
        ++_functionCount;
        _currentBuffer = null;

        return functionName;
    }

    /**
     * Quotes the given byte-array, interpreted as ISO-8859-1 encoded
     * String to a java-script parameter.
     */
    private String quote(byte buf[]) throws IOException {
        String out = new String(buf, "ISO-8859-1");
        return quote(out);
    }

    /**
     * Quotes the given String so that it can be used within a
     * JavaScript-String; i.e. it qoutes the special characters '\n', 
     * '"' and backslash.
     */
    static String quote(String in) {
        StringBuffer result = new StringBuffer();
        int len = in.length();
        for (int i=0; i < len; ++i) {
            char c = in.charAt(i);
            switch(c) {
            case '\n':
            case '\"':
            case '\\':
                result.append("\\");
            }
            result.append(c);
        }
        return result.toString();
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
