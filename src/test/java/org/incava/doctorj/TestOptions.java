package org.incava.doctorj;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.incava.analysis.Violation;
import org.incava.text.Lines;
import org.incava.text.Location;
import org.incava.text.LocationRange;

public class TestOptions extends AbstractDoctorJTestCase {
    

    public TestOptions(String name) {
        super(name);
    }

    public <T> T getValue(T[] ary, int idx) {
        return ary.length > 1 ? ary[idx] : ary[0];
    }

    public Lines createLines(Lines baseLines, int index, String newLine) {
        Lines newLines = (Lines)baseLines.clone();
        newLines.set(index, newLine);
        return newLines;
    }

    public Lines[] createLinesByAccess(Lines baseLines, int index, String newLine) {
        List<Lines> lineList = new ArrayList<Lines>();
        lineList.add(createLines(baseLines, index, "    private " + newLine));
        lineList.add(createLines(baseLines, index, "    " + newLine));
        lineList.add(createLines(baseLines, index, "    protected " + newLine));
        lineList.add(createLines(baseLines, index, "    public " + newLine));
        return lineList.toArray(new Lines[lineList.size()]);
    }

    public void runTest(int level, int violationLevel,
                        Lines content, 
                        int index,
                        String message,
                        LocationRange location) {
        List<Violation> violations = new ArrayList<Violation>();
        if (level >= 0 && violationLevel + 3 - index <= level) {
            violations.add(new Violation(message, location));
        }
        
        Options options = new Options();
        options.process(Arrays.asList(new String[] { "--level=" + level }));
        evaluate(content, options, violations.toArray(new Violation[violations.size()]));
    }

    public void runTest(int level, int violationLevel,
                        Lines content, 
                        int index,
                        String[] messages,
                        LocationRange[] locations) {
        runTest(level, violationLevel, content, index, getValue(messages, index), getValue(locations, index));
    }

    public void runTest(int level, int violationLevel,
                        Lines[] contents, 
                        String message,
                        LocationRange location) {
        for (int ci = 0; ci < contents.length; ++ci) {
            runTest(level, violationLevel, contents[ci], ci, message, location);
        }
    }    

    public void runTest(int level, int violationLevel,
                        Lines[] contents, 
                        String[] messages,
                        LocationRange[] locations) {
        for (int ci = 0; ci < contents.length; ++ci) {
            runTest(level, violationLevel, contents[ci], ci, messages, locations);
        }
    }    

    public void runTest(int violationLevel, Lines[] contents, String message, LocationRange location) {
        for (int level = -1; level < 9; ++level) {
            runTest(level, violationLevel, contents, message, location);
        }
    }    

    public void runTest(int violationLevel, Lines[] contents, String[] messages, LocationRange ... locations) {
        for (int level = -1; level < 9; ++level) {
            runTest(level, violationLevel, contents, messages, locations);
        }
    }
    
    public void doUndocumentedClassTest(Lines lines, String classLine, int lineIndex, int position, String className) {
        int lineNum = lineIndex + 1;
        
        runTest(ItemDocAnalyzer.CHKLVL_SUMMARY_SENTENCE,
                createLinesByAccess(lines, lineIndex, classLine),
                new String[] {
                    "Undocumented private class",
                    "Undocumented class",
                    "Undocumented protected class",
                    "Undocumented public class"
                },
                locrange(lineNum, 8 + position,  className),
                locrange(lineNum, position,      className),
                locrange(lineNum, 10 + position, className),
                locrange(lineNum, 7 + position,  className));
    }

    public void testUndocumentedInnerClass() {
        String innerClassLine = "class Inner {";
        Lines lines = new Lines("/** This is a description.",
                                "  */",
                                "class Test {",
                                innerClassLine,
                                "    }",
                                "}");
        doUndocumentedClassTest(lines, innerClassLine, 3, 11, "Inner");
    }

    public void testUndocumentedOuterClass() {
        String outerClassLine = "class Test {";
        Lines lines = new Lines(outerClassLine,
                                "}");
        doUndocumentedClassTest(lines, outerClassLine, 0, 11, "Test");
    }

    public void testSummarySentenceInnerClass() {
        String innerClassLine = "class Inner {";
        Lines lines = new Lines("/** This is a description.",
                                "  */",
                                "class Test {",
                                "    /** Too short. */",
                                innerClassLine,
                                "    }",
                                "}");
        
        runTest(ItemDocAnalyzer.CHKLVL_SUMMARY_SENTENCE,
                createLinesByAccess(lines, 4, innerClassLine),
                ItemDocAnalyzer.MSG_SUMMARY_SENTENCE_TOO_SHORT,
                locrange(loc(4,  9), loc(4, 18)));
    }

    public void testSummarySentenceOuterClass() {
        String outerClassLine = "class Test {";
        Lines lines = new Lines("/** Too short. */",
                                outerClassLine,
                                "}");
        
        runTest(ItemDocAnalyzer.CHKLVL_SUMMARY_SENTENCE,
                createLinesByAccess(lines, 1, outerClassLine),                    
                ItemDocAnalyzer.MSG_SUMMARY_SENTENCE_TOO_SHORT,
                locrange(loc(1,  5), loc(1, 14)));
    }
    
    public void testClassTagOrder() {
        String innerClassLine = "class Inner {";
        Lines lines = new Lines("/** This is a description.",
                                "  */",
                                "class Test {",
                                "    /**",
                                "      * This is a description. ",
                                "      * @version 0.1.2 ",
                                "      * @author me ",
                                "      */",
                                innerClassLine,
                                "    }",
                                "}");

        runTest(ItemDocAnalyzer.CHKLVL_MISORDERED_TAGS,
                createLinesByAccess(lines, 8, innerClassLine),
                ItemDocAnalyzer.MSG_TAG_IMPROPER_ORDER,
                locrange(loc(7, 9), loc(7, 15)));
    }

    public void testMethodTagOrder() {
        String methodLine = "void f() {}";
        Lines lines = new Lines("/** This is a description.",
                                "  */",
                                "class Test {",
                                "    /**",
                                "      * This is a description. ",
                                "      * @since 0.1.2 ",
                                "      * @see Something#else() ",
                                "      */",
                                methodLine,
                                "}");

        runTest(ItemDocAnalyzer.CHKLVL_MISORDERED_TAGS,
                createLinesByAccess(lines, 8, methodLine),
                ItemDocAnalyzer.MSG_TAG_IMPROPER_ORDER,
                locrange(loc(7, 9), loc(7, 12)));
    }

    public void testMethodValidTags() {
        String methodLine = "void f() {}";
        Lines lines = new Lines("/** This is a description.",
                                "  */",
                                "class Test {",
                                "    /**",
                                "      * This is a description. ",
                                "      * @version 0.1.2 ",
                                "      */",
                                methodLine,
                                "}");
        runTest(ItemDocAnalyzer.CHKLVL_VALID_TAGS,
                createLinesByAccess(lines, 7, methodLine),
                "Tag not valid for method",
                locrange(loc(6, 9), loc(6, 16)));
    }
    
    public void testClassTagContent() {
        String innerClassLine = "class Inner {";
        Lines lines = new Lines("/** This is a description.",
                                "  */",
                                "class Test {",
                                "    /**",
                                "      * This is a description. ",
                                "      * @version  ",
                                "      */",
                                innerClassLine,
                                "    }",
                                "}");

        runTest(ItemDocAnalyzer.CHKLVL_TAG_CONTENT,
                createLinesByAccess(lines, 7, innerClassLine),
                TypeDocAnalyzer.MSG_VERSION_WITHOUT_TEXT,
                locrange(6, 9, "@version"));
    }

    public void testMethodTags() {
        String methodLine = "int f() { return 1; }";
        Lines lines = new Lines("/** This is a description.",
                                "  */",
                                "class Test {",
                                "    /**",
                                "      * This is a description. ",
                                "      * @return  ",
                                "      */",
                                methodLine,
                                "}");

        runTest(ItemDocAnalyzer.CHKLVL_TAG_CONTENT,
                createLinesByAccess(lines, 7, methodLine),                
                MethodDocAnalyzer.MSG_RETURN_WITHOUT_DESCRIPTION,
                locrange(loc(6, 9), loc(6, 15)));
    }

    public void testMethodParameters() {
        String methodLine = "int f(int i) { return 1; }";
        Lines lines = new Lines("/** This is a description.",
                                "  */",
                                "class Test {",
                                "    /**",
                                "      * This is a description. ",
                                "      * @param i  ",
                                "      */",
                                methodLine,
                                "}");

        runTest(ItemDocAnalyzer.CHKLVL_TAG_CONTENT,
                createLinesByAccess(lines, 7, methodLine),
                ParameterDocAnalyzer.MSG_PARAMETER_WITHOUT_DESCRIPTION,
                locrange(loc(6, 16), loc(6, 16)));
    }

    public void testMethodExceptions() {
        String methodLine = "int f() throws IOException { return 1; }";
        Lines lines = new Lines("/** This is a description.",
                                "  */",
                                "class Test {",
                                "    /**",
                                "      * This is a description. ",
                                "      * @throws IOException  ",
                                "      */",
                                methodLine,
                                "}");

        runTest(ItemDocAnalyzer.CHKLVL_TAG_CONTENT,
                createLinesByAccess(lines, 7, methodLine),
                ExceptionDocAnalyzer.MSG_EXCEPTION_WITHOUT_DESCRIPTION,
                locrange(loc(6, 17), loc(6, 27)));
    }

    public void testFieldDoc() {
        String methodLine = "ObjectStreamField[] serialPersistentFields = {";
        Lines lines = new Lines("/** This is a description. */",
                                "class Test {",
                                "    /** This is a description.",
                                "      * @serialField one int ",
                                "      */",
                                methodLine,
                                "        new ObjectStreamField(\"one\",  Integer.TYPE) ",
                                "    };",
                                "}");
        
        runTest(ItemDocAnalyzer.CHKLVL_TAG_CONTENT,
                createLinesByAccess(lines, 5, methodLine),
                FieldDocAnalyzer.MSG_SERIALFIELD_WITHOUT_DESCRIPTION,
                locrange(loc(4, 22), loc(4, 29)));
    }
}
