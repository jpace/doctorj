package org.incava.analysis;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;


public class TestViolation extends TestCase {

    public TestViolation(String name) {
        super(name);
    }

    public void testLineSameColumnSame() {
        Violation v1, v2;
        int comp;

        v1 = new Violation("test", 3, 3, 14, 8);
        v2 = new Violation("test", 3, 3, 14, 8);
        comp = v1.compareTo(v2);
        assertEquals("comparision value", 0, comp);
        assertTrue("equality", v1.equals(v2));
        assertTrue("equality", v2.equals(v1));

        v1 = new Violation("test", 12, 12, 14, 8);
        v2 = new Violation("test", 12, 12, 14, 8);
        comp = v1.compareTo(v2);
        assertEquals("comparision value", 0, comp);
        assertTrue("equality", v1.equals(v2));
        assertTrue("equality", v2.equals(v1));
    }

    public void testLineSameColumnDifferent() {
        Violation v1, v2;
        int comp;

        v1 = new Violation("test", 3, 3, 14, 8);
        v2 = new Violation("test", 3, 17, 14, 8);
        comp = v1.compareTo(v2);
        assertTrue("comparision value", comp < 0);
        assertFalse("equality", v1.equals(v2));
        assertFalse("equality", v2.equals(v1));

        comp = v2.compareTo(v1);
        assertTrue("comparision value", comp > 0);

        v1 = new Violation("test", 3, 17, 14, 8);
        v2 = new Violation("test", 3, 3, 14, 8);
        comp = v1.compareTo(v2);
        assertTrue("comparision value", comp > 0);        
        assertFalse("equality", v1.equals(v2));
        assertFalse("equality", v2.equals(v1));

        comp = v2.compareTo(v1);
        assertTrue("comparision value", comp < 0);        
    }

    public void testLineDifferentColumnSame() {
        Violation v1, v2;
        int comp;

        v1 = new Violation("test", 3, 3, 14, 8);
        v2 = new Violation("test", 17, 3, 14, 8);
        comp = v1.compareTo(v2);
        assertTrue("comparision value", comp < 0);
        assertFalse("equality", v1.equals(v2));
        assertFalse("equality", v2.equals(v1));

        comp = v2.compareTo(v1);
        assertTrue("comparision value", comp > 0);

        v1 = new Violation("test", 17, 3, 14, 8);
        v2 = new Violation("test", 3, 3, 14, 8);
        comp = v1.compareTo(v2);
        assertTrue("comparision value", comp > 0);        
        assertFalse("equality", v1.equals(v2));
        assertFalse("equality", v2.equals(v1));

        comp = v2.compareTo(v1);
        assertTrue("comparision value", comp < 0);        
    }

    public void testEndLineDifferent() {
        Violation v1, v2;
        int comp;

        v1 = new Violation("test", 3, 3, 3, 9);
        v2 = new Violation("test", 3, 3, 17, 9);
        comp = v1.compareTo(v2);
        assertTrue("comparision value", comp < 0);
        assertFalse("equality", v1.equals(v2));
        assertFalse("equality", v2.equals(v1));

        comp = v2.compareTo(v1);
        assertTrue("comparision value", comp > 0);

        v1 = new Violation("test", 3, 3, 17, 9);
        v2 = new Violation("test", 3, 3, 3, 9);
        comp = v1.compareTo(v2);
        assertTrue("comparision value", comp > 0);        
        assertFalse("equality", v1.equals(v2));
        assertFalse("equality", v2.equals(v1));

        comp = v2.compareTo(v1);
        assertTrue("comparision value", comp < 0);        
    }

    public void testEndColumnDifferent() {
        Violation v1, v2;
        int comp;

        v1 = new Violation("test", 3, 3, 3, 3);
        v2 = new Violation("test", 3, 3, 3, 17);
        comp = v1.compareTo(v2);
        assertTrue("comparision value", comp < 0);
        assertFalse("equality", v1.equals(v2));
        assertFalse("equality", v2.equals(v1));

        comp = v2.compareTo(v1);
        assertTrue("comparision value", comp > 0);

        v1 = new Violation("test", 3, 3, 3, 17);
        v2 = new Violation("test", 3, 3, 3, 3);
        comp = v1.compareTo(v2);
        assertTrue("comparision value", comp > 0);        
        assertFalse("equality", v1.equals(v2));
        assertFalse("equality", v2.equals(v1));

        comp = v2.compareTo(v1);
        assertTrue("comparision value", comp < 0);        
    }

}
