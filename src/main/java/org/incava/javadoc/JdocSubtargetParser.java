package org.incava.javadoc;

import java.util.*;
import static org.incava.ijdk.util.IUtil.*;


public abstract class JdocSubtargetParser {

    protected final JdocParser jdp;

    public JdocSubtargetParser(JdocParser jdp) {
        this.jdp = jdp;
    }

    abstract protected boolean matchesStart();

    abstract protected boolean matchesEnd();

}
