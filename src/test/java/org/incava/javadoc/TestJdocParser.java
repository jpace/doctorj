package org.incava.javadoc;

import java.util.*;
import junit.framework.TestCase;
import org.incava.text.TextLocation;


public class TestJdocParser extends TestCase {

    public TestJdocParser(String name) {
        super(name);

        tr.Ace.setVerbose(true);
    }
    
    public void testAll() {
        JdocParser jdp = new JdocParser();
        tr.Ace.log("jdp", jdp);

        assertNotNull(jdp);
    }

}
