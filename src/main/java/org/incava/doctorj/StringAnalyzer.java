package org.incava.doctorj;

import java.util.*;
import java.util.regex.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.Report;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.text.spell.*;


/**
 * Analyzes strings.
 */
public class StringAnalyzer extends JavaParserVisitorAdapter {

    private final Report report;

    private final ParsingSpellChecker sc;

    public StringAnalyzer(Report r) {
        report = r;
        tr.Ace.setVerbose(true);

        sc = new ParsingSpellChecker(new NoCaseSpellChecker());
        sc.addDictionary("/home/jpace/proj/doctorj/etc/words.en_US");
    }

    public Object visit(SimpleJavaNode node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    public Object visit(ASTLiteral node, Object data) {
        tr.Ace.yellow("node", node);
        SimpleNodeUtil.dump(node, "");
        String nodeStr = SimpleNodeUtil.toString(node);
        tr.Ace.log("nodeStr", nodeStr);

        String quotedStrPat = "\\A\"(.*)\"\\z";

        Pattern pat = Pattern.compile(quotedStrPat);
        Matcher mat = pat.matcher(nodeStr);
        tr.Ace.log("mat", mat);
        tr.Ace.log("mat.match?", "" + mat.matches());

        if (mat.matches()) {
            tr.Ace.log("mat.match?", "" + mat.matches());

            String content = mat.group(1);
            tr.Ace.log("content", content);

            sc.check(content);
        }

        return visit((SimpleJavaNode)node, data);
    }
}
