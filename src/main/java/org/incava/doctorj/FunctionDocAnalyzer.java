package org.incava.doctorj;

import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.Report;
import org.incava.javadoc.*;
import org.incava.pmdx.SimpleNodeUtil;


/**
 * Analyzes Javadoc and code for methods and constructors, AKA functions.
 */
public abstract class FunctionDocAnalyzer extends ItemDocAnalyzer {

    public final static String MSG_SERIALDATA_WITHOUT_DESCRIPTION = "@serialData without description";
    
    public FunctionDocAnalyzer(Report r, SimpleNode node) {
        super(r, node);
    }

    protected void checkJavadoc(JavadocNode javadoc) {
        super.checkJavadoc(javadoc);

        SimpleNode encNode = getEnclosingNode();
        int        chkLevel = SimpleNodeUtil.getLevel(encNode);

        ExceptionDocAnalyzer eda = new ExceptionDocAnalyzer(getReport(), javadoc, getNode(), chkLevel);
        eda.run();

        ASTFormalParameters  params = getParameterList();
        ParameterDocAnalyzer pda = new ParameterDocAnalyzer(getReport(), javadoc, getNode(), params, chkLevel);
        pda.run();

        if (javadoc == null) {
            tr.Ace.log("no javadoc");
        }
        else if (isCheckable(encNode, CHKLVL_TAG_CONTENT)) {
            JavadocTaggedNode[] taggedComments = javadoc.getTaggedComments();
            tr.Ace.log("taggedComments", taggedComments);
            
            for (int ti = 0; ti < taggedComments.length; ++ti) {
                JavadocTaggedNode jtn = taggedComments[ti];
                JavadocTag        tag = jtn.getTag();

                if (tag.text.equals(JavadocTags.SERIALDATA)) {
                    checkForTagDescription(jtn, MSG_SERIALDATA_WITHOUT_DESCRIPTION);
                }
            }
        }
        else {
            tr.Ace.log("skipping check for tag content");
        }
    }

    /**
     * Returns the parameter list for the function.
     */
    protected abstract ASTFormalParameters getParameterList();

}
