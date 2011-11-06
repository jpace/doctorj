package org.incava.jagol;

import java.io.*;
import java.util.*;

public class TestOptionSet extends AbstractJagolTestCase {    
    private final OptionSet optSet = new OptionSet("app", "this application does wonderful things");        
    private final IntegerOption intOpt = new IntegerOption("intopt",            "this option takes an integer argument");
    private final StringOption  stringOpt = new StringOption("stringopt",      "this option takes a string argument");
    private final FloatOption   floatOpt = new FloatOption("floatopt",        "this option takes a float argument", 'f');
    private final DoubleOption  doubleOpt = new DoubleOption("doubleopt",      "this option takes a double argument");
    private final BooleanOption booleanOpt = new BooleanOption("booleanopt",    "this option takes a boolean argument");

    public static List<String> createList(String ... args) {
        return Arrays.asList(args);
    }

    public TestOptionSet(String name) {
        super(name);
        tr.Ace.log("running");
    }

    protected List<String> process(String ... args) {
        return optSet.process(createList(args));
    }
    
    public void setUp() {
        tr.Ace.log("setting up");

        floatOpt.setShortName('f');
        
        optSet.addOption(intOpt);
        optSet.addOption(stringOpt);
        optSet.addOption(floatOpt);
        optSet.addOption(doubleOpt);
        optSet.addOption(booleanOpt);
    }

    public void testCommandLine() {
        tr.Ace.log("testing command line");
        
        List<String> unprocessed = process("--intopt", "1",
                                           "--stringopt=two",
                                           "-f", "3.1415",
                                           "--no-booleanopt",
                                           "--doubleopt", "4.14");

        assertValue(new Integer(1),     intOpt);
        assertValue("two",              stringOpt);
        assertValue(new Float(3.1415F), floatOpt);
        assertValue(new Double(4.14),   doubleOpt);
        assertValue(Boolean.FALSE,      booleanOpt);
    }

    public void testCommandLineRemainingArgs() {
        List<String> unprocessed = process("--intopt", "1",
                                           "--stringopt=two",
                                           "-f", "3.1415",
                                           "--no-booleanopt",
                                           "--doubleopt", "4.14",
                                           "foo",
                                           "bar", 
                                           "baz");

        assertValue(1,     intOpt);
        assertValue("two", stringOpt);
        assertValue(new Float(3.1415F), floatOpt);
        assertValue(new Double(4.14),   doubleOpt);
        assertValue(Boolean.FALSE,      booleanOpt);

        assertEquals(3, unprocessed.size());
        assertEquals("foo", unprocessed.get(0));
        assertEquals("bar", unprocessed.get(1));
        assertEquals("baz", unprocessed.get(2));
    }

    public void testUsage() {
        process("--help");
    }

    public void testConfig() {
        process("--help-config");
    }

    public void testRunControlFile() {
        try {
            String userHome = System.getProperty("user.home");
            String rcFileName = userHome + "/.TestOptionSet";
            File   rcFile = new File(rcFileName);
            
            Writer out = new BufferedWriter(new FileWriter(rcFile));
            out.write("intopt:     999\n");
            out.write("stringopt:  april\n");
            out.write("floatopt:   8.41\n");
            out.write("booleanopt: false\n");
            out.write("doubleopt:  66.938432\n");
            out.close();
            
            optSet.addRunControlFile("~/.TestOptionSet");
            
            process("app");
            
            assertValue(new Integer(999),      intOpt);
            assertValue("april",               stringOpt);
            assertValue(new Float(8.41F),      floatOpt);
            assertValue(new Double(66.938432), doubleOpt);
            assertValue(Boolean.FALSE,         booleanOpt);
            
            rcFile.delete();
        }
        catch (IOException ioe) {
            fail("exception not expected");
        }
    }

    public void testRunControlFileAndCommandLine() {
        try {
            String userHome = System.getProperty("user.home");
            String rcFileName = userHome + "/.TestOptionSet";
            File   rcFile = new File(rcFileName);
            
            Writer out = new BufferedWriter(new FileWriter(rcFile));
            out.write("intopt:     999\n");
            out.write("stringopt:  april\n");
            out.write("floatopt:   8.41\n");
            out.write("booleanopt: false\n");
            out.write("doubleopt:  66.938432\n");
            out.close();
            
            optSet.addRunControlFile("~/.TestOptionSet");
            
            process("--doubleopt=4.38", "--booleanopt");
            
            assertEquals(new Integer(999), intOpt.getValue());
            assertEquals("april",          stringOpt.getValue());
            assertEquals(new Float(8.41F), floatOpt.getValue());
            assertEquals(new Double(4.38), doubleOpt.getValue());
            assertEquals(Boolean.TRUE,     booleanOpt.getValue());
            
            rcFile.delete();
        }
        catch (IOException ioe) {
            fail("exception not expected");
        }
    }

}

