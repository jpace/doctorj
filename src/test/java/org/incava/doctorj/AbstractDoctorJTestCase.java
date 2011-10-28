package org.incava.doctorj;

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

    protected Report analyze(String contents, String version) {
        StringWriter      reportOutput = new StringWriter();
        Report            report       = new TerseReport(reportOutput);
        JavaParserVisitor analyzer     = new JavadocAnalyzer(report);
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

    public void evaluate(String contents, Violation ... expectations) {
        evaluate(contents, "1.4", expectations);
    }

    public void evaluate(String contents, String version, Violation ... expectations) {
        tr.Ace.log("expectations", expectations);
        Report report = analyze(contents, version);
        
        Set<Violation> violations = report.getViolations();
        tr.Ace.log("violations", violations);
        
        assertEquals("number of violations", expectations.length, violations.size());
        
        Iterator<Violation> vit = violations.iterator();
        for (int vi = 0; vit.hasNext() && vi < violations.size(); ++vi) {
            Violation violation = vit.next();
            Violation exp       = expectations[vi];
                
            assertNotNull("violation not null", violation);
            assertEquals("violation[" + vi + "]", exp, violation);
        }

        report.flush();
    }
    
}
