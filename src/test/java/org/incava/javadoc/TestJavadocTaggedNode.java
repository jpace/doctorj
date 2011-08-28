package org.incava.javadoc;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import org.incava.text.Location;


public class TestJavadocTaggedNode extends TestCase
{
    public TestJavadocTaggedNode(String name)
    {
        super(name);
    }

    public void testTagOnly()
    {
        String text = "@tag";

        // starts at line 11, column 4:
        Location start = new Location(11, 4);
        Location end = new Location(11, 4 + text.length() - 1);
        JavadocTaggedNode jtn = new JavadocTaggedNode(text, start, end);

        assertNotNull("javadoc tagged node", jtn);
        assertEquals("javadoc tagged comment description text",      "@tag",  jtn.text);
        assertEquals("javadoc tagged comment start location",        new Location(11, 4),       jtn.start);
        assertEquals("javadoc tagged comment end location",          new Location(11, 7),       jtn.end);
        
        assertNotNull("javadoc tagged comment #0 tag",                                          jtn.getTag());
        assertEquals("javadoc tagged comment #0 tag text",           "@tag",                    jtn.getTag().text);
        assertEquals("javadoc tagged comment #0 tag start location", new Location(11, 4),       jtn.getTag().start);
        assertEquals("javadoc tagged comment #0 tag end location",   new Location(11, 7),       jtn.getTag().end);

        assertNull("javadoc tagged comment #0 description",                                          jtn.getDescription());
    }

    public void testTagNoFollowingText()
    {
        String text = "@tag ";

        // starts at line 11, column 4:
        Location start = new Location(11, 4);
        Location end = new Location(11, 4 + text.length() - 1);
        JavadocTaggedNode jtn = new JavadocTaggedNode(text, start, end);

        assertNotNull("javadoc tagged node", jtn);
        assertEquals("javadoc tagged comment description text",      "@tag ",  jtn.text);
        assertEquals("javadoc tagged comment start location",        new Location(11, 4),       jtn.start);
        assertEquals("javadoc tagged comment end location",          new Location(11, 8),       jtn.end);
        
        assertNotNull("javadoc tagged comment #0 tag",                                          jtn.getTag());
        assertEquals("javadoc tagged comment #0 tag text",           "@tag",                    jtn.getTag().text);
        assertEquals("javadoc tagged comment #0 tag start location", new Location(11, 4),       jtn.getTag().start);
        assertEquals("javadoc tagged comment #0 tag end location",   new Location(11, 7),       jtn.getTag().end);

        assertNull("javadoc tagged comment #0 description",                                          jtn.getDescription());
    }

    public void testOneLetter()
    {
        String text = "@tag X";

        // starts at line 11, column 4:
        Location start = new Location(11, 4);
        Location end = new Location(11, 4 + text.length() - 1);
        JavadocTaggedNode jtn = new JavadocTaggedNode(text, start, end);

        assertNotNull("javadoc tagged node", jtn);
        assertEquals("javadoc tagged comment description text",      "@tag X",  jtn.text);
        assertEquals("javadoc tagged comment start location",        new Location(11, 4),       jtn.start);
        assertEquals("javadoc tagged comment end location",          new Location(11, 9),       jtn.end);
        
        assertNotNull("javadoc tagged comment #0 tag",                                          jtn.getTag());
        assertEquals("javadoc tagged comment #0 tag text",           "@tag",                    jtn.getTag().text);
        assertEquals("javadoc tagged comment #0 tag start location", new Location(11, 4),       jtn.getTag().start);
        assertEquals("javadoc tagged comment #0 tag end location",   new Location(11, 7),       jtn.getTag().end);

        assertNotNull("javadoc tagged comment #0 description",                                          jtn.getDescription());
        assertEquals("javadoc tagged comment #0 description text",           "X",                       jtn.getDescription().text);
        assertEquals("javadoc tagged comment #0 description start location", new Location(11, 9),       jtn.getDescription().start);
        assertEquals("javadoc tagged comment #0 description end location",   new Location(11, 9),       jtn.getDescription().end);
    }

    public void testOneLine()
    {
        String text = "@tag And that's a tag.";

        // starts at line 11, column 4:
        Location start = new Location(11, 4);
        Location end = new Location(11, 4 + text.length() - 1);
        JavadocTaggedNode jtn = new JavadocTaggedNode(text, start, end);

        assertNotNull("javadoc tagged node", jtn);
        assertEquals("javadoc tagged comment description text",      "@tag And that's a tag.",  jtn.text);
        assertEquals("javadoc tagged comment start location",        new Location(11, 4),       jtn.start);
        assertEquals("javadoc tagged comment end location",          new Location(11, 25),      jtn.end);
        
        assertNotNull("javadoc tagged comment #0 tag",                                          jtn.getTag());
        assertEquals("javadoc tagged comment #0 tag text",           "@tag",                    jtn.getTag().text);
        assertEquals("javadoc tagged comment #0 tag start location", new Location(11, 4),       jtn.getTag().start);
        assertEquals("javadoc tagged comment #0 tag end location",   new Location(11, 7),       jtn.getTag().end);
    }

    public void testOneWord()
    {
        String text = "@tag And";

        // starts at line 11, column 4:
        Location start = new Location(11, 4);
        Location end = new Location(11, 4 + text.length() - 1);
        JavadocTaggedNode jtn = new JavadocTaggedNode(text, start, end);

        assertNotNull("javadoc tagged node", jtn);
        assertEquals("javadoc tagged comment description text",      "@tag And",  jtn.text);
        assertEquals("javadoc tagged comment start location",        new Location(11, 4),       jtn.start);
        assertEquals("javadoc tagged comment end location",          new Location(11, 11),      jtn.end);
        
        assertNotNull("javadoc tagged comment #0 tag",                                          jtn.getTag());
        assertEquals("javadoc tagged comment #0 tag text",           "@tag",                    jtn.getTag().text);
        assertEquals("javadoc tagged comment #0 tag start location", new Location(11, 4),       jtn.getTag().start);
        assertEquals("javadoc tagged comment #0 tag end location",   new Location(11, 7),       jtn.getTag().end);

        assertNotNull("javadoc tagged comment #0 description",                                          jtn.getDescription());
        assertEquals("javadoc tagged comment #0 description text",           "And",                     jtn.getDescription().text);
        assertEquals("javadoc tagged comment #0 description start location", new Location(11, 9),       jtn.getDescription().start);
        assertEquals("javadoc tagged comment #0 description end location",   new Location(11, 11),      jtn.getDescription().end);
    }

    public void testWordTarget()
    {
        String text = "@tag And that's a tag.";

        // starts at line 11, column 8:
        Location          start = new Location(11, 8);
        Location          end   = new Location(11, 8 + text.length() - 1);
        JavadocTaggedNode jtn   = new JavadocTaggedNode(text, start, end);

        assertNotNull("javadoc tagged node",                                                      jtn);
        assertEquals("javadoc tagged comment description text",         "@tag And that's a tag.", jtn.text);
        assertEquals("javadoc tagged comment start location",           new Location(11, 8),      jtn.start);
        assertEquals("javadoc tagged comment end location",             new Location(11, 29),     jtn.end);

        assertNotNull("javadoc tagged comment #0 tag",                                            jtn.getTag());
        assertEquals("javadoc tagged comment #0 tag text",              "@tag",                   jtn.getTag().text);
        assertEquals("javadoc tagged comment #0 tag start location",    new Location(11, 8),      jtn.getTag().start);
        assertEquals("javadoc tagged comment #0 tag end location",      new Location(11, 11),     jtn.getTag().end);

        assertNotNull("javadoc tagged comment #0 target",                                         jtn.getTarget());
        assertEquals("javadoc tagged comment #0 target text",           "And",                    jtn.getTarget().text);
        assertEquals("javadoc tagged comment #0 target start location", new Location(11, 13),     jtn.getTarget().start);
        assertEquals("javadoc tagged comment #0 target end location",   new Location(11, 15),     jtn.getTarget().end);
    }

    public void testHTMLTarget()
    {
        String text = "@tag <a href=\"www.somewhere.tld/something/something\">this</a> See that thing for more.";

        // starts at line 11, column 8:
        Location          start = new Location(8, 3);
        Location          end   = new Location(8, 3 + text.length() - 1);
        JavadocTaggedNode jtn   = new JavadocTaggedNode(text, start, end);

        assertNotNull("javadoc tagged node",                                                      jtn);
        assertEquals("javadoc tagged comment description text",         text,                     jtn.text);
        assertEquals("javadoc tagged comment start location",           new Location(8, 3),       jtn.start);
        assertEquals("javadoc tagged comment end location",             new Location(8, 88),      jtn.end);

        assertNotNull("javadoc tagged comment #0 tag",                                            jtn.getTag());
        assertEquals("javadoc tagged comment #0 tag text",              "@tag",                   jtn.getTag().text);
        assertEquals("javadoc tagged comment #0 tag start location",    new Location(8, 3),       jtn.getTag().start);
        assertEquals("javadoc tagged comment #0 tag end location",      new Location(8, 6),       jtn.getTag().end);

        assertNotNull("javadoc tagged comment #0 target",                                         jtn.getTarget());
        assertEquals("javadoc tagged comment #0 target text",           "<a href=\"www.somewhere.tld/something/something\">this</a>", jtn.getTarget().text);
        assertEquals("javadoc tagged comment #0 target start location", new Location(8, 8),       jtn.getTarget().start);
        assertEquals("javadoc tagged comment #0 target end location",   new Location(8, 63),      jtn.getTarget().end);

        assertNotNull("javadoc tagged comment #0 nontarget desc",                                 jtn.getDescriptionNonTarget());
        assertEquals("javadoc tagged comment #0 nontarget desc text",           "See that thing for more.", jtn.getDescriptionNonTarget().text);
        assertEquals("javadoc tagged comment #0 nontarget desc start location", new Location(8, 65),       jtn.getDescriptionNonTarget().start);
        assertEquals("javadoc tagged comment #0 nontarget desc end location",   new Location(8, 88),      jtn.getDescriptionNonTarget().end);
    }

    public void testHTMLTargetUppercase()
    {
        String text = "@tag <A HREF=\"www.somewhere.tld/something/something\">this</A> See that thing for more.";

        // starts at line 11, column 8:
        Location          start = new Location(8, 3);
        Location          end   = new Location(8, 3 + text.length() - 1);
        JavadocTaggedNode jtn   = new JavadocTaggedNode(text, start, end);

        assertNotNull("javadoc tagged node",                                                      jtn);
        assertEquals("javadoc tagged comment description text",         text,                     jtn.text);
        assertEquals("javadoc tagged comment start location",           new Location(8, 3),       jtn.start);
        assertEquals("javadoc tagged comment end location",             new Location(8, 88),      jtn.end);

        assertNotNull("javadoc tagged comment #0 tag",                                            jtn.getTag());
        assertEquals("javadoc tagged comment #0 tag text",              "@tag",                   jtn.getTag().text);
        assertEquals("javadoc tagged comment #0 tag start location",    new Location(8, 3),       jtn.getTag().start);
        assertEquals("javadoc tagged comment #0 tag end location",      new Location(8, 6),       jtn.getTag().end);

        assertNotNull("javadoc tagged comment #0 target",                                         jtn.getTarget());
        assertEquals("javadoc tagged comment #0 target text",           "<A HREF=\"www.somewhere.tld/something/something\">this</A>", jtn.getTarget().text);
        assertEquals("javadoc tagged comment #0 target start location", new Location(8, 8),       jtn.getTarget().start);
        assertEquals("javadoc tagged comment #0 target end location",   new Location(8, 63),      jtn.getTarget().end);
    }

    public void testHTMLTargetNoEnd()
    {
        String text = "@tag <A HREF=\"www.somewhere.tld/something/something\">this";

        // starts at line 11, column 8:
        Location          start = new Location(8, 3);
        Location          end   = new Location(8, 3 + text.length() - 1);
        JavadocTaggedNode jtn   = new JavadocTaggedNode(text, start, end);

        assertNotNull("javadoc tagged node",                                                      jtn);
        assertEquals("javadoc tagged comment description text",         text,                     jtn.text);
        assertEquals("javadoc tagged comment start location",           new Location(8, 3),       jtn.start);
        assertEquals("javadoc tagged comment end location",             new Location(8, 59),      jtn.end);

        assertNotNull("javadoc tagged comment #0 tag",                                            jtn.getTag());
        assertEquals("javadoc tagged comment #0 tag text",              "@tag",                   jtn.getTag().text);
        assertEquals("javadoc tagged comment #0 tag start location",    new Location(8, 3),       jtn.getTag().start);
        assertEquals("javadoc tagged comment #0 tag end location",      new Location(8, 6),       jtn.getTag().end);

        assertNotNull("javadoc tagged comment #0 target",                                         jtn.getTarget());
        assertEquals("javadoc tagged comment #0 target text",           "<A HREF=\"www.somewhere.tld/something/something\">this", jtn.getTarget().text);
        assertEquals("javadoc tagged comment #0 target start location", new Location(8, 8),       jtn.getTarget().start);
        assertEquals("javadoc tagged comment #0 target end location",   new Location(8, 59),      jtn.getTarget().end);
    }

    public void testQuotedTarget()
    {
        String text = "@tag Something#foo(int, double, float)";

        // starts at line 11, column 8:
        Location          start = new Location(8, 3);
        Location          end   = new Location(8, 3 + text.length() - 1);
        JavadocTaggedNode jtn   = new JavadocTaggedNode(text, start, end);

        assertNotNull("javadoc tagged node",                                                      jtn);
        assertEquals("javadoc tagged comment description text",         text,                     jtn.text);
        assertEquals("javadoc tagged comment start location",           start,                    jtn.start);
        assertEquals("javadoc tagged comment end location",             end,                      jtn.end);

        assertNotNull("javadoc tagged comment #0 tag",                                            jtn.getTag());
        assertEquals("javadoc tagged comment #0 tag text",              "@tag",                   jtn.getTag().text);
        assertEquals("javadoc tagged comment #0 tag start location",    start,                    jtn.getTag().start);
        assertEquals("javadoc tagged comment #0 tag end location",      new Location(start.line, start.column + 3), jtn.getTag().end);

        assertNotNull("javadoc tagged comment #0 target",                                         jtn.getTarget());
        assertEquals("javadoc tagged comment #0 target text",           "Something#foo(int, double, float)", jtn.getTarget().text);
        assertEquals("javadoc tagged comment #0 target start location", new Location(start.line, start.column + 5), jtn.getTarget().start);
        assertEquals("javadoc tagged comment #0 target end location",   new Location(start.line, start.column + 5 + 32), jtn.getTarget().end);
    }

    public void testQuotedTargetNoEnd()
    {
        String text = "@tag Something#foo(int, double";

        // starts at line 11, column 8:
        Location          start = new Location(8, 3);
        Location          end   = new Location(8, 3 + text.length() - 1);
        JavadocTaggedNode jtn   = new JavadocTaggedNode(text, start, end);

        assertNotNull("javadoc tagged node",                                                      jtn);
        assertEquals("javadoc tagged comment description text",         text,                     jtn.text);
        assertEquals("javadoc tagged comment start location",           start,                    jtn.start);
        assertEquals("javadoc tagged comment end location",             end,                      jtn.end);

        assertNotNull("javadoc tagged comment #0 tag",                                            jtn.getTag());
        assertEquals("javadoc tagged comment #0 tag text",              "@tag",                   jtn.getTag().text);
        assertEquals("javadoc tagged comment #0 tag start location",    start,                    jtn.getTag().start);
        assertEquals("javadoc tagged comment #0 tag end location",      new Location(start.line, start.column + 3), jtn.getTag().end);

        assertNotNull("javadoc tagged comment #0 target",                                         jtn.getTarget());
        assertEquals("javadoc tagged comment #0 target text",           "Something#foo(int, double", jtn.getTarget().text);
        assertEquals("javadoc tagged comment #0 target start location", new Location(start.line, start.column + 5), jtn.getTarget().start);
        assertEquals("javadoc tagged comment #0 target end location",   new Location(start.line, start.column + 5 + 24), jtn.getTarget().end);
    }

}
