package org.incava.doctorj;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import org.incava.analysis.Violation;


public class TestOptions extends AbstractDoctorJTestCase {
    
    public TestOptions(String name) {
        super(name);
    }

    public void runTest(int violationLevel, String[] contents, String[] messages, int[] beginLine, int[] beginColumn, int[] endLine, int[] endColumn) {
        for (int level = -1; level < 9; ++level) {
            tr.Ace.yellow("level: " + level);
            
            for (int ci = 0; ci < contents.length; ++ci) {
                String cont   = contents[ci];
                String msg    = messages.length    > 1 ? messages[ci]    : messages[0];
                int    bgLn   = beginLine.length   > 1 ? beginLine[ci]   : beginLine[0];
                int    bgCol  = beginColumn.length > 1 ? beginColumn[ci] : beginColumn[0];
                int    endLn  = endLine.length     > 1 ? endLine[ci]     : endLine[0];
                int    endCol = endColumn.length   > 1 ? endColumn[ci]   : endColumn[0];

                tr.Ace.yellow("msg", msg);
                tr.Ace.yellow("violationLevel + 3 - ci: " + (violationLevel + 3 - ci));

                Violation[] violations = new Violation[0];
                
                if (level >= 0 && violationLevel + 3 - ci <= level) {
                    violations = new Violation[] {
                        new Violation(msg, bgLn, bgCol, endLn, endCol)
                    };
                }

                evaluate(contents[ci], level, JAVA_VERSION_DEFAULT, violations);
            }
        }
    }
    
    public void testDocumented() {
        runTest(ItemDocAnalyzer.CHKLVL_DOC_EXISTS,
                new String[] {
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    private class Inner {\n" +
                    "    }\n" +
                    "}\n",
                        
                    "class Test {\n" +
                    "}\n",
                        
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    protected class Inner {\n" +
                    "    }\n" +
                    "}\n",
                        
                    "public class TestHasJavadoc {\n" +
                    "}\n"
                },
                    
                new String[] {
                    "Undocumented private class",
                    "Undocumented class",
                    "Undocumented protected class",
                    "Undocumented public class"
                },
                    
                new int[] {  4,  1,  4,  1 },  // begin lines
                new int[] { 19,  7, 21, 14 },  // begin columns
                new int[] {  4,  1,  4,  1 },  // end lines
                new int[] { 23, 10, 25, 27 }); // end columns
    }

    public void testSummarySentence() {
        runTest(ItemDocAnalyzer.CHKLVL_SUMMARY_SENTENCE,
                new String[] {
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /** Too short. */\n" +
                    "    private class Inner {\n" +
                    "    }\n" +
                    "}\n",
                        
                    "/** Too short. */\n" +
                    "class Test {\n" +
                    "}\n",
                        
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /** Too short. */\n" +
                    "    protected class Inner {\n" +
                    "    }\n" +
                    "}\n",
                    
                    "/** Too short. */\n" +
                    "public class TestHasJavadoc {\n" +
                    "}\n"
                },
                    
                new String[] {
                    ItemDocAnalyzer.MSG_SUMMARY_SENTENCE_TOO_SHORT
                },
                    
                new int[] {  4,  1,  4,  1 }, // lines
                new int[] {  9,  5,  9,  5 }, // columns
                new int[] {  4,  1,  4,  1 },
                new int[] { 18, 14, 18, 14 });
    }

    public void testClassTagOrder() {
        runTest(ItemDocAnalyzer.CHKLVL_MISORDERED_TAGS,
                new String[] {
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @version 0.1.2 \n" +
                    "      * @author me \n" +
                    "      */\n" +
                    "    private class Inner {\n" +
                    "    }\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @version 0.1.2 \n" +
                    "      * @author me \n" +
                    "      */\n" +
                    "    class Inner {\n" +
                    "    }\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @version 0.1.2 \n" +
                    "      * @author me \n" +
                    "      */\n" +
                    "    protected class Inner {\n" +
                    "    }\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @version 0.1.2 \n" +
                    "      * @author me \n" +
                    "      */\n" +
                    "    public class Inner {\n" +
                    "    }\n" +
                    "}\n",
                },
                
                new String[] { 
                    ItemDocAnalyzer.MSG_TAG_IMPROPER_ORDER
                },
                new int[] {  7 },
                new int[] {  9 },
                new int[] {  7 },
                new int[] { 15 });
    }

    public void testMethodTagOrder() {
        runTest(ItemDocAnalyzer.CHKLVL_MISORDERED_TAGS,
                new String[] {
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @since 0.1.2 \n" +
                    "      * @see Something#else() \n" +
                    "      */\n" +
                    "    private void f() {}\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @since 0.1.2 \n" +
                    "      * @see Something#else() \n" +
                    "      */\n" +
                    "    void f() {}\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @since 0.1.2 \n" +
                    "      * @see Something#else() \n" +
                    "      */\n" +
                    "    protected void f() {}\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @since 0.1.2 \n" +
                    "      * @see Something#else() \n" +
                    "      */\n" +
                    "    public void f() {}\n" +
                    "}\n",
                },
                
                new String[] { 
                    ItemDocAnalyzer.MSG_TAG_IMPROPER_ORDER
                },
                new int[] {  7 },
                new int[] {  9 },
                new int[] {  7 },
                new int[] { 12 });
    }

    public void testMethodValidTags() {
        runTest(ItemDocAnalyzer.CHKLVL_VALID_TAGS,
                new String[] {
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @version 0.1.2 \n" +
                    "      */\n" +
                    "    private void f() {}\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @version 0.1.2 \n" +
                    "      */\n" +
                    "    void f() {}\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @version 0.1.2 \n" +
                    "      */\n" +
                    "    protected void f() {}\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @version 0.1.2 \n" +
                    "      */\n" +
                    "    public void f() {}\n" +
                    "}\n",
                },
                
                new String[] { 
                    "Tag not valid for method"
                },
                new int[] { 6 },
                new int[] { 9 },
                new int[] { 6 },
                new int[] { 16 });
    }

    public void testClassTagContent() {
        runTest(ItemDocAnalyzer.CHKLVL_TAG_CONTENT,
                new String[] {
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @version  \n" +
                    "      */\n" +
                    "    private class Inner {\n" +
                    "    }\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @version  \n" +
                    "      */\n" +
                    "    class Inner {\n" +
                    "    }\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @version  \n" +
                    "      */\n" +
                    "    protected class Inner {\n" +
                    "    }\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @version  \n" +
                    "      */\n" +
                    "    public class Inner {\n" +
                    "    }\n" +
                    "}\n",
                },
                
                new String[] { 
                    TypeDocAnalyzer.MSG_VERSION_WITHOUT_TEXT
                },
                new int[] { 6 },
                new int[] { 9 },
                new int[] { 6 },
                new int[] { 16 });
    }

    public void testMethodTags() {
        runTest(ItemDocAnalyzer.CHKLVL_TAG_CONTENT,
                new String[] {
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @return  \n" +
                    "      */\n" +
                    "    private int f() { return 1; }\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @return  \n" +
                    "      */\n" +
                    "    int f() { return 1; }\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @return  \n" +
                    "      */\n" +
                    "    protected int f() { return 1; }\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @return  \n" +
                    "      */\n" +
                    "    public int f() { return 1; }\n" +
                    "}\n",
                },
                
                new String[] { 
                    MethodDocAnalyzer.MSG_RETURN_WITHOUT_DESCRIPTION
                },
                new int[] {  6 },
                new int[] {  9 },
                new int[] {  6 },
                new int[] { 15 });
    }

    public void testMethodParameters() {
        runTest(ItemDocAnalyzer.CHKLVL_TAG_CONTENT,
                new String[] {
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @param i  \n" +
                    "      */\n" +
                    "    private int f(int i) { return 1; }\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @param i  \n" +
                    "      */\n" +
                    "    int f(int i) { return 1; }\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @param i  \n" +
                    "      */\n" +
                    "    protected int f(int i) { return 1; }\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @param i  \n" +
                    "      */\n" +
                    "    public int f(int i) { return 1; }\n" +
                    "}\n",
                },
                
                new String[] { 
                    ParameterDocAnalyzer.MSG_PARAMETER_WITHOUT_DESCRIPTION
                },
                new int[] {  6 },
                new int[] { 16 },
                new int[] {  6 },
                new int[] { 16 });
    }

    public void testMethodExceptions() {
        runTest(ItemDocAnalyzer.CHKLVL_TAG_CONTENT,
                new String[] {
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @throws IOException  \n" +
                    "      */\n" +
                    "    private int f() throws IOException { return 1; }\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @throws IOException  \n" +
                    "      */\n" +
                    "    int f() throws IOException { return 1; }\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @throws IOException  \n" +
                    "      */\n" +
                    "    protected int f() throws IOException { return 1; }\n" +
                    "}\n",
                    
                    "/** This is a description.\n" +
                    "  */\n" +
                    "class Test {\n" +
                    "    /**\n" +
                    "      * This is a description. \n" +
                    "      * @throws IOException  \n" +
                    "      */\n" +
                    "    public int f() throws IOException { return 1; }\n" +
                    "}\n",
                },
                
                new String[] { 
                    ExceptionDocAnalyzer.MSG_EXCEPTION_WITHOUT_DESCRIPTION
                },
                new int[] {  6 },
                new int[] { 17 },
                new int[] {  6 },
                new int[] { 27 });
    }

    public void testFieldDoc() {
        runTest(ItemDocAnalyzer.CHKLVL_TAG_CONTENT,
                new String[] {
                    "/** This is a description. */\n" +
                    "class Test {\n" +
                    "    /** This is a description.\n" +
                    "      * @serialField one int " +
                    "      */\n" +
                    "    private ObjectStreamField[] serialPersistentFields = { \n" +
                    "        new ObjectStreamField(\"one\",  Integer.TYPE) \n" +
                    "    };\n" +
                    "}\n",

                    "/** This is a description. */\n" +
                    "class Test {\n" +
                    "    /** This is a description.\n" +
                    "      * @serialField one int " +
                    "      */\n" +
                    "    ObjectStreamField[] serialPersistentFields = { \n" +
                    "        new ObjectStreamField(\"one\",  Integer.TYPE) \n" +
                    "    };\n" +
                    "}\n",

                    "/** This is a description. */\n" +
                    "class Test {\n" +
                    "    /** This is a description.\n" +
                    "      * @serialField one int " +
                    "      */\n" +
                    "    protected ObjectStreamField[] serialPersistentFields = { \n" +
                    "        new ObjectStreamField(\"one\",  Integer.TYPE) \n" +
                    "    };\n" +
                    "}\n",

                    "/** This is a description. */\n" +
                    "class Test {\n" +
                    "    /** This is a description.\n" +
                    "      * @serialField one int " +
                    "      */\n" +
                    "    public ObjectStreamField[] serialPersistentFields = { \n" +
                    "        new ObjectStreamField(\"one\",  Integer.TYPE) \n" +
                    "    };\n" +
                    "}\n",
                },
                
                new String[] { 
                    FieldDocAnalyzer.MSG_SERIALFIELD_WITHOUT_DESCRIPTION,
                },
                new int[] {  4 },
                new int[] { 22 },
                new int[] {  4 },
                new int[] { 29 });
    }
    
}
