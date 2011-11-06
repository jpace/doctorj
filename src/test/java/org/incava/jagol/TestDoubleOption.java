package org.incava.jagol;

import java.util.ArrayList;
import java.util.List;


public class TestDoubleOption extends AbstractJagolTestCase {
    private DoubleOption opt = new DoubleOption("dblopt", "this is the description of dblopt");

    public TestDoubleOption(String name) {
        super(name);
    }

    public void assertValue(Double exp, DoubleOption opt) {
        assertEquals("option value: " + opt.toString(), exp, opt.getValue());
    }

    public void testDefaultNull() {
        assertLongName("dblopt", opt);
        assertDescription("this is the description of dblopt", opt);
        assertValue(null, opt);
    }

    public void testDefaultValue() {
        DoubleOption opt = new DoubleOption("dblopt", "this is the description of dblopt", new Double(6.66));
        assertValue(new Double(6.66), opt);
    }

    public void testShortName() {
        opt.setShortName('d');
        assertShortName('d', opt);
    }

    public void testSetDoubleValue() {
        opt.setValue(new Double(1.4));
        assertValue(new Double(1.4), opt);
    }

    public void testSetInvalidValueString() {
        try {
            opt.setValueFromString("fred");
            fail("exception expected");
        }
        catch (InvalidTypeException ite) {
        }
    }

    public void testSetInvalidValue() {
        try {
            opt.setValueFromString("1.4.8");
            fail("exception expected");
        }
        catch (InvalidTypeException ite) {
        }
    }

    public void testSetValidValueNegative() {
        try {
            opt.setValueFromString("-9.87");
            assertValue(new Double(-9.87), opt);
        }
        catch (InvalidTypeException ite) {
            fail("exception not expected");
        }
    }

    public void testSetValidValueNoLeadingZero() {
        try {
            opt.setValueFromString(".87");
            assertValue(new Double(0.87), opt);
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
            assertValue(new Double(4.44), opt);
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
            assertValue(new Double(41.82), opt);
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
            assertValue(new Double(3.1415), opt);
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
            assertValue(new Double(1234.567890), opt);
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
