package org.incava.jagol;

import java.util.*;
import junit.framework.TestCase;


public class AbstractJagolTestCase extends TestCase {
    public AbstractJagolTestCase(String name) {
        super(name);
    }

    public void assertShortName(Character exp, Option opt) {
        assertEquals("option short name: " + opt.toString(), exp, opt.getShortName());
    }

    public void assertLongName(String exp, Option opt) {
        assertEquals("option short name: " + opt.toString(), exp, opt.getLongName());
    }

    public void assertDescription(String exp, Option opt) {
        assertEquals("option description: " + opt.toString(), exp, opt.getDescription());
    }

    public <VarType> void assertValue(VarType exp, Option<VarType> opt) {
        assertEquals("option value: " + opt.toString(), exp, opt.getValue());
    }

}
