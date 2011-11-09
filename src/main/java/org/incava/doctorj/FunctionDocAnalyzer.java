package org.incava.doctorj;

import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.analysis.Report;
import org.incava.javadoc.*;
import org.incava.pmdx.SimpleNodeUtil;
import static org.incava.ijdk.util.IUtil.*;

/**
 * Analyzes Javadoc and code for methods and constructors, AKA functions.
 */
public abstract class FunctionDocAnalyzer extends ItemDocAnalyzer {
    public final static String MSG_SERIALDATA_WITHOUT_DESCRIPTION = "@serialData without description";
    
    public FunctionDocAnalyzer(Report r, SimpleNode node, int warningLevel) {
        super(r, node, warningLevel);
    }

    protected void checkJavadoc(JavadocNode javadoc) {
        super.checkJavadoc(javadoc);

        SimpleNode encNode      = getEnclosingNode();
        int        chkLevel     = SimpleNodeUtil.getLevel(encNode);
        Report     report       = getReport();
        SimpleNode function     = getNode();
        int        warningLevel = getWarningLevel();

        ExceptionDocAnalyzer eda = new ExceptionDocAnalyzer(report, javadoc, function, chkLevel, warningLevel);
        eda.run();

        ASTFormalParameters  params = getParameterList();
        ParameterDocAnalyzer pda = new ParameterDocAnalyzer(report, javadoc, function, params, chkLevel, warningLevel);
        pda.run();

        if (isNotNull(javadoc) && isCheckable(encNode, CHKLVL_TAG_CONTENT)) {
            for (JavadocTaggedNode jtn : javadoc.getTaggedComments()) {
                JavadocTag tag = jtn.getTag();
                if (tag.textMatches(JavadocTags.SERIALDATA)) {
                    checkForTagDescription(jtn, MSG_SERIALDATA_WITHOUT_DESCRIPTION);
                }
            }
        }
    }

    /**
     * Returns the parameter list for the function.
     */
    protected abstract ASTFormalParameters getParameterList();
}
