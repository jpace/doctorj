package org.incava.jagol;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;


public class TestOptionSet extends TestCase {
    
    OptionSet optSet = new OptionSet("app", "this application does wonderful things");
        
    IntegerOption intOpt = new IntegerOption("intopt",            "this option takes an integer argument");
    StringOption  stringOpt = new StringOption("stringopt",      "this option takes a string argument");
    FloatOption   floatOpt = new FloatOption("floatopt",        "this option takes a float argument");
    DoubleOption  doubleOpt = new DoubleOption("doubleopt",      "this option takes a double argument");
    BooleanOption booleanOpt = new BooleanOption("booleanopt",    "this option takes a boolean argument");

    public TestOptionSet(String name) {
        super(name);

        tr.Ace.log("running");
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

        tr.Ace.log("done adding");
        
        String[] args = new String[] { "--intopt", "1", "--stringopt=two", "-f", "3.1415", "--no-booleanopt", "--doubleopt", "4.14" };
        
        args = optSet.process(args);

        assertEquals(new Integer(1),     intOpt.getValue());
        assertEquals("two",              stringOpt.getValue());
        assertEquals(new Float(3.1415F), floatOpt.getValue());
        assertEquals(new Double(4.14),   doubleOpt.getValue());
        assertEquals(Boolean.FALSE,      booleanOpt.getValue());
    }

    public void testCommandLineRemainingArgs() {
        tr.Ace.log("testing command line");

        tr.Ace.log("done adding");
        
        String[] args = new String[] {
            "--intopt", "1",
            "--stringopt=two",
            "-f", "3.1415",
            "--no-booleanopt",
            "--doubleopt", "4.14",
            "foo",
            "bar", 
            "baz"
        };
        
        args = optSet.process(args);

        assertEquals(new Integer(1),     intOpt.getValue());
        assertEquals("two",              stringOpt.getValue());
        assertEquals(new Float(3.1415F), floatOpt.getValue());
        assertEquals(new Double(4.14),   doubleOpt.getValue());
        assertEquals(Boolean.FALSE,      booleanOpt.getValue());

        assertEquals(3, args.length);
        assertEquals("foo", args[0]);
        assertEquals("bar", args[1]);
        assertEquals("baz", args[2]);
    }

    public void testUsage() {
        tr.Ace.log("testing usage");

        String[] args = new String[] { "--help" };
        optSet.addRunControlFile("/etc/TestOptionSet.conf");
        optSet.addRunControlFile("~/.TestOptionSet");
        
        optSet.process(args);
    }

    public void testConfig() {
        tr.Ace.log("testing config help");

        String[] args = new String[] { "--help-config" };
        optSet.addRunControlFile("/etc/TestOptionSet.conf");
        optSet.addRunControlFile("~/.TestOptionSet");
        
        optSet.process(args);
    }

    public void testRunControlFile() {
        tr.Ace.log("testing command line");

        tr.Ace.log("done adding");

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
            
            String[] args = new String[] { "app" };
        
            args = optSet.process(args);
            
            assertEquals(new Integer(999),      intOpt.getValue());
            assertEquals("april",               stringOpt.getValue());
            assertEquals(new Float(8.41F),      floatOpt.getValue());
            assertEquals(new Double(66.938432), doubleOpt.getValue());
            assertEquals(Boolean.FALSE,         booleanOpt.getValue());
            
            rcFile.delete();
        }
        catch (IOException ioe) {
            fail("exception not expected");
        }
    }

    public void testRunControlFileAndCommandLine() {
        tr.Ace.log("testing command line");

        tr.Ace.log("done adding");

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
            
            String[] args = new String[] { "--doubleopt=4.38", "--booleanopt" };
        
            args = optSet.process(args);
            
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

