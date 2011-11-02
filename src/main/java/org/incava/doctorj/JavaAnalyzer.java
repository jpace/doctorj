package org.incava.doctorj;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.Report;



/**
 * Analyzes Javadoc and code.
 */
public class JavaAnalyzer extends JavaParserVisitorAdapter {

    private final Report report;

    private final Options options;

    public JavaAnalyzer(Report r, Options options) {
        this.report = r;
        this.options = options;
    }

    public boolean checkComments() {
        return options.checkComments();
    }

    public boolean checkStrings() {
        return options.checkStrings();
    }
  
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (checkComments()) {
            TypeDocAnalyzer analyzer = null;
            if (node.isInterface()) {
                analyzer = new InterfaceDocAnalyzer(this.report, node, options.getWarningLevel());
            }
            else { 
                analyzer = new ClassDocAnalyzer(this.report, node, options.getWarningLevel());
            }
            analyzer.run();
        }
        return super.visit(node, data);
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        if (checkComments()) {
            FieldDocAnalyzer analyzer = new FieldDocAnalyzer(this.report, node, options.getWarningLevel());
            analyzer.run();
        }
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        if (checkComments()) {
            MethodDocAnalyzer analyzer = new MethodDocAnalyzer(this.report, node, options.getWarningLevel());
            analyzer.run();
        }
        return super.visit(node, data);
    }

    public Object visit(ASTConstructorDeclaration node, Object data) {
        if (checkComments()) {
            CtorDocAnalyzer analyzer = new CtorDocAnalyzer(this.report, node, options.getWarningLevel());
            analyzer.run();
        }
        return super.visit(node, data);
    }

    public Object visit(ASTLiteral node, Object data) {
        if (checkStrings()) {
            LiteralAnalyzer analyzer = new LiteralAnalyzer(this.report, node);
            analyzer.run();
        }
        return super.visit(node, data);
    }
}
