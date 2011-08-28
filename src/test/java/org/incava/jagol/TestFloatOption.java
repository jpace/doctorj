package org.incava.jagol;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;


public class TestFloatOption extends TestCase
{
    FloatOption opt = new FloatOption("fltopt", "this is the description of fltopt");

    public TestFloatOption(String name)
    {
        super(name);
    }

    public void testDefaultNull()
    {
        assertEquals("fltopt", opt.getLongName());
        assertEquals("this is the description of fltopt", opt.getDescription());

        assertNull("default value", opt.getValue());
    }

    public void testDefaultValue()
    {
        FloatOption opt = new FloatOption("fltopt", "this is the description of fltopt", new Float(10.12F));
        assertEquals("default value", new Float(10.12F), opt.getValue());
    }

    public void testShortName()
    {
        opt.setShortName('d');
        assertEquals('d', opt.getShortName());
    }

    public void testSetFloatValue()
    {
        opt.setValue(new Float(1.4F));
        assertEquals("option value", new Float(1.4F), opt.getValue());
    }

    public void testSetInvalidValueString()
    {
        try {
            opt.setValue("fred");
            fail("exception expected");
        }
        catch (InvalidTypeException ite) {
        }
    }

    public void testSetInvalidValue()
    {
        try {
            opt.setValue("1.4.8");
            fail("exception expected");
        }
        catch (InvalidTypeException ite) {
        }
    }

    public void testSetValidValueNegative()
    {
        try {
            opt.setValue("-9.87");
            assertEquals("option value", new Float(-9.87F), opt.getValue());
        }
        catch (InvalidTypeException ite) {
            fail("exception not expected");
        }
    }

    public void testSetValidValueNoLeadingZero()
    {
        try {
            opt.setValue(".87");
            assertEquals("option value", new Float(0.87F), opt.getValue());
        }
        catch (InvalidTypeException ite) {
            fail("exception not expected");
        }
    }

    public void testSetFromArgsListEqual()
    {
        List args = new ArrayList();
        try {
            boolean processed = opt.set("--fltopt=4.44", args);
            assertEquals("option processed", true, processed);
            assertEquals("option value", new Float(4.44F), opt.getValue());
            assertEquals("argument removed from list", 0, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetFromArgsListSeparateString()
    {
        List args = new ArrayList();
        args.add("41.82");
        try {
            boolean processed = opt.set("--fltopt", args);
            assertEquals("option processed", true, processed);
            assertEquals("option value", new Float(41.82F), opt.getValue());
            assertEquals("argument removed from list", 0, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetFromLongerArgsListEqual()
    {
        List args = new ArrayList();
        args.add("--anotheropt");
        try {
            boolean processed = opt.set("--fltopt=3.1415", args);
            assertEquals("option processed", true, processed);
            assertEquals("option value", new Float(3.1415F), opt.getValue());
            assertEquals("argument not removed from list", 1, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetFromLongerArgsListSeparateString()
    {
        List args = new ArrayList();
        args.add("1234.567890");
        args.add("--anotheropt");
        try {
            boolean processed = opt.set("--fltopt", args);
            assertEquals("option processed", true, processed);
            assertEquals("option value", new Float(1234.567890F), opt.getValue());
            assertEquals("argument removed from list", 1, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetInvalidValueDanglingEquals()
    {
        List args = new ArrayList();
        args.add("--anotheropt");
        try {
            boolean processed = opt.set("--fltopt=", args);
            fail("exception expected");
        }
        catch (OptionException ite) {
        }
    }

}
