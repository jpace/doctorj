package org.incava.jagol;

import java.util.ArrayList;
import java.util.List;


public class TestBooleanOption extends AbstractJagolTestCase {
    private BooleanOption opt = new BooleanOption("boolopt", "this is the description of boolopt");

    public TestBooleanOption(String name) {
        super(name);
    }

    public void assertValue(Boolean exp, BooleanOption opt) {
        assertEquals("option value: " + opt.toString(), exp, opt.getValue());
    }

    public void testDefaultNull() {
        assertLongName("boolopt", opt);
        assertDescription("this is the description of boolopt", opt);
        assertValue(null, opt);
    }

    public void testDefaultValue() {
        BooleanOption opt = new BooleanOption("boolopt", "this is the description of boolopt", Boolean.TRUE);
        assertValue(Boolean.TRUE, opt);
    }

    public void testShortName() {
        opt.setShortName('n');
        assertShortName('n', opt);
    }

    public void testSetBooleanValue() {
        opt.setValue(Boolean.TRUE);
        assertValue(Boolean.TRUE, opt);

        opt.setValue(Boolean.FALSE);
        assertValue(Boolean.FALSE, opt);
    }

    public void testSetFromArgsListPositive() {
        List<String> args = new ArrayList<String>();
        try {
            boolean processed = opt.set("--boolopt", args);
            assertEquals("option processed", true, processed);
            assertValue(Boolean.TRUE, opt);
            assertEquals("argument removed from list", 0, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetFromArgsListNegativeDash() {
        List<String> args = new ArrayList<String>();
        try {
            boolean processed = opt.set("--no-boolopt", args);
            assertEquals("option processed", true, processed);
            assertValue(Boolean.FALSE, opt);
            assertEquals("argument removed from list", 0, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetFromArgsListNegativeNoDash() {
        List<String> args = new ArrayList<String>();
        try {
            boolean processed = opt.set("--noboolopt", args);
            assertEquals("option processed", true, processed);
            assertValue(Boolean.FALSE, opt);
            assertEquals("argument removed from list", 0, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

}
