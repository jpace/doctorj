package org.incava.doctorj;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.*;


public class TestStringAnalyzer extends Tester {
    
    // static {
    //     ItemDocAnalyzer.spellChecker.addDictionary("/home/jpace/proj/doctorj/etc/words.en_US");
    // }
    
    public TestStringAnalyzer(String name) {
        super(name);

        tr.Ace.setVerbose(true);
    }

    protected Report analyze(String contents, String version) {
        StringWriter      reportOutput = new StringWriter();
        Report            report = new TerseReport(reportOutput);
        JavaParserVisitor analyzer = new StringAnalyzer(report);
        try {
            report.reset(contents);

            Reader             rdr = new StringReader(contents);
            JavaCharStream     jcs = new JavaCharStream(rdr);
            JavaParser         parser = new JavaParser(jcs);
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
                           "    String s = \"something\";" +
                           "    int i = 4;" +
                           "    String t = \"this is a test\";" +
                           "}\n");
        Report report = analyze(contents, "1.5");
        tr.Ace.log("report", report);
    }

    public void testIncorrectString() {
        String contents = ("class Test {\n" +
                           "    String s = \"somet hing\";" +
                           "    int i = 4;" +
                           "    String t = \"thi si sate st\";" +
                           "}\n");
        Report report = analyze(contents, "1.5");
        tr.Ace.log("report", report);
    }

}
