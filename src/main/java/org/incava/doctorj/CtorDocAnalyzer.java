package org.incava.doctorj;

import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.Report;
import org.incava.javadoc.JavadocNode;
import org.incava.pmd.CtorUtil;
import org.incava.pmd.SimpleNodeUtil;


/**
 * Analyzes Javadoc and code for constructors.
 */
public class CtorDocAnalyzer extends FunctionDocAnalyzer {

    private ASTConstructorDeclaration _ctor;
    
    public CtorDocAnalyzer(Report r, ASTConstructorDeclaration ctor) {
        super(r, ctor);
        
        _ctor = ctor;
    }

    public String getItemType() {
        return "constructor";
    }

    /**
     * Returns the throws list for the constructor.
     */
    protected ASTFormalParameters getParameterList() {
        return CtorUtil.getParameters(_ctor);
    }

    /**
     * Returns the valid tags, as strings, for ctors.
     */
    protected List getValidTags() {
        return JavadocTags.getValidConstructorTags();
    }

    /**
     * Adds a violation for a constructor, with the violation pointing to the
     * constructor name.
     */
    protected void addUndocumentedViolation(String desc) {
        Token nameTk = CtorUtil.getName(_ctor);
        addViolation(desc, nameTk);
    }

    protected SimpleNode getEnclosingNode() {
        return SimpleNodeUtil.getParent(_ctor);
    }

}
