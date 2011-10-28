package org.incava.doctorj;

import java.util.*;
import net.sourceforge.pmd.ast.*;
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
        
        tr.Ace.setVerbose(true);

        this.spellingAnalyzer = new SpellingAnalyzer();
        this.spellingAnalyzer.addDictionary("/home/jpace/proj/doctorj/etc/words.en_US");
    }

    public Object visit(SimpleJavaNode node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    public Object visit(ASTLiteral node, Object data) {
        String nodeStr = SimpleNodeUtil.toString(node);
        tr.Ace.log("nodeStr", nodeStr);

        Token st = node.getFirstToken();
        tr.Ace.log("st", st);

        if (isTrue(st) && StringExt.charAt(st.image, 0) == '"' && StringExt.charAt(st.image, -1) == '"') {
            tr.Ace.yellow("st", st);
            
            LineMapping lines = new LineMapping(st.image, st.beginLine, st.beginColumn);
            tr.Ace.log("lines", lines);

            this.spellingAnalyzer.check(this.analyzer, lines, st.image);
        }

        return visit((SimpleJavaNode)node, data);
    }
}
