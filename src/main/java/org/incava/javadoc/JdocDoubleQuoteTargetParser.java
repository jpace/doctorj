package org.incava.javadoc;

import java.util.*;
import static org.incava.ijdk.util.IUtil.*;


public class JdocDoubleQuoteTargetParser extends JdocSubtargetParser {

    public JdocDoubleQuoteTargetParser(JdocParser jdp) {
        super(jdp);
    }

    protected boolean matchesStart() {
        return this.jdp.currentCharIs('"');
    }

    protected boolean matchesEnd() {
        return this.jdp.currentCharIs('"');
    }

}
