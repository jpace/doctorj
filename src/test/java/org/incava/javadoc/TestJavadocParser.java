package org.incava.javadoc;

import java.awt.Point;
import java.util.List;
import junit.framework.TestCase;


public class TestJavadocParser extends TestCase {

    public TestJavadocParser(String name) {
        super(name);

        tr.Ace.setVerbose(true);
    }

    public static List<Point> parse(String text) {
        return JavadocParser.parse(text);
    }

    public void testBasic() {
        String text  = ("/** This is a description.\n" +
                        "  */\n" +
                        "class Test {\n" +
                        "    /**\n" +
                        "      * This is a description. \n" +
                        "      * @@throws IOException  \n" +
                        "      */\n" +
                        "    int f(int i) throws IOException { return 1; }\n" +
                        "}\n");   
        List<Point> segments = parse(text);
        
        assertNotNull("results", segments);
    }

    public void testNone() {
        String text = "";
        List<Point> segments = parse(text);
        
        assertNull("results", segments);
    }

    public void testNotJavadoc() {
        String text = ("/* This is a description,\n" +
                       " * not in Javadoc format. */\n");
        List<Point> segments = parse(text);
        
        assertNull("results", segments);
    }
    
    public void testEmpty() {
        List<Point> segments = null;
        
        segments = parse("/**/");
        assertEquals("size of results", 0, segments.size());

        segments = parse("/** */");
        assertEquals("size of results", 0, segments.size());

        segments = parse("/**     */");
        assertEquals("size of results", 0, segments.size());
    }
    
    public void testDescribedSingleLine() {
        String text = "/** This is a test. */";
        List<Point> segments = parse(text);
        
        assertEquals("size of results",  1,                  segments.size());
        assertEquals("description",      (new Point(4, 19)), segments.get(0));
        assertEquals("description text", "This is a test.",  text.substring(4, 19));
    }
    
    public void testDescribedSeparateLine() {
        String text = "/** \n * This is a test. \n */";
        List<Point> segments = parse(text);
        
        assertEquals("size of results",  1,                  segments.size());
        assertEquals("description",      (new Point(8, 23)), segments.get(0));
        assertEquals("description text", "This is a test.",  text.substring(8, 23));
    }
    
    public void testDescribedMultiLineOnSeparateLine() {
        String text = "/** \n * This is a test.\n * There are many like it,\n   but this one is mine. \n */";
        List<Point> segments = parse(text);
        
        assertEquals("size of results",  1,                  segments.size());
        assertEquals("description",      (new Point(8, 75)), segments.get(0));
        assertEquals("description text", "This is a test.\n * There are many like it,\n   but this one is mine.",  text.substring(8, 75));
    }
    
    public void testDescribedOneTag() {
        String text = "/** \n * This is a test.\n * @tag description. \n */";
        List<Point> segments = parse(text);
        
        assertEquals("size of results",  2,                   segments.size());
        assertEquals("description",      (new Point(8, 23)),  segments.get(0));
        assertEquals("description text", "This is a test.",   text.substring(8, 23));
        assertEquals("tag text",         "@tag description.", text.substring(27, 44));
    }
    
    public void testUndescribedOneTag() {
        String text = "/** \n * @tag description. \n */";
        List<Point> segments = parse(text);
        
        assertEquals("size of results",  2,                   segments.size());
        assertNull("no description",     segments.get(0));
        assertEquals("tag",              (new Point(8, 25)),  segments.get(1));
        assertEquals("tag text",         "@tag description.", text.substring(8, 25));
    }
    
    public void testDescribedTwoTags() {
        String text  = "/** \n * This is a test.\n * @tag0 description. \n * @tag1 Another description, \n * this one on multiple lines.\n */";
        List<Point> segments = parse(text);
        
        assertEquals("size of results",  3,                    segments.size());
        assertEquals("description",      (new Point(8, 23)),   segments.get(0));
        assertEquals("description text", "This is a test.",    text.substring(8, 23));
        assertEquals("tag",              (new Point(27, 46)),  segments.get(1));
        assertEquals("tag text",         "@tag0 description. ", text.substring(27, 46));
        assertEquals("tag",              (new Point(50, 108)),  segments.get(2));
        assertEquals("tag text",         "@tag1 Another description, \n * this one on multiple lines.", text.substring(50, 108));
    }
    
    public void testDescribedTwoTagsEndCommentOnSameLine() {
        String text   = "/** \n * This is a test.\n * @tag0 description. \n * @tag1 Another description, \n * this one on multiple lines. */";
        List<Point> segments = parse(text);
        
        assertEquals("size of results",  3,                     segments.size());
        assertEquals("description",      (new Point(8, 23)),    segments.get(0));
        assertEquals("description text", "This is a test.",     text.substring(8, 23));
        assertEquals("tag",              (new Point(27, 46)),   segments.get(1));
        assertEquals("tag text",         "@tag0 description. ", text.substring(27, 46));
        assertEquals("tag",              (new Point(50, 108)),  segments.get(2));
        assertEquals("tag text",         "@tag1 Another description, \n * this one on multiple lines.", text.substring(50, 108));
    }
    
    public void testDescribedPreBlock() {
        String text  = "/** \n" +
            " * This is a test.\n" + 
            " * And here is its pre block: \n" +
            " * <pre> \n" +
            " *     blah\n" +
            " *     blah\n" +
            " *     blah\n" +
            " * </pre> \n" +
            " */";
        List<Point> segments = parse(text);
        
        assertEquals("size of results",  1,                   segments.size());
        assertEquals("description",      (new Point(8, 110)), segments.get(0));
        assertEquals("description text",
                     "This is a test.\n" + 
                     " * And here is its pre block: \n" +
                     " * <pre> \n" +
                     " *     blah\n" +
                     " *     blah\n" +
                     " *     blah\n" +
                     " * </pre>",    text.substring(8, 110));
    }

    public void testLineStartsWithLink() {
        String text = ("/**\n" +
                       " * This is a description that makes a reference to \n" +
                       " * {@link SomeWhere#someThing(someType)} which should not\n" +
                       " * be treated as a comment/tag start.\n" +
                       " */\n");
        
        List<Point> segments = parse(text);
        
        assertEquals("size of results",  1,                   segments.size());
        assertEquals("description",      (new Point(7, 151)), segments.get(0));
    }
}
