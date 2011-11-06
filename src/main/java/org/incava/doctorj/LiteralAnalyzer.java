package org.incava.doctorj;

import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.Analyzer;
import org.incava.analysis.Report;
import org.incava.ijdk.lang.StringExt;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.text.LineMapping;
import static org.incava.ijdk.util.IUtil.*;


/**
 * Analyzes literals.
 */
public class LiteralAnalyzer extends Analyzer {

    private final ASTLiteral node;

    public LiteralAnalyzer(Report r, ASTLiteral node) {
        super(r);
        this.node = node;
    }

    public void run() {
        String nodeStr = SimpleNodeUtil.toString(this.node);
        Token st = this.node.getFirstToken();

        if (isTrue(st) && StringExt.charAt(st.image, 0) == '"' && StringExt.charAt(st.image, -1) == '"') {
            String substr = StringExt.get(st.image, 1, -2);
            LineMapping lines = new LineMapping(substr, st.beginLine, st.beginColumn + 1);
            SpellingAnalyzer.getInstance().check(this, lines, substr);
        }
    }
}
