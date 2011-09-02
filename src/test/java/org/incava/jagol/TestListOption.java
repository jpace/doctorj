package org.incava.jagol;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;


public class TestListOption extends TestCase
{
    ListOption opt = new ListOption("listopt", "this is the description of listopt");

    public TestListOption(String name) {
        super(name);
    }

    public void testDefault() {
        assertEquals("listopt", opt.getLongName());
        assertEquals("this is the description of listopt", opt.getDescription());

        assertEquals("default value", (new ArrayList()), opt.getValue());
    }

    public void testShortName() {
        opt.setShortName('d');
        assertEquals('d', opt.getShortName());
    }

    public void testSetListValue() {
        List<String> v = new ArrayList<String>();
        v.add("dimmu");
        v.add("callendish");
        v.add("charon");
        
        opt.setValue(v);
        List values = opt.getValue();
        assertNotNull("list", values);
        assertEquals("list size",    3,        values.size());
        assertEquals("option value", v.get(0), values.get(0));
        assertEquals("option value", v.get(1), values.get(1));
        assertEquals("option value", v.get(2), values.get(2));
    }

    public void testSetFromArgsListEqual() {
        List<String> args = new ArrayList<String>();
        try {
            boolean processed = opt.set("--listopt=fee, fi, fo, fum", args);
            assertEquals("option processed", true, processed);
            List values = opt.getValue();
            assertEquals("list size", 4, values.size());
            assertEquals("option value", "fee", values.get(0));
            assertEquals("option value", "fi",  values.get(1));
            assertEquals("option value", "fo",  values.get(2));
            assertEquals("option value", "fum", values.get(3));
            assertEquals("argument removed from list", 0, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetFromArgsListSeparateList() {
        List<String> args = new ArrayList<String>();
        args.add("closing\nwinds");
        try {
            boolean processed = opt.set("--listopt", args);
            assertEquals("option processed", true, processed);
            List values = opt.getValue();
            assertEquals("list size", 2, values.size());
            assertEquals("option value", "closing", values.get(0));
            assertEquals("option value", "winds",  values.get(1));
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
            boolean processed = opt.set("--listopt=ord", args);
            assertEquals("option processed", true, processed);
            List values = opt.getValue();
            assertEquals("list size", 1, values.size());
            assertEquals("option value", "ord", values.get(0));
            assertEquals("argument removed from list", 1, args.size());
        }
        catch (OptionException ite) {
            fail("failure is not an option");
        }
    }

    public void testSetFromLongerArgsListSeparateList() {
        List<String> args = new ArrayList<String>();
        args.add("\"red blue\tgreen\"");
        args.add("--anotheropt");
        try {
            boolean processed = opt.set("--listopt", args);
            assertEquals("option processed", true, processed);
            List values = opt.getValue();
            assertEquals("list size", 3, values.size());
            assertEquals("option value", "red", values.get(0));
            assertEquals("option value", "blue", values.get(1));
            assertEquals("option value", "green", values.get(2));
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
            boolean processed = opt.set("--listopt=", args);
            fail("exception expected");
        }
        catch (OptionException ite) {
        }
    }

}
