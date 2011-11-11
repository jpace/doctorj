package org.incava.doctorj;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.*;
import org.incava.text.Lines;
import org.incava.text.Location;
import org.incava.text.LocationRange;

public class AbstractDoctorJTestCase extends TestCase {
    public final static int WARNING_LEVEL_DEFAULT = Options.MAXIMUM_WARNING_LEVEL;
    public final static String JAVA_VERSION_DEFAULT = "1.4";

    public AbstractDoctorJTestCase(String name) {
        super(name);
    }

    public LocationRange locrange(Location start, Location end) {
        return new LocationRange(start, end);
    }

    public LocationRange locrange(int line, int column, String var) {
        return locrange(loc(line, column), loc(line, column, var));
    }

    public Location loc(int line, int column) {
        return new Location(line, column);
    }

    public Location loc(int line, int col, String var) {
        return loc(line, col + var.length() - 1);
    }

    public void assertViolations(Collection<Violation> expected, Collection<Violation> actual) {
        tr.Ace.log("expected", expected);
        tr.Ace.log("actual", actual);

        tr.Ace.setVerbose(true);

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

    protected Report analyze(String contents, Options options) {
        StringWriter reportOutput = new StringWriter();
        Report       report       = new TerseReport(reportOutput);

        JavaParserVisitor analyzer = new JavaAnalyzer(report, options);
        try {
            report.reset(contents);

            Reader             rdr = new StringReader(contents);
            JavaCharStream     jcs = new JavaCharStream(rdr);
            JavaParser         parser = new JavaParser(jcs);
            if ("1.3".equals(options.getSource())) {
                parser.setJDK13();
            }
            else if ("1.5".equals(options.getSource())) {
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

    public void evaluate(String contents, Options options, Violation ... expectations) {
        Report report = analyze(contents, options);
        assertViolations(Arrays.asList(expectations), report.getViolations());
        report.flush();
    }        

    public void evaluate(Lines lines, Violation ... expectations) {
        evaluate(lines.toString(), new Options(), expectations);
    }

    public void evaluate(Lines lines, Options options, Violation ... expectations) {
        evaluate(lines.toString(), options, expectations);
    }        

    public void evaluate(String contents, Options options, String version, Violation ... expectations) {
        options.process(Arrays.asList(new String[] { "--source=" + version }));
        evaluate(contents, options, expectations);
    }        

    public void evaluate(String contents, String version, Violation ... expectations) {
        evaluate(contents, new Options(), version, expectations);
    }        

    public void evaluate(String contents, Violation ... expectations) {
        evaluate(contents, new Options(), expectations);
    }        
}
