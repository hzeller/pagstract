package org.pagstract.view.template.parser.ast;

import org.pagstract.view.template.parser.scanner.FilePosition;

public interface NamedTemplateNode extends TemplateNode {
    String getModelName();
}
