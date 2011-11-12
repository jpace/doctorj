package org.incava.doctorj;

import java.util.List;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.Report;
import org.incava.javadoc.JavadocNode;
import org.incava.pmdx.CtorUtil;
import org.incava.pmdx.SimpleNodeUtil;

/**
 * Analyzes Javadoc and code for constructors.
 */
public class CtorDocAnalyzer extends FunctionDocAnalyzer {
    private final ASTConstructorDeclaration ctor;
    
    public CtorDocAnalyzer(Report r, ASTConstructorDeclaration ctor, int warningLevel) {
        super(r, ctor, warningLevel);        
        this.ctor = ctor;
    }

    public String getItemType() {
        return "constructor";
    }

    protected ASTFormalParameters getParameterList() {
        return CtorUtil.getParameters(this.ctor);
    }

    protected List<String> getValidTags() {
        return JavadocTags.getValidConstructorTags();
    }

    protected void addUndocumentedViolation(String desc) {
        Token nameTk = CtorUtil.getName(this.ctor);
        addViolation(desc, nameTk);
    }

    protected SimpleNode getEnclosingNode() {
        return SimpleNodeUtil.getParent(this.ctor);
    }
}
