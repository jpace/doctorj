package org.incava.doctorj;

import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.Analyzer;
import org.incava.analysis.Report;
import org.incava.ijdk.lang.StringExt;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.text.LineMapping;
import org.incava.text.spell.*;
import static org.incava.ijdk.util.IUtil.*;

/**
 * Analyzes strings.
 */
public class StringAnalyzer extends JavaParserVisitorAdapter {
    private final Analyzer analyzer;
    private final Report report;
    private final SpellingAnalyzer spellingAnalyzer;

    public StringAnalyzer(Report r) {
        this.report = r;
        this.analyzer = new Analyzer(r);

        this.spellingAnalyzer = new SpellingAnalyzer();
        this.spellingAnalyzer.addDictionary("/home/jpace/proj/doctorj/etc/words.en_US");
    }

    public Object visit(SimpleJavaNode node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    public Object visit(ASTLiteral node, Object data) {
        Token st = node.getFirstToken();
        if (isTrue(st) && StringExt.charAt(st.image, 0) == '"' && StringExt.charAt(st.image, -1) == '"') {
            String      substr = StringExt.get(st.image, 1, -2);
            LineMapping lines  = new LineMapping(substr, st.beginLine, st.beginColumn + 1);
            this.spellingAnalyzer.check(this.analyzer, lines, substr);
        }
        return visit((SimpleJavaNode)node, data);
    }
}
