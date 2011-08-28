package org.incava.javadoc;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import org.incava.text.Location;


public class TestJavadocNode extends TestCase
{
    public TestJavadocNode(String name)
    {
        super(name);
    }

    public void testNone()
    {
        String      text = "";
        JavadocNode jd   = JavadocNode.parse(text, 1, 1);
        
        assertNull("no javadoc node for empty string", jd);
    }
    
    public void testEmpty()
    {
        JavadocNode jd;        

        jd = JavadocNode.parse("/**/", 1, 1);
        assertNotNull("javadoc node for empty javadoc comment", jd);
        assertNull("javadoc description", jd.getDescription());
        assertEquals("javadoc tagged comments", 0, jd.getTaggedComments().length);

        jd = JavadocNode.parse("/** */", 1, 1);
        assertNotNull("javadoc node for javadoc comment with one space", jd);
        assertNull("javadoc description", jd.getDescription());
        assertEquals("javadoc tagged comments", 0, jd.getTaggedComments().length);

        jd = JavadocNode.parse("/**     */", 1, 1);
        assertNotNull("javadoc node for javadoc comment with multiple spaces", jd);
        assertNull("javadoc description", jd.getDescription());
        assertEquals("javadoc tagged comments", 0, jd.getTaggedComments().length);
    }
    
    public void testDescribedSingleLine()
    {
        String text  = "/** This is a test. */";
        
        // starts at line 5, column 1:
        JavadocNode jd = JavadocNode.parse(text, 5, 1);

        assertNotNull("javadoc node", jd);
        
        JavadocDescriptionNode desc = jd.getDescription();
        assertNotNull("javadoc description",                          desc);
        assertEquals("javadoc description text", "This is a test.",   desc.description);
        assertEquals("javadoc start location",   new Location(5, 5),  desc.start);
        assertEquals("javadoc end location",     new Location(5, 20), desc.end);
        
        assertEquals("javadoc tagged comments",  0,                   jd.getTaggedComments().length);
    }
    
    public void testDescribedSeparateLine()
    {
        String text  = ("/** \n" +
                        " * This is a test. \n" +
                        " */");

        // starts at line 5, column 1:
        JavadocNode jd = JavadocNode.parse(text, 5, 1);

        assertNotNull("javadoc node", jd);
        
        JavadocDescriptionNode desc = jd.getDescription();
        assertNotNull("javadoc description",                          desc);
        assertEquals("javadoc description text", "This is a test.",   desc.description);
        assertEquals("javadoc start location",   new Location(6, 4),  desc.start);
        assertEquals("javadoc end location",     new Location(6, 19), desc.end);

        assertEquals("javadoc tagged comments", 0, jd.getTaggedComments().length);
    }

    public void testDescribedOneTagLine()
    {
        String text  = ("/** \n" +
                        " * This is a test. \n" +
                        " * @tag And that's a tag.\n" +
                        " */");

        // starts at line 5, column 1:
        JavadocNode jd = JavadocNode.parse(text, 5, 1);

        assertNotNull("javadoc node", jd);
        
        JavadocDescriptionNode desc = jd.getDescription();
        assertNotNull("javadoc description",                          desc);
        assertEquals("javadoc description text", "This is a test. ",  desc.description);
        assertEquals("javadoc start location",   new Location(6, 4),  desc.start);
        assertEquals("javadoc end location",     new Location(6, 20), desc.end);

        JavadocTaggedNode[] jtcs = jd.getTaggedComments();
        assertEquals("javadoc tagged comments",                      1,                        jtcs.length);
        assertNotNull("javadoc tagged comment #0",                                             jtcs[0]);
        assertEquals("javadoc tagged comment description text",      "@tag And that's a tag.", jtcs[0].text);
        assertEquals("javadoc tagged comment start location",        new Location(7, 4),       jtcs[0].start);
        assertEquals("javadoc tagged comment end location",          new Location(7, 26),      jtcs[0].end);
        assertNotNull("javadoc tagged comment #0 tag",                                         jtcs[0].getTag());
        assertEquals("javadoc tagged comment #0 tag text",           "@tag",                   jtcs[0].getTag().text);
        assertEquals("javadoc tagged comment #0 tag start location", new Location(7, 4),       jtcs[0].getTag().start);
        assertEquals("javadoc tagged comment #0 tag end location",   new Location(7, 7),       jtcs[0].getTag().end);
    }

    public void testDescribedTwoTagsMultiLines()
    {
        String text  = ("/** \n" +
                        " * This is a test. \n" +
                        " * @tag0 And that's a tag,\n" +
                        " * which is described on multiple lines.\n" +
                        " * \n" +
                        " * @tag1 And that's another tag,\n" +
                        " * also described on multiple lines.\n" +
                        " */");

        // starts at line 5, column 1:
        JavadocNode jd = JavadocNode.parse(text, 5, 1);

        assertNotNull("javadoc node", jd);

        JavadocDescriptionNode desc = jd.getDescription();
        assertNotNull("javadoc description", desc);
        assertEquals("javadoc description text", "This is a test. ",  desc.description);
        assertEquals("javadoc start location",   new Location(6, 4),  desc.start);
        assertEquals("javadoc end location",     new Location(6, 20), desc.end);

        JavadocTaggedNode[] jtcs = jd.getTaggedComments();
        assertEquals("javadoc tagged comments", 2, jtcs.length);

        assertNotNull("javadoc tagged comment #0", jtcs[0]);
        assertEquals("javadoc tagged comment description text", "@tag0 And that's a tag,\n * which is described on multiple lines.", jtcs[0].text);
        assertEquals("javadoc tagged comment start location", new Location(7, 4),  jtcs[0].start);
        assertEquals("javadoc tagged comment end location",   new Location(8, 41), jtcs[0].end);
        
        assertNotNull("javadoc tagged comment #0 tag", jtcs[0].getTag());
        assertEquals("javadoc tagged comment #0 tag text", "@tag0", jtcs[0].getTag().text);
        assertEquals("javadoc tagged comment #0 tag start location", new Location(7, 4), jtcs[0].getTag().start);
        assertEquals("javadoc tagged comment #0 tag end location",   new Location(7, 8), jtcs[0].getTag().end);

        assertNotNull("javadoc tagged comment #0 target", jtcs[0].getTarget());
        assertEquals("javadoc tagged comment #0 target text", "And", jtcs[0].getTarget().text);
        assertEquals("javadoc tagged comment #0 target start location", new Location(7, 10), jtcs[0].getTarget().start);
        assertEquals("javadoc tagged comment #0 target end location",   new Location(7, 12), jtcs[0].getTarget().end);
        
        assertNotNull("javadoc tagged comment #1 tag", jtcs[1].getTag());
        assertEquals("javadoc tagged comment #1 tag text", "@tag1", jtcs[1].getTag().text);
        assertEquals("javadoc tagged comment #1 tag start location", new Location(10, 4), jtcs[1].getTag().start);
        assertEquals("javadoc tagged comment #1 tag end location",   new Location(10, 8), jtcs[1].getTag().end);

        assertNotNull("javadoc tagged comment #1 target", jtcs[1].getTarget());
        assertEquals("javadoc tagged comment #1 target text", "And", jtcs[1].getTarget().text);
        assertEquals("javadoc tagged comment #1 target start location", new Location(10, 10), jtcs[1].getTarget().start);
        assertEquals("javadoc tagged comment #1 target end location",   new Location(10, 12), jtcs[1].getTarget().end);
    }
    
}
