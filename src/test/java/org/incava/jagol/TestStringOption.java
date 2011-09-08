package org.incava.jagol;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;


public class TestStringOption extends TestCase {
    
    StringOption opt = new StringOption("stropt", "this is the description of stropt");

    public TestStringOption(String name) {
        super(name);
    }

    public void testDefaultNull() {
        assertEquals("stropt", opt.getLongName());
        assertEquals("this is the description of stropt", opt.getDescription());

        assertNull("default value", opt.getValue());
    }

    public void testDefaultValue() {
        StringOption opt = new StringOption("stropt", "this is the description of stropt", "defval");
        assertEquals("default value", "defval", opt.getValue());
    }

    public void testShortName() {
        opt.setShortName('d');
        assertEquals('d', opt.getShortName());
    }

    public void testSetStringValue() {
        opt.setValue("krisiun");
        assertEquals("option value", "krisiun", opt.getValue());
    }

    public void testSetFromArgsListEqual() {
        List<String> args = new ArrayList<String>();
        try {
            boolean processed = opt.set("--stropt=hecate", args);
            assertEquals("option processed", true, processed);
            assertEquals("option value", "hecate", opt.getValue());
            assertEquals("argument removed from list", 0, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetFromArgsListSeparateString() {
        List<String> args = new ArrayList<String>();
        args.add("opeth");
        try {
            boolean processed = opt.set("--stropt", args);
            assertEquals("option processed", true, processed);
            assertEquals("option value", "opeth", opt.getValue());
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
            boolean processed = opt.set("--stropt=vader", args);
            assertEquals("option processed", true, processed);
            assertEquals("option value", "vader", opt.getValue());
            assertEquals("argument removed from list", 1, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetFromLongerArgsListSeparateString() {
        List<String> args = new ArrayList<String>();
        args.add("wham");
        args.add("--anotheropt");
        try {
            boolean processed = opt.set("--stropt", args);
            assertEquals("option processed", true, processed);
            assertEquals("option value", "wham", opt.getValue());
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
            boolean processed = opt.set("--stropt=", args);
            fail("exception expected");
        }
        catch (OptionException ite) {
        }
    }

}
