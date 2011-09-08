package org.incava.jagol;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;


public class TestDoubleOption extends TestCase {
    
    DoubleOption opt = new DoubleOption("dblopt", "this is the description of dblopt");

    public TestDoubleOption(String name) {
        super(name);
    }

    public void testDefaultNull() {
        assertEquals("dblopt", opt.getLongName());
        assertEquals("this is the description of dblopt", opt.getDescription());

        assertNull("default value", opt.getValue());
    }

    public void testDefaultValue() {
        DoubleOption opt = new DoubleOption("dblopt", "this is the description of dblopt", new Double(6.66));
        assertEquals("default value", new Double(6.66), opt.getValue());
    }

    public void testShortName() {
        opt.setShortName('d');
        assertEquals('d', opt.getShortName());
    }

    public void testSetDoubleValue() {
        opt.setValue(new Double(1.4));
        assertEquals("option value", new Double(1.4), opt.getValue());
    }

    public void testSetInvalidValueString() {
        try {
            opt.setValue("fred");
            fail("exception expected");
        }
        catch (InvalidTypeException ite) {
        }
    }

    public void testSetInvalidValue() {
        try {
            opt.setValue("1.4.8");
            fail("exception expected");
        }
        catch (InvalidTypeException ite) {
        }
    }

    public void testSetValidValueNegative() {
        try {
            opt.setValue("-9.87");
            assertEquals("option value", new Double(-9.87), opt.getValue());
        }
        catch (InvalidTypeException ite) {
            fail("exception not expected");
        }
    }

    public void testSetValidValueNoLeadingZero() {
        try {
            opt.setValue(".87");
            assertEquals("option value", new Double(0.87), opt.getValue());
        }
        catch (InvalidTypeException ite) {
            fail("exception not expected");
        }
    }

    public void testSetFromArgsListEqual() {
        List<String> args = new ArrayList<String>();
        try {
            boolean processed = opt.set("--dblopt=4.44", args);
            assertEquals("option processed", true, processed);
            assertEquals("option value", new Double(4.44), opt.getValue());
            assertEquals("argument removed from list", 0, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetFromArgsListSeparateString() {
        List<String> args = new ArrayList<String>();
        args.add("41.82");
        try {
            boolean processed = opt.set("--dblopt", args);
            assertEquals("option processed", true, processed);
            assertEquals("option value", new Double(41.82), opt.getValue());
            assertEquals("argument removed from list", 0, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetFromLongerArgsListEqual() {
        List<String> args = new ArrayList<String>();
        args.add("--anotheropt");
        try {
            boolean processed = opt.set("--dblopt=3.1415", args);
            assertEquals("option processed", true, processed);
            assertEquals("option value", new Double(3.1415), opt.getValue());
            assertEquals("argument removed from list", 1, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetFromLongerArgsListSeparateString() {
        List<String> args = new ArrayList<String>();
        args.add("1234.567890");
        args.add("--anotheropt");
        try {
            boolean processed = opt.set("--dblopt", args);
            assertEquals("option processed", true, processed);
            assertEquals("option value", new Double(1234.567890), opt.getValue());
            assertEquals("argument removed from list", 1, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetInvalidValueDanglingEquals() {
        List<String> args = new ArrayList<String>();
        args.add("--anotheropt");
        try {
            boolean processed = opt.set("--dblopt=", args);
            fail("exception expected");
        }
        catch (OptionException ite) {
        }
    }

}
