#!/bin/sh
cd `dirname $0`
cat TemplateParser.cup | $JAVA_HOME/bin/java -cp ../lib/javacup-0.10j.jar java_cup.Main -parser TemplateParser

TARGETDIR=../build/src/org/pagstract/view/template/parser
mkdir -p $TARGETDIR
mv sym.java TemplateParser.java $TARGETDIR 
#EOF

