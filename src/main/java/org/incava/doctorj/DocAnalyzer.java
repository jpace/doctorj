package org.incava.doctorj;

import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.Analyzer;
import org.incava.analysis.Report;
import org.incava.pmd.SimpleNodeUtil;


/**
 * Analyzes Javadoc and code.
 */
public class DocAnalyzer extends Analyzer
{
    protected final static int CHKLVL_DOC_EXISTS = 1;
    
    protected final static int CHKLVL_TAG_CONTENT = 1;

    public DocAnalyzer(Report r) {
        super(r);
    }

    public boolean isCheckable(SimpleNode node, int level) {
        int nodeLevel = SimpleNodeUtil.getLevel(node);
        return Options.warningLevel >= level + nodeLevel;
    }
    
}
