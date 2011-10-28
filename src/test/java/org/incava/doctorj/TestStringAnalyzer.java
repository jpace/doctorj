package org.incava.doctorj;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.*;


public class TestStringAnalyzer extends AbstractDoctorJTestCase {
    
    // static {
    //     SpellingAnalyzer.addDictionary("/home/jpace/proj/doctorj/etc/words.en_US");
    // }
    
    public TestStringAnalyzer(String name) {
        super(name);

        tr.Ace.setVerbose(true);
    }

    protected Report analyze(String contents, String version) {
        StringWriter   reportOutput = new StringWriter();
        Report         report       = new TerseReport(reportOutput);
        StringAnalyzer analyzer     = new StringAnalyzer(report);

        try {
            report.reset(contents);

            Reader         rdr    = new StringReader(contents);
            JavaCharStream jcs    = new JavaCharStream(rdr);
            JavaParser     parser = new JavaParser(jcs);

            if ("1.3".equals(version)) {
                parser.setJDK13();
            }
            else if ("1.5".equals(version)) {
                parser.setJDK15();
            }
            ASTCompilationUnit cu = parser.CompilationUnit();

            cu.jjtAccept(analyzer, null);
        }
        catch (ParseException e) {
            System.out.println(e.getMessage());
            System.out.println("Encountered errors during parse.");
            fail(e.getMessage());
        }
        
        return report;
    }

    public void testOKString() {
        String contents = ("class Test {\n" +
                           "    String s = \"something\";\n" +
                           "    int i = 4;\n" +
                           "    String t = \"this is a test\";\n" +
                           "}\n");
        Report report = analyze(contents, "1.5");
        tr.Ace.log("report", report);

        Set<Violation> violations = report.getViolations();
        tr.Ace.yellow("violations", violations);

        assertEquals("# of violations", 0, violations.size());
    }

    public void testIncorrectString() {
        //$$$ test with one close match, none, many 

        String contents = ("class Test {\n" +
                           "    String s = \"wafle freis\";\n" +
                           "    int i = 4;\n" +
                           "    String t = \"supperman mxyzptlk\";\n" +
                           "}\n");
        Report report = analyze(contents, "1.5");
        tr.Ace.log("report", report);

        Set<Violation> violations = report.getViolations();
        tr.Ace.yellow("violations", violations);

        assertEquals("# of violations", 4, violations.size());

        Violation[] expected = new Violation[] {
            new Violation("Word 'wafle' appears to be misspelled. Closest matches: waffle, wale, ale, wafer, waffled, waffler", 2, 17, 2, 21),
            new Violation("Word 'freis' appears to be misspelled. Closest matches: reis, fares, fires, fores, fredi, fredi's", 2, 23, 2, 27),
            new Violation("Word 'supperman' appears to be misspelled. Closest matches: superman, superhuman, superman's, supermen, superwoman, supper", 4, 17, 4, 25),
            new Violation("Word 'mxyzptlk' appears to be misspelled. No close matches", 4, 27, 4, 34)
        };

        assertViolations(Arrays.asList(expected), violations);
    }

}
