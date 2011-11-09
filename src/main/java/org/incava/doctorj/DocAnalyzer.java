package org.incava.doctorj;

import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.analysis.Analyzer;
import org.incava.analysis.Report;
import org.incava.pmdx.SimpleNodeUtil;

/**
 * Analyzes Javadoc and code.
 */
public class DocAnalyzer extends Analyzer {
    protected final static int CHKLVL_DOC_EXISTS = 1;
    protected final static int CHKLVL_TAG_CONTENT = 1;

    private final int warningLevel;

    public DocAnalyzer(Report r, int warningLevel) {
        super(r);
        this.warningLevel = warningLevel;
    }

    public int getWarningLevel() {
        return this.warningLevel;
    }

    public boolean isCheckable(SimpleNode node, int level) {
        int nodeLevel = SimpleNodeUtil.getLevel(node);
        return getWarningLevel() >= level + nodeLevel;
    }
}
