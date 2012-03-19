package org.incava.javadoc;

import java.util.List;
import junit.framework.TestCase;
import org.incava.ijdk.text.Location;

public class TestJavadocNode extends TestCase {    
    public TestJavadocNode(String name) {
        super(name);
    }

    public void testNone() {
        String      text = "";
        JavadocNode jd = JavadocParser.parseJavadocNode(text, 1, 1);
        assertNull("no javadoc node for empty string", jd);
    }
    
    public void testEmpty() {
        JavadocNode jd;        

        jd = JavadocParser.parseJavadocNode("/**/", 1, 1);
        assertNotNull("javadoc node for empty javadoc comment", jd);
        assertNull("javadoc description", jd.getDescription());
        assertEquals("javadoc tagged comments", 0, jd.getTaggedComments().size());

        jd = JavadocParser.parseJavadocNode("/** */", 1, 1);
        assertNotNull("javadoc node for javadoc comment with one space", jd);
        assertNull("javadoc description", jd.getDescription());
        assertEquals("javadoc tagged comments", 0, jd.getTaggedComments().size());

        jd = JavadocParser.parseJavadocNode("/**     */", 1, 1);
        assertNotNull("javadoc node for javadoc comment with multiple spaces", jd);
        assertNull("javadoc description", jd.getDescription());
        assertEquals("javadoc tagged comments", 0, jd.getTaggedComments().size());
    }
    
    public void testDescribedSingleLine() {
        String text  = "/** This is a test. */";
        
        // starts at line 5, column 1:
        JavadocNode jd = JavadocParser.parseJavadocNode(text, 5, 1);

        assertNotNull("javadoc node", jd);
        
        JavadocDescriptionNode desc = jd.getDescription();
        assertNotNull("javadoc description",                          desc);
        assertEquals("javadoc description text", "This is a test.",   desc.description);
        assertEquals("javadoc start location",   new Location(5, 5),  desc.start);
        assertEquals("javadoc end location",     new Location(5, 20), desc.end);
        
        assertEquals("javadoc tagged comments",  0,                   jd.getTaggedComments().size());
    }
    
    public void testDescribedSeparateLine() {
        String text  = ("/** \n" +
                        " * This is a test. \n" +
                        " */");

        // starts at line 5, column 1:
        JavadocNode jd = JavadocParser.parseJavadocNode(text, 5, 1);

        assertNotNull("javadoc node", jd);
        
        JavadocDescriptionNode desc = jd.getDescription();
        assertNotNull("javadoc description",                          desc);
        assertEquals("javadoc description text", "This is a test.",   desc.description);
        assertEquals("javadoc start location",   new Location(6, 4),  desc.start);
        assertEquals("javadoc end location",     new Location(6, 19), desc.end);

        assertEquals("javadoc tagged comments", 0, jd.getTaggedComments().size());
    }

    public void testDescribedOneTagLine() {
        String text  = ("/** \n" +
                        " * This is a test. \n" +
                        " * @tag And that's a tag.\n" +
                        " */");

        // starts at line 5, column 1:
        JavadocNode jd = JavadocParser.parseJavadocNode(text, 5, 1);

        assertNotNull("javadoc node", jd);
        
        JavadocDescriptionNode desc = jd.getDescription();
        assertNotNull("javadoc description",                          desc);
        assertEquals("javadoc description text", "This is a test. ",  desc.description);
        assertEquals("javadoc start location",   new Location(6, 4),  desc.start);
        assertEquals("javadoc end location",     new Location(6, 20), desc.end);

        List<JavadocTaggedNode> jtcs = jd.getTaggedComments();
        assertEquals("javadoc tagged comments",                      1,                        jtcs.size());
        assertNotNull("javadoc tagged comment #0",                                             jtcs.get(0));
        assertEquals("javadoc tagged comment description text",      "@tag And that's a tag.", jtcs.get(0).text);
        assertEquals("javadoc tagged comment start location",        new Location(7, 4),       jtcs.get(0).start);
        assertEquals("javadoc tagged comment end location",          new Location(7, 26),      jtcs.get(0).end);
        assertNotNull("javadoc tagged comment #0 tag",                                         jtcs.get(0).getTag());
        assertEquals("javadoc tagged comment #0 tag text",           "@tag",                   jtcs.get(0).getTag().text);
        assertEquals("javadoc tagged comment #0 tag start location", new Location(7, 4),       jtcs.get(0).getTag().start);
        assertEquals("javadoc tagged comment #0 tag end location",   new Location(7, 7),       jtcs.get(0).getTag().end);
    }

    public void testDescribedTwoTagsMultiLines() {
        String text  = ("/** \n" +
                        " * This is a test. \n" +
                        " * @tag0 And that's a tag,\n" +
                        " * which is described on multiple lines.\n" +
                        " * \n" +
                        " * @tag1 And that's another tag,\n" +
                        " * also described on multiple lines.\n" +
                        " */");

        // starts at line 5, column 1:
        JavadocNode jd = JavadocParser.parseJavadocNode(text, 5, 1);

        assertNotNull("javadoc node", jd);

        JavadocDescriptionNode desc = jd.getDescription();
        assertNotNull("javadoc description", desc);
        assertEquals("javadoc description text", "This is a test. ",  desc.description);
        assertEquals("javadoc start location",   new Location(6, 4),  desc.start);
        assertEquals("javadoc end location",     new Location(6, 20), desc.end);

        List<JavadocTaggedNode> jtcs = jd.getTaggedComments();
        assertEquals("javadoc tagged comments", 2, jtcs.size());

        assertNotNull("javadoc tagged comment #0", jtcs.get(0));
        assertEquals("javadoc tagged comment description text", "@tag0 And that's a tag,\n * which is described on multiple lines.", jtcs.get(0).text);
        assertEquals("javadoc tagged comment start location", new Location(7, 4),  jtcs.get(0).start);
        assertEquals("javadoc tagged comment end location",   new Location(8, 41), jtcs.get(0).end);
        
        assertNotNull("javadoc tagged comment #0 tag", jtcs.get(0).getTag());
        assertEquals("javadoc tagged comment #0 tag text", "@tag0", jtcs.get(0).getTag().text);
        assertEquals("javadoc tagged comment #0 tag start location", new Location(7, 4), jtcs.get(0).getTag().start);
        assertEquals("javadoc tagged comment #0 tag end location",   new Location(7, 8), jtcs.get(0).getTag().end);

        assertNotNull("javadoc tagged comment #0 target", jtcs.get(0).getTarget());
        assertEquals("javadoc tagged comment #0 target text", "And", jtcs.get(0).getTarget().text);
        assertEquals("javadoc tagged comment #0 target start location", new Location(7, 10), jtcs.get(0).getTarget().start);
        assertEquals("javadoc tagged comment #0 target end location",   new Location(7, 12), jtcs.get(0).getTarget().end);
        
        assertNotNull("javadoc tagged comment #1 tag", jtcs.get(1).getTag());
        assertEquals("javadoc tagged comment #1 tag text", "@tag1", jtcs.get(1).getTag().text);
        assertEquals("javadoc tagged comment #1 tag start location", new Location(10, 4), jtcs.get(1).getTag().start);
        assertEquals("javadoc tagged comment #1 tag end location",   new Location(10, 8), jtcs.get(1).getTag().end);

        assertNotNull("javadoc tagged comment #1 target", jtcs.get(1).getTarget());
        assertEquals("javadoc tagged comment #1 target text", "And", jtcs.get(1).getTarget().text);
        assertEquals("javadoc tagged comment #1 target start location", new Location(10, 10), jtcs.get(1).getTarget().start);
        assertEquals("javadoc tagged comment #1 target end location",   new Location(10, 12), jtcs.get(1).getTarget().end);
    }
}
