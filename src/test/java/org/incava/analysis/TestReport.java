package org.incava.analysis;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;


public class TestReport extends TestCase
{
    public TestReport(String name)
    {
        super(name);
    }

    public void testFlush()
    {
        Report r = new Report(System.out) {
                protected String toString(Violation violation)
                {
                    return "";
                }
            };
        r.addViolation(new Violation("test",  3, 5, 4, 6));
        r.addViolation(new Violation("test2", 5, 3, 6, 4));
        assertEquals(2, r.getViolations().size());
        r.flush();
        assertEquals(0, r.getViolations().size());
    }
}

