package org.incava.doctorj;

import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.Report;
import org.incava.java.*;
import org.incava.pmdx.*;
import org.incava.javadoc.*;
import static org.incava.ijdk.util.IUtil.*;


/**
 * Analyzes Javadoc and code for methods.
 */
public class MethodDocAnalyzer extends FunctionDocAnalyzer {
    public final static String MSG_RETURN_WITHOUT_DESCRIPTION = "@return without description.";
    public final static String MSG_RETURN_FOR_VOID_METHOD = "@return for method returning void";
    public final static String MSG_RETURN_TYPE_USED = "@return refers to method return type";
    
    private final ASTMethodDeclaration method;
    
    public MethodDocAnalyzer(Report r, ASTMethodDeclaration method, int warningLevel) {
        super(r, method, warningLevel);
        this.method = method;
    }

    public String getItemType() {
        return "method";
    }

    /**
     * Returns the parent node, which is the enclosing declaration.
     */
    protected SimpleNode getEnclosingNode() {
        return SimpleNodeUtil.getParent(this.method);
    }

    protected void checkJavadoc(JavadocNode javadoc) {
        super.checkJavadoc(javadoc);

        for (JavadocTaggedNode jtn : iter(javadoc.getTaggedComments())) {
            JavadocTag tag = jtn.getTag();
            if (tag.textMatches(JavadocTags.RETURN)) {
                checkReturn(jtn);
            }
        }
    }

    protected Token getReturnToken() {
        ASTResultType resType = (ASTResultType)SimpleNodeUtil.findChild(this.method, ASTResultType.class);
        return resType.getFirstToken();
    }
    
    protected boolean checkReturnAgainstTarget(JavadocTaggedNode jtn) {
        Token      resTkn  = getReturnToken();
        JavadocElement tgt = jtn.getTarget();
        if (tgt == null) {
            if (isCheckable(getEnclosingNode(), CHKLVL_TAG_CONTENT)) {
                JavadocTag tag = jtn.getTag();
                addViolation(MSG_RETURN_WITHOUT_DESCRIPTION, tag.start, tag.end);
            }
        }
        else if (tgt.text.equals(resTkn.image)) {
            addViolation(MSG_RETURN_TYPE_USED, tgt.start, tgt.end);
        }
        return true;
    }

    protected void checkReturn(JavadocTaggedNode jtn) {
        JavadocTag tag     = jtn.getTag();
        Token      resTkn  = getReturnToken();
                    
        if (resTkn.kind == JavaParserConstants.VOID) {
            addViolation(MSG_RETURN_FOR_VOID_METHOD, tag.start, tag.end);
        }
        else {
            checkReturnAgainstTarget(jtn);
        }
    }

    /**
     * Returns the parameter list for the method.
     */
    protected ASTFormalParameters getParameterList() {
        return MethodUtil.getParameters(this.method);
    }

    /**
     * Returns the valid tags, as strings, for methods.
     */
    protected List<String> getValidTags() {
        return JavadocTags.getValidMethodTags();
    }

    /**
     * Adds a violation for a method, with the violation pointing to the method
     * name.
     */
    protected void addUndocumentedViolation(String desc) {
        Token nameTk = MethodUtil.getName(this.method);
        addViolation(desc, nameTk);
    }
}
