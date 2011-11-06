package org.incava.jagol;

import java.util.ArrayList;
import java.util.List;

public class TestIntegerOption extends AbstractJagolTestCase {
    private IntegerOption opt = new IntegerOption("intopt", "this is the description of intopt");

    public TestIntegerOption(String name) {
        super(name);
    }

    public void testDefaultNull() {
        assertLongName("intopt", opt);
        assertDescription("this is the description of intopt", opt);
        assertValue(null, opt);
    }

    public void testDefaultValue() {
        IntegerOption opt = new IntegerOption("intopt", "this is the description of intopt", new Integer(1012));
        assertValue(new Integer(1012), opt);
    }
    
    public void testSetIntegerValue() {
        opt.setValue(new Integer(14));
        assertValue(new Integer(14), opt);
    }

    public void testSetInvalidValueString() {
        try {
            opt.setValueFromString("fred");
            fail("exception expected");
        }
        catch (InvalidTypeException ite) {
        }
    }

    public void testSetInvalidValueFloatingPoint() {
        try {
            opt.setValueFromString("1.4");
            fail("exception expected");
        }
        catch (InvalidTypeException ite) {
        }
    }

    public void testSetValidValueNegative() {
        try {
            opt.setValueFromString("-987");
            assertValue(new Integer(-987), opt);
        }
        catch (InvalidTypeException ite) {
            fail("exception not expected");
        }
    }

    public void testSetFromArgsListEqual() {
        List<String> args = new ArrayList<String>();
        try {
            boolean processed = opt.set("--intopt=444", args);
            assertEquals("option processed", true, processed);
            assertValue(new Integer(444), opt);
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
            boolean processed = opt.set("--intopt", args);
            assertEquals("option processed", true, processed);
            assertValue(new Integer(41), opt);
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
            boolean processed = opt.set("--intopt=666", args);
            assertEquals("option processed", true, processed);
            assertValue(new Integer(666), opt);
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
            boolean processed = opt.set("--intopt", args);
            assertEquals("option processed", true, processed);
            assertValue(new Integer(1234), opt);
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
            boolean processed = opt.set("--intopt=", args);
            fail("exception expected");
        }
        catch (OptionException ite) {
        }
    }
}
