package org.incava.jagol;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;


public class TestNonBooleanOption extends TestCase {
    
    NonBooleanOption opt = new NonBooleanOption("nbopt", "this is the description of nbopt") {
            public void setValue(String value) throws InvalidTypeException {        
            }

            protected String getType() {
                return "";
            }
        };
    
    public TestNonBooleanOption(String name) {
        super(name);
    }

    public void testSetFromArgsListEqual() {
        List<String> args = new ArrayList<String>();
        try {
            boolean processed = opt.set("--nbopt=444", args);
            assertEquals("option processed", true, processed);
            assertEquals("argument removed from list", 0, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetFromArgsListSeparateString() {
        List<String> args = new ArrayList<String>();
        args.add("41");
        try {
            boolean processed = opt.set("--nbopt", args);
            assertEquals("option processed", true, processed);
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
            boolean processed = opt.set("--nbopt=666", args);
            assertEquals("option processed", true, processed);
            assertEquals("argument removed from list", 1, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetFromLongerArgsListSeparateString() {
        List<String> args = new ArrayList<String>();
        args.add("1234");
        args.add("--anotheropt");
        try {
            boolean processed = opt.set("--nbopt", args);
            assertEquals("option processed", true, processed);
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
            boolean processed = opt.set("--nbopt=", args);
            fail("exception expected");
        }
        catch (OptionException ite) {
        }
    }

}

