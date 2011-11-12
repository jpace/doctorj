package org.incava.javadoc;

import java.util.*;
import org.incava.text.TextLocation;
import static org.incava.ijdk.util.IUtil.*;

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
        TextLocation tgtEnd = this.jdp.getLocation();
        JdocSubtargetParser subparser = null;

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
            }

            if (inTarget) {
                if (isTrue(subparser)) {
                    if (subparser.matchesEnd()) {
                        tgtEnd = this.jdp.getLocation();
                        atEnd = true;
                    }
                }
                else {
                    for (JdocSubtargetParser subp : subparsers) {
                        if (subp.matchesStart()) {
                            subparser = subp;
                            break;
                        }
                    }
                    
                    if (!isTrue(subparser)) {
                        if (this.jdp.isCurrentCharWhitespace()) {
                            atEnd = true;

                            // skip advancing to the next char
                            break;
                        }
                        else {
                            tgtEnd = this.jdp.getLocation();
                        }
                    }
                }
            }
            this.jdp.gotoNextChar();
        }

        return new JdocElement(this.jdp.getString(), tgtStart, tgtEnd);
    }

    protected boolean matchesStart() {
        return !this.jdp.isCurrentCharCommentOrWhitespace();
    }

    protected boolean matchesEnd() {
        return this.jdp.atCommentEnd();
    }


}
