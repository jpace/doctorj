package org.incava.doctorj;

import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.Report;
import org.incava.java.*;
import org.incava.pmd.*;
import org.incava.javadoc.*;


/**
 * Analyzes Javadoc and code for methods.
 */
public class MethodDocAnalyzer extends FunctionDocAnalyzer
{
    public final static String MSG_RETURN_WITHOUT_DESCRIPTION = "@return without description.";

    public final static String MSG_RETURN_FOR_VOID_METHOD = "@return for method returning void";

    public final static String MSG_RETURN_TYPE_USED = "@return refers to method return type";
    
    private ASTMethodDeclaration _method;
    
    public MethodDocAnalyzer(Report r, ASTMethodDeclaration method) {
        super(r, method);
        
        _method = method;
    }

    public String getItemType() {
        return "method";
    }

    /**
     * Returns the parent node, which is the enclosing declaration.
     */
    protected SimpleNode getEnclosingNode() {
        return SimpleNodeUtil.getParent(_method);
    }

    protected void checkJavadoc(JavadocNode javadoc) {
        tr.Ace.log("javadoc: " + javadoc);

        super.checkJavadoc(javadoc);

        if (javadoc != null) {
            JavadocTaggedNode[] taggedComments = javadoc.getTaggedComments();
            for (int ti = 0; ti < taggedComments.length; ++ti) {
                JavadocTaggedNode jtn = taggedComments[ti];
                JavadocTag        tag = jtn.getTag();
                tr.Ace.log("checking tag: " + tag);
                
                if (tag.text.equals(JavadocTags.RETURN)) {
                    ASTResultType resType = (ASTResultType)SimpleNodeUtil.findChild(_method, ASTResultType.class);
                    Token         resTkn = resType.getFirstToken();
                    
                    if (resTkn.kind == JavaParserConstants.VOID) {
                        addViolation(MSG_RETURN_FOR_VOID_METHOD, tag.start, tag.end);
                    }
                    else {
                        JavadocElement tgt = jtn.getTarget();
                        if (tgt == null) {
                            if (isCheckable(getEnclosingNode(), CHKLVL_TAG_CONTENT)) {
                                addViolation(MSG_RETURN_WITHOUT_DESCRIPTION, tag.start, tag.end);
                            }
                        }
                        else {
                            String text = tgt.text;
                            tr.Ace.log("text: '" + text + "'");
                            if (text.equals(resTkn.image)) {
                                addViolation(MSG_RETURN_TYPE_USED, tgt.start, tgt.end);
                            }
                        }
                    }
                }
            }
        }        
    }

    /**
     * Returns the parameter list for the method.
     */
    protected ASTFormalParameters getParameterList() {
        return MethodUtil.getParameters(_method);
    }

    /**
     * Returns the valid tags, as strings, for methods.
     */
    protected List getValidTags() {
        return JavadocTags.getValidMethodTags();
    }

    /**
     * Adds a violation for a method, with the violation pointing to the method
     * name.
     */
    protected void addUndocumentedViolation(String desc) {
        Token nameTk = MethodUtil.getName(_method);
        addViolation(desc, nameTk);
    }

}
