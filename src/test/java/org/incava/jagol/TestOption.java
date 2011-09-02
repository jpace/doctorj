package org.incava.jagol;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;


public class TestOption extends TestCase
{
    public TestOption(String name) {
        super(name);
    }

    public void testDefault() {
        Option opt = new Option("name", "this is the description of name") {
                public boolean set(String arg, List args) throws OptionException { return false; }
                public void setValue(String value) throws InvalidTypeException {}
            };
        assertEquals("name", opt.getLongName());
        assertEquals("this is the description of name", opt.getDescription());

        opt.setShortName('n');
        assertEquals('n', opt.getShortName());
    }

}

