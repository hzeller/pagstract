package org.pagstract.view.template.parser.ast;

import org.pagstract.view.template.parser.scanner.FilePosition;

public interface TemplateNode {
    void accept(Visitor t) throws Exception;
    FilePosition getPosition();
}
