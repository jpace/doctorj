package org.incava.doctorj;

import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.ijdk.io.Find;
import org.incava.ijdk.util.TimedEvent;
import org.incava.ijdk.util.TimedEventSet;
import org.incava.pmdx.SimpleNodeUtil;


public class DoctorJ {

    private TimedEventSet totalInit = new TimedEventSet();

    private TimedEventSet totalParse = new TimedEventSet();

    private TimedEventSet totalAnalysis = new TimedEventSet();

    private JavaParser parser = null;

    private final Report report;

    private final JavaParserVisitor analyzer;

    private int exitValue;

    private int nFiles;

    public DoctorJ(String[] args) {
        tr.Ace.set(true, 25, 4, 20, 25);
        tr.Ace.setOutput(tr.Ace.VERBOSE, tr.Ace.LEVEL4);
        tr.Ace.setOutput(tr.Ace.QUIET,   tr.Ace.LEVEL2);

        this.exitValue = 0;
        this.nFiles = 0;

        Options  opts = Options.getInstance();
        String[] names = opts.process(args);

        tr.Ace.log("args", args);
        tr.Ace.log("names", names);
        tr.Ace.log("properties", System.getProperties());

        if (opts.getEmacsOutput()) {
            this.report = new TerseReport(System.out);
        }
        else {
            this.report = new ContextReport(System.out);
        }
        
        this.analyzer = new JavaAnalyzer(this.report, opts);

        for (int ni = 0; ni < names.length; ++ni) {
            String name = names[ni];
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

    protected void process(String name) {
        File fd = new File(name);
        if (fd.exists()) {
            if (fd.isDirectory()) {
                tr.Ace.log("processing directory");
                String[] contents = fd.list();
                Arrays.sort(contents);
                for (int ci = 0; ci < contents.length; ++ci) {
                    String nm = contents[ci];
                    String fullName = name + File.separator + nm;
                    File   f = new File(fullName);
                    if (f.isDirectory()) {
                        process(fullName);
                    }
                    else if (f.isFile() && nm.endsWith(".java")) {
                        processFile(fullName);
                    }
                    else {
                        tr.Ace.log("not a match", f);
                    }   
                }
            }
            else if (fd.isFile()) {
                processFile(name);
            }
        }
        else {
            System.err.println("File " + name + " not found.");
        }
    }
    
    protected void processFile(String fileName) {
        ++this.nFiles;

        if (initParser(fileName)) {
            ASTCompilationUnit cu = parse(fileName);
            if (cu != null) {
                analyze(cu);
            }
        }
    }

    protected boolean initParser(String fileName) {
        tr.Ace.log("fileName", fileName);

        TimedEvent init = new TimedEvent(this.totalInit);
        try {
            this.report.reset(new File(fileName));
                
            // = fromIsStdin ? new FileReader(FileDescriptor.in) :
            FileReader     rdr = new FileReader(fileName);
            JavaCharStream jcs = new JavaCharStream(rdr);
            
            this.parser = new JavaParser(jcs);

            String src = Options.getInstance().getSource();
            if (src.equals("1.3")) {
                tr.Ace.log("setting as 1.3");
                this.parser.setJDK13();
            }
            else if (src.equals("1.4")) {
                tr.Ace.log("leaving as 1.4");
            }
            else if (src.equals("1.5") || src.equals("1.6")) {
                tr.Ace.log("setting as 1.5");
                this.parser.setJDK15();
            }
            else {
                System.err.println("ERROR: source version '" + src + "' not recognized");
                System.exit(-1);
            }
            init.end();

            tr.Ace.log("init: " + init.duration);

            return true;
        }
        catch (FileNotFoundException e) {
            System.out.println("File " + fileName + " not found.");
            this.exitValue = 1;

            return false;
        }
        catch (IOException e) {
            System.out.println("Error opening " + fileName + ": " + e);
            this.exitValue = 1;

            return false;
        }
        catch (TokenMgrError tme) {
            System.out.println("Error parsing (tokenizing) " + fileName + ": " + tme.getMessage());
            this.exitValue = 1;

            return false;
        }
    }

    protected ASTCompilationUnit parse(String fileName) {
        tr.Ace.log("running parser");
            
        try {
            TimedEvent         parse = new TimedEvent(this.totalParse);
            ASTCompilationUnit cu = this.parser.CompilationUnit();
            parse.end();

            tr.Ace.log("parse: " + parse.duration);

            return cu;
        }
        catch (ParseException e) {
            System.out.println("Parse error in " + fileName + ": " + e.getMessage());
            this.exitValue = 1;

            return null;
        }
    }

    protected void analyze(ASTCompilationUnit cu) {
        TimedEvent analysis = new TimedEvent(this.totalAnalysis);
        cu.jjtAccept(this.analyzer, null);

        this.report.flush();
        analysis.end();

        tr.Ace.log("analysis: " + analysis.duration);
    }

    public static void main(String[] args) {
        tr.Ace.setVerbose(false);
        
        DoctorJ drj = new DoctorJ(args);
        System.exit(drj.getExitValue());
    }

}
