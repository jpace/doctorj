package org.incava.jagol;

import java.util.List;


public class TestOption extends AbstractJagolTestCase {
    public TestOption(String name) {
        super(name);
    }

    public void testDefault() {
        Option<String> opt = new Option<String>("name", "this is the description of name") {
                public boolean set(String arg, List<String> args) throws OptionException { return false; }
                public void setValueFromString(String value) throws InvalidTypeException {}
            };
        assertLongName("name", opt);
        assertDescription("this is the description of name", opt);
        
        opt.setShortName('n');
        assertShortName('n', opt);
    }
}
