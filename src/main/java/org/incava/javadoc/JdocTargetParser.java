package org.incava.javadoc;

import java.util.*;
import static org.incava.ijdk.util.IUtil.*;
import org.incava.text.TextLocation;


public class JdocTargetParser {

    private final JdocParser jdp;

    public JdocTargetParser(JdocParser jdp) {
        this.jdp = jdp;
    }

    public JdocElement parse() {
        // target is the next balanced thing after the tag, one of 
        // * @html <a href="http://www.foo.org/something.html">An HTML Target</a>
        // * @quoted "A Quoted Target"
        // * @word Word
        // * @parens something(with, parentheses)

        TextLocation tgtStart = this.jdp.getLocation();
        tr.Ace.log("tgtStart", tgtStart);

        TextLocation tgtEnd = this.jdp.getLocation();
        tr.Ace.log("tgtEnd", tgtEnd);

        JdocSubtargetParser subparser = null;
        tr.Ace.log("subparser", subparser);

        List<JdocSubtargetParser> subparsers = new ArrayList<JdocSubtargetParser>();
        subparsers.add(new JdocDoubleQuoteTargetParser(this.jdp));
        subparsers.add(new JdocHtmlTargetParser(this.jdp));
        subparsers.add(new JdocParenthesisTargetParser(this.jdp));
        
        boolean atStart = true;
        boolean inTarget = false;
        boolean atEnd = false;

        while (this.jdp.hasMore() && !atEnd && !matchesEnd()) {
            if (atStart && matchesStart()) {
                inTarget = true;
                tr.Ace.log("inTarget", "" + inTarget);
            }

            if (inTarget) {
                if (isTrue(subparser)) {
                    if (subparser.matchesEnd()) {
                        tgtEnd = this.jdp.getLocation();
                        tr.Ace.log("tgtEnd", tgtEnd);
                        atEnd = true;
                    }
                }
                else {
                    for (JdocSubtargetParser subp : subparsers) {
                        if (subp.matchesStart()) {
                            subparser = subp;
                            tr.Ace.log("subparser", subparser);
                            break;
                        }
                    }
                    
                    if (!isTrue(subparser)) {
                        if (this.jdp.isCurrentCharWhitespace()) {
                            atEnd = true;
                            tr.Ace.log("atEnd", "" + atEnd);

                            // skip advancing to the next char
                            break;
                        }
                        else {
                            tgtEnd = this.jdp.getLocation();
                            tr.Ace.log("tgtEnd", tgtEnd);
                        }
                    }
                }
            }
            this.jdp.gotoNextChar();
        }

        tr.Ace.log("tgtEnd", tgtEnd);
        tr.Ace.log("tgtStart", tgtStart);

        return new JdocElement(this.jdp.getString(), tgtStart, tgtEnd);
    }

    protected boolean matchesStart() {
        return !this.jdp.isCurrentCharCommentOrWhitespace();
    }

    protected boolean matchesEnd() {
        return this.jdp.atCommentEnd();
    }


}
