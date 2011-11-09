package org.incava.doctorj;

import org.incava.analysis.Violation;
import org.incava.text.Location;
import org.incava.text.LocationRange;

public class TestOptions extends AbstractDoctorJTestCase {    
    public TestOptions(String name) {
        super(name);
    }

    public <T> T getValue(T[] ary, int idx) {
        return ary.length > 1 ? ary[idx] : ary[0];
    }

    public void runTest(int level, int violationLevel,
                        String content, 
                        int index,
                        String[] messages,
                        LocationRange[] locations) {
        Violation[] violations = new Violation[0];                
        if (level >= 0 && violationLevel + 3 - index <= level) {
            String msg = getValue(messages, index);

            LocationRange location = getValue(locations, index);
            violations = new Violation[] {
                new Violation(msg, location)
            };
        }
        
        evaluate(content, level, JAVA_VERSION_DEFAULT, violations);
    }

    public void runTest(int level, int violationLevel,
                        String content, 
                        int index,
                        String message,
                        LocationRange location) {
        Violation[] violations = new Violation[0];                
        if (level >= 0 && violationLevel + 3 - index <= level) {
            violations = new Violation[] {
                new Violation(message, location)
            };
        }
        
        evaluate(content, level, JAVA_VERSION_DEFAULT, violations);
    }

    public void runTest(int level, int violationLevel,
                        String content, 
                        int index,
                        String[] messages,
                        Integer[] beginLine, Integer[] beginColumn, 
                        Integer[] endLine, Integer[] endColumn) {
        String msg    = getValue(messages,    index);
        int    bgLn   = getValue(beginLine,   index);
        int    bgCol  = getValue(beginColumn, index);
        int    endLn  = getValue(endLine,     index);
        int    endCol = getValue(endColumn,   index);

        Violation[] violations = new Violation[0];
                
        if (level >= 0 && violationLevel + 3 - index <= level) {
            violations = new Violation[] {
                new Violation(msg, bgLn, bgCol, endLn, endCol)
            };
        }

        evaluate(content, level, JAVA_VERSION_DEFAULT, violations);
    }

    public void runTest(int level, int violationLevel,
                        String[] contents, 
                        String[] messages,
                        Integer[] beginLine, Integer[] beginColumn, 
                        Integer[] endLine, Integer[] endColumn) {
        for (int ci = 0; ci < contents.length; ++ci) {
            runTest(level, violationLevel, contents[ci], ci, messages, beginLine, beginColumn, endLine, endColumn);
        }
    }    

    public void runTest(int level, int violationLevel,
                        String[] contents, 
                        String message,
                        LocationRange location) {
        for (int ci = 0; ci < contents.length; ++ci) {
            runTest(level, violationLevel, contents[ci], ci, message, location);
        }
    }    

    public void runTest(int violationLevel, String[] contents, String[] messages, Integer[] beginLine, Integer[] beginColumn, Integer[] endLine, Integer[] endColumn) {
        for (int level = -1; level < 9; ++level) {
            runTest(level, violationLevel, contents, messages, beginLine, beginColumn, endLine, endColumn);
        }
    }

    public void runTest(int violationLevel, String[] contents, String message, LocationRange location) {
        for (int level = -1; level < 9; ++level) {
            runTest(level, violationLevel, contents, message, location);
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
                    
                new Integer[] {  4,  1,  4,  1 },  // begin lines
                new Integer[] { 19,  7, 21, 14 },  // begin columns
                new Integer[] {  4,  1,  4,  1 },  // end lines
                new Integer[] { 23, 10, 25, 27 }); // end columns
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
                    
                new Integer[] {  4,  1,  4,  1 }, // lines
                new Integer[] {  9,  5,  9,  5 }, // columns
                new Integer[] {  4,  1,  4,  1 },
                new Integer[] { 18, 14, 18, 14 });
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
                
                ItemDocAnalyzer.MSG_TAG_IMPROPER_ORDER,
                locrange(loc(7, 9), loc(7, 15)));
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
                
                ItemDocAnalyzer.MSG_TAG_IMPROPER_ORDER,
                locrange(loc(7, 9), loc(7, 12)));
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
                
                "Tag not valid for method",
                locrange(loc(6, 9), loc(6, 16)));
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
                
                TypeDocAnalyzer.MSG_VERSION_WITHOUT_TEXT,
                locrange(loc(6, 9), loc(6, 16)));
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
                
                MethodDocAnalyzer.MSG_RETURN_WITHOUT_DESCRIPTION,
                locrange(loc(6, 9), loc(6, 15)));
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
                
                ParameterDocAnalyzer.MSG_PARAMETER_WITHOUT_DESCRIPTION,
                locrange(loc(6, 16), loc(6, 16)));
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
                
                ExceptionDocAnalyzer.MSG_EXCEPTION_WITHOUT_DESCRIPTION,
                locrange(loc(6, 17), loc(6, 27)));
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
                
                FieldDocAnalyzer.MSG_SERIALFIELD_WITHOUT_DESCRIPTION,
                locrange(loc(4, 22), loc(4, 29)));
    }
}
