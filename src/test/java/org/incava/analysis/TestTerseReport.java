package org.incava.analysis;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;


public class TestTerseReport extends TestCase
{
    public TestTerseReport(String name)
    {
        super(name);
    }

    public void testReportOrder()
    {
        StringWriter sw;
        Report r;
        
        sw = new StringWriter();
        r = new TerseReport(sw);
        r.addViolation(new Violation("msg",  3, 5, 4, 6));
        r.addViolation(new Violation("msg2", 5, 3, 6, 4));
        assertEquals(2, r.getViolations().size());
        r.flush();
        
        String str0 = sw.toString();
        tr.Ace.log("str0: " + str0);

        sw = new StringWriter();
        r = new TerseReport(sw);
        r.addViolation(new Violation("msg2", 5, 3, 6, 4));
        r.addViolation(new Violation("msg",  3, 5, 4, 6));
        assertEquals(2, r.getViolations().size());
        r.flush();
        
        String str1 = sw.toString();
        tr.Ace.log("str1: " + str1);

        assertEquals("order of reported violations", str0, str1);
    }

    public void testOutput()
    {
        StringWriter sw = new StringWriter();
        Report r = new TerseReport(sw);
        r.addViolation(new Violation("msg",  3, 5, 4, 6));
        r.addViolation(new Violation("msg2", 5, 3, 6, 4));
        assertEquals(2, r.getViolations().size());
        r.flush();
        String str = sw.toString();
        tr.Ace.log("str: " + str);

        assertEquals("-:3:5:4:6: msg\n-:5:3:6:4: msg2\n", str);
    }
}

