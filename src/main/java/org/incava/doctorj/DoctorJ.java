package org.incava.doctorj;

import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaCharStream;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.JavaParserVisitor;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.TokenMgrError;
import org.incava.analysis.*;
import org.incava.ijdk.io.Find;
import org.incava.ijdk.util.TimedEvent;
import org.incava.ijdk.util.TimedEventSet;
import org.incava.pmdx.SimpleNodeUtil;

public class DoctorJ {
    private final TimedEventSet totalInit;
    private final TimedEventSet totalParse;
    private final TimedEventSet totalAnalysis;
    private final Report report;
    private final JavaParserVisitor analyzer;
    private final String sourceVersion;

    private JavaParser parser = null;
    private int exitValue;
    private int nFiles;

    public DoctorJ(String[] args) {
        tr.Ace.set(true, 25, 4, 20, 25);
        tr.Ace.setOutput(tr.Ace.VERBOSE, tr.Ace.LEVEL4);
        tr.Ace.setOutput(tr.Ace.QUIET,   tr.Ace.LEVEL2);

        this.totalInit = new TimedEventSet();
        this.totalParse = new TimedEventSet();
        this.totalAnalysis = new TimedEventSet();

        this.exitValue = 0;
        this.nFiles = 0;

        Options  opts = new Options();
        List<String> names = opts.process(Arrays.asList(args));

        this.sourceVersion = opts.getSource();

        if (opts.getEmacsOutput()) {
            this.report = new TerseReport(System.out);
        }
        else {
            this.report = new ContextReport(System.out);
        }
        
        this.analyzer = new JavaAnalyzer(this.report, opts);

        for (String name : names) {
            process(name);
        }
        
        if (this.nFiles > 1) {
            long total = this.totalInit.duration + this.totalParse.duration + this.totalAnalysis.duration;
            tr.Ace.log("total time: " + total);
            tr.Ace.log("init      : " + this.totalInit.duration);
            tr.Ace.log("parse     : " + this.totalParse.duration);
            tr.Ace.log("analysis  : " + this.totalAnalysis.duration);
            tr.Ace.log("#files    : " + this.nFiles);
        }
    }

    public int getExitValue() {
        return this.exitValue;
    }

    protected void processDirectory(File dir) {
        File[] contents = dir.listFiles();
        Arrays.sort(contents);
        for (File fd : contents) {
            if (fd.isDirectory()) {
                processDirectory(fd);
            }
            else if (fd.isFile() && fd.getName().endsWith(".java")) {
                processFile(fd);
            }
        }
    }

    protected void process(String name) {
        File fd = new File(name);
        if (fd.exists()) {
            if (fd.isDirectory()) {
                processDirectory(fd);
            }
            else if (fd.isFile()) {
                processFile(fd);
            }
        }
        else {
            System.err.println("File " + name + " not found.");
        }
    }
    
    protected void processFile(File file) {
        ++this.nFiles;

        if (initParser(file)) {
            ASTCompilationUnit cu = parse(file);
            if (cu != null) {
                analyze(cu);
            }
        }
    }

    protected boolean initParser(File file) {
        tr.Ace.log("file", file);

        TimedEvent init = new TimedEvent(this.totalInit);
        try {
            this.report.reset(file);
                
            FileReader     rdr = new FileReader(file);
            JavaCharStream jcs = new JavaCharStream(rdr);
            
            this.parser = new JavaParser(jcs);

            // as of 2011/11/12, there is no setJDK14.

            if (sourceVersion.equals("1.3")) {
                this.parser.setJDK13();
            }
            else if (sourceVersion.equals("1.5") || sourceVersion.equals("1.6")) {
                this.parser.setJDK15();
            }
            else {
                System.err.println("ERROR: source version '" + sourceVersion + "' not recognized");
                System.exit(-1);
            }
            init.end();

            return true;
        }
        catch (FileNotFoundException e) {
            System.out.println("File " + file.getName() + " not found.");
            this.exitValue = 1;
            return false;
        }
        catch (IOException e) {
            System.out.println("Error opening " + file.getName() + ": " + e);
            this.exitValue = 1;
            return false;
        }
        catch (TokenMgrError tme) {
            System.out.println("Error parsing (tokenizing) " + file.getName() + ": " + tme.getMessage());
            this.exitValue = 1;
            return false;
        }
    }

    protected ASTCompilationUnit parse(File file) {
        try {
            TimedEvent         parse = new TimedEvent(this.totalParse);
            ASTCompilationUnit cu = this.parser.CompilationUnit();
            parse.end();
            return cu;
        }
        catch (ParseException e) {
            System.out.println("Parse error in " + file.getName() + ": " + e.getMessage());
            this.exitValue = 1;
            return null;
        }
    }

    protected void analyze(ASTCompilationUnit cu) {
        TimedEvent analysis = new TimedEvent(this.totalAnalysis);
        cu.jjtAccept(this.analyzer, null);

        this.report.flush();
        analysis.end();
    }

    public static void main(String[] args) {
        tr.Ace.setVerbose(false);
        DoctorJ drj = new DoctorJ(args);
        System.exit(drj.getExitValue());
    }
}
