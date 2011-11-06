package org.incava.jagol;

import java.util.ArrayList;
import java.util.List;


public class TestFloatOption extends AbstractJagolTestCase {
    private FloatOption opt = new FloatOption("fltopt", "this is the description of fltopt");

    public TestFloatOption(String name) {
        super(name);
    }

    public void testDefaultNull() {
        assertLongName("fltopt", opt);
        assertDescription("this is the description of fltopt", opt);
        assertValue(null, opt);
    }

    public void testDefaultValue() {
        FloatOption opt = new FloatOption("fltopt", "this is the description of fltopt", new Float(10.12F));
        assertValue(new Float(10.12F), opt);
    }

    public void testShortName() {
        opt.setShortName('d');
        assertShortName('d', opt);
    }

    public void testSetFloatValue() {
        opt.setValue(new Float(1.4F));
        assertValue(new Float(1.4F), opt);
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
            assertValue(new Float(-9.87F), opt);
        }
        catch (InvalidTypeException ite) {
            fail("exception not expected");
        }
    }

    public void testSetValidValueNoLeadingZero() {
        try {
            opt.setValueFromString(".87");
            assertValue(new Float(0.87F), opt);
        }
        catch (InvalidTypeException ite) {
            fail("exception not expected");
        }
    }

    public void testSetFromArgsListEqual() {
        List<String> args = new ArrayList<String>();
        try {
            boolean processed = opt.set("--fltopt=4.44", args);
            assertEquals("option processed", true, processed);
            assertValue(new Float(4.44F), opt);
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
            boolean processed = opt.set("--fltopt", args);
            assertEquals("option processed", true, processed);
            assertValue(new Float(41.82F), opt);
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
            boolean processed = opt.set("--fltopt=3.1415", args);
            assertEquals("option processed", true, processed);
            assertValue(new Float(3.1415F), opt);
            assertEquals("argument not removed from list", 1, args.size());
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
            boolean processed = opt.set("--fltopt", args);
            assertEquals("option processed", true, processed);
            assertValue(new Float(1234.567890F), opt);
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
            boolean processed = opt.set("--fltopt=", args);
            fail("exception expected");
        }
        catch (OptionException ite) {
        }
    }

}
