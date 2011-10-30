package org.incava.doctorj;

import java.awt.Point;
import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.*;


public class AbstractDoctorJTestCase extends TestCase {

    public AbstractDoctorJTestCase(String name) {
        super(name);
        
        Options.warningLevel = Options.MAXIMUM_WARNING_LEVEL;
    }

    public Point loc(int line, int column) {
        return new Point(line, column);
    }

    public Point loc(int line, int col, String var) {
        return loc(line, col + var.length() - 1);
    }

    public Point loc(Point pt, String var) {
        return loc(pt.x, pt.y + (var == null ? 0 : var.length() - 1));
    }

    public void assertViolations(Collection<Violation> expected, Collection<Violation> actual) {
        tr.Ace.log("expected", expected);
        tr.Ace.log("actual", actual);

        List<Violation> expList = new ArrayList<Violation>(expected);
        List<Violation> actList = new ArrayList<Violation>(actual);        

        if (expected.size() != actual.size()) {
            tr.Ace.red("expList", expList);
            tr.Ace.red("actList", actList);
        }

        assertEquals("number of violations", expected.size(), actual.size());

        for (int idx = 0; idx < expList.size(); ++idx) {
            Violation exp = expList.get(idx);
            Violation act = actList.get(idx);
            assertNotNull("violation not null", act);
            assertEquals("violation[" + idx + "]", exp, act);
        }
    }

    protected Report analyze(String contents, String version) {
        StringWriter      reportOutput = new StringWriter();
        Report            report       = new TerseReport(reportOutput);
        JavaParserVisitor analyzer     = new JavaAnalyzer(report, Options.getInstance());
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

    public void evaluate(Lines lines, Violation ... expectations) {
        evaluate(lines.toString(), expectations);
    }

    public void evaluate(Lines lines, String version, Violation ... expectations) {
        evaluate(lines.toString(), version, expectations);
    }

    public void evaluate(String contents, Violation ... expectations) {
        evaluate(contents, "1.4", expectations);
    }

    public void evaluate(String contents, String version, Violation ... expectations) {
        tr.Ace.log("expectations", expectations);
        Report report = analyze(contents, version);

        assertViolations(Arrays.asList(expectations), report.getViolations());
        
        report.flush();
    }
    
}
