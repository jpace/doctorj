package org.incava.javadoc;

import java.util.*;
import static org.incava.ijdk.util.IUtil.*;


public class JdocHtmlTargetParser extends JdocSubtargetParser {

    public JdocHtmlTargetParser(JdocParser jdp) {
        super(jdp);
    }

    protected boolean matchesStart() {
        return this.jdp.currentCharIs('<') && this.jdp.nextCharIs('a');
    }

    protected boolean matchesEnd() {
        if (this.jdp.currentCharIs('a') && this.jdp.nextCharIs('>')) {
            this.jdp.gotoNextChar();
            return true;
        }
        else {
            return false;
        }
    }

}
