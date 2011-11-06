package org.incava.jagol;

import java.util.ArrayList;
import java.util.List;

public class TestStringOption extends AbstractJagolTestCase {
    private StringOption opt = new StringOption("stropt", "this is the description of stropt");

    public TestStringOption(String name) {
        super(name);
    }

    public void testDefaultNull() {
        assertLongName("stropt", opt);
        assertDescription("this is the description of stropt", opt);
        assertValue(null, opt);
    }

    public void testDefaultValue() {
        StringOption opt = new StringOption("stropt", "this is the description of stropt", "defval");
        assertValue("defval", opt);
    }

    public void testShortName() {
        opt.setShortName('d');
        assertShortName('d', opt);
    }

    public void testSetStringValue() {
        opt.setValue("krisiun");
        assertValue("krisiun", opt);
    }

    public void testSetFromArgsListEqual() {
        List<String> args = new ArrayList<String>();
        try {
            boolean processed = opt.set("--stropt=hecate", args);
            assertEquals("option processed", true, processed);
            assertValue("hecate", opt);
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
            assertValue("opeth", opt);
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
            assertValue("vader", opt);
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
            assertValue("wham", opt);
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
