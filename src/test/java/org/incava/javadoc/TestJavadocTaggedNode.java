package org.incava.javadoc;

import java.util.*;
import junit.framework.TestCase;
import org.incava.ijdk.text.Location;

public class TestJavadocTaggedNode extends TestCase {    
    public TestJavadocTaggedNode(String name) {
        super(name);
    }

    public static Location loc(int line, int column) {
        return new Location(line, column);
    }

    public static Location loc(Location location, String text) {
        return loc(location.getLine(), location.getColumn() + text.length() - 1);
    }

    public static Location loc(int line, int column, String text) {
        return loc(line, column + text.length() - 1);
    }

    public JavadocTaggedNode createTaggedNode(String text, Location start, Location end) {
        return JavadocParser.createTaggedNode(text, start, end);
    }

    public void assertTaggedNode(JavadocTaggedNode jtn, String expText, Location expStart, Location expEnd) {
        assertNotNull("javadoc tagged node", jtn);
        assertEquals("javadoc tagged comment description text", expText,  jtn.text);
        assertEquals("javadoc tagged comment start location",   expStart, jtn.start);
        assertEquals("javadoc tagged comment end location",     expEnd,   jtn.end);
    }

    public JavadocTaggedNode assertTaggedNode(String text, Location start, Location end) {
        JavadocTaggedNode jtn = createTaggedNode(text, start, end);
        assertTaggedNode(jtn, text, start, end);
        return jtn;
    }

    public void assertElement(JavadocElement elmt, String expText, Location expStart, Location expEnd) {
        assertNotNull("javadoc element", elmt);
        assertEquals("javadoc element text",           expText,  elmt.text);
        assertEquals("javadoc element start location", expStart, elmt.start);
        assertEquals("javadoc element end location",   expEnd,   elmt.end);
    }

    public void assertElement(JavadocElement elmt, String expText, Location expStart) {
        if (expText == null) {
            assertElementNull(elmt);
        }
        else {
            assertElement(elmt, expText, expStart, loc(expStart, expText));
        }
    }

    public void assertElementNull(JavadocElement elmt) {
        assertNull("javadoc element", elmt);
    }

    public void assertTaggedNode(String text, String tag, String tgt, String desc, String descNonTgt, Location start, Location tgtStart, Location descNonTgtStart) {
        Location          end = loc(start, text);
        JavadocTaggedNode jtn = assertTaggedNode(text, start, end);

        assertElement(jtn.getTag(), tag, start);
        assertElement(jtn.getTarget(), tgt, tgtStart);
        assertElement(jtn.getDescription(), desc, tgtStart);
        assertElement(jtn.getDescriptionNonTarget(), descNonTgt, descNonTgtStart);
    }

    public void assertTaggedNode(String text, String tag, String tgt, String descNonTgt, Location start, Location tgtStart, Location descNonTgtStart) {
        Location          end = loc(start, text);
        JavadocTaggedNode jtn = assertTaggedNode(text, start, end);

        assertElement(jtn.getTag(), tag, start);
        assertElement(jtn.getTarget(), tgt, tgtStart);
        // assertElement(jtn.getDescription(), desc, descStart);
        assertElement(jtn.getDescriptionNonTarget(), descNonTgt, descNonTgtStart);
    }

    public void assertTaggedNode(String text, String tag, String tgt, Location start, Location tgtStart) {
        assertTaggedNode(text, tag, tgt, null, start, tgtStart, null);
    }

    public void testTagOnly() {
        String tag = "@tag";
        String text = tag;
        assertTaggedNode(text, tag, null, loc(11, 4), null);
    }

    public void testTagNoFollowingText() {
        String tag = "@tag";
        String desc = "";
        String text = tag + " " + desc;
        assertTaggedNode(text, tag, null, loc(11, 4), null);
    }

    public void testOneLetter() {
        String tag = "@tag";
        String desc = "X";
        String text = tag + " " + desc;
        assertTaggedNode(text, tag, desc, loc(11, 4), loc(11, 9));
    }

    public void testOneLine() {
        String tag = "@tag";
        String tgt = "See";
        String descNt = "that thing for more.";
        String fullDesc = tgt + " " + descNt;
        String text = tag + " " + fullDesc;
        assertTaggedNode(text, tag, tgt, fullDesc, descNt, loc(11, 4), loc(11, 9), loc(11, 13));
    }

    public void testOneWord() {
        String tag  = "@tag";
        String tgt = "And";
        String descNt = "";
        String fullDesc = tgt + "" + descNt;
        String text = tag + " " + fullDesc;
        assertTaggedNode(text, tag, tgt, fullDesc, null, loc(11, 4), loc(11, 9), null);
    }

    public void testWordTarget() {
        String tag  = "@tag";
        String tgt  = "And";
        String desc = "that's a tag.";
        String text = tag + " " + tgt + " " + desc;
        assertTaggedNode(text, tag, tgt, desc, loc(11, 8), loc(11, 13), loc(11, 17));
    }

    public void testHTMLTarget() {
        String tag  = "@tag";
        String tgt  = "<a href=\"www.somewhere.tld/something/something\">this</a>";
        String desc = "See that thing for more.";
        String text = tag + " " + tgt + " " + desc;
        assertTaggedNode(text, tag, tgt, desc, loc(8, 3), loc(8, 8), loc(8, 65));
    }

    public void testHTMLTargetUppercase() {
        String tag  = "@tag";
        String tgt  = "<A HREF=\"www.somewhere.tld/something/something\">this</A>";
        String desc = "See that thing for more.";
        String text = tag + " " + tgt + " " + desc;
        assertTaggedNode(text, tag, tgt, desc, loc(8, 3), loc(8, 8), loc(8, 65));
    }

    public void testHTMLTargetNoEnd() {
        String tag  = "@tag";
        String tgt  = "<A HREF=\"www.somewhere.tld/something/something\">this";
        String text = tag + " " + tgt;
        assertTaggedNode(text, tag, tgt, loc(8, 3), loc(8, 8));
    }

    public void testQuotedTarget() {
        String tag  = "@tag";
        String tgt  = "Something#foo(int, double, float)";
        String text = tag + " " + tgt;
        assertTaggedNode(text, tag, tgt, loc(8, 3), loc(8, 8));
    }

    public void testQuotedTargetNoEnd() {
        String tag  = "@tag";
        String tgt  = "Something#foo(int, double";
        String text = tag + " " + tgt;
        assertTaggedNode(text, tag, tgt, loc(8, 3), loc(8, 8));
    }
}
