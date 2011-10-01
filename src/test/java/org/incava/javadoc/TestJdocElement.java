package org.incava.javadoc;

import java.util.*;
import junit.framework.TestCase;
import org.incava.text.TextLocation;


public class TestJdocElement extends TestCase {

    public TestJdocElement(String name) {
        super(name);
    }

    public void testAll() {
        String comment = "/** this is a test. */";
        TextLocation startLoc = new TextLocation(4, 1, 5);
        TextLocation endLoc = new TextLocation(18, 1, 19);

        JdocElement jde = new JdocElement(comment, startLoc, endLoc);
        assertEquals(comment, jde.getComment());
        assertEquals(startLoc, jde.getStartLocation());
        assertEquals(endLoc, jde.getEndLocation());

        assertEquals("this is a test.", jde.getText());
    }

}
