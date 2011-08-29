package org.incava.doctorj;

import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.io.Find;
import org.incava.pmd.SimpleNodeUtil;
import org.incava.util.TimedEvent;
import org.incava.util.TimedEventSet;


public class DoctorJ
{
    private TimedEventSet _totalInit = new TimedEventSet();

    private TimedEventSet _totalParse = new TimedEventSet();

    private TimedEventSet _totalAnalysis = new TimedEventSet();

    private JavaParser _parser = null;

    private Report _report = null;

    private JavaParserVisitor _analyzer;

    private int _exitValue;

    private int _nFiles;

    public DoctorJ(String[] args) {
        tr.Ace.set(true, 25, 4, 20, 25);
        tr.Ace.setOutput(tr.Ace.VERBOSE, tr.Ace.LEVEL4);
        tr.Ace.setOutput(tr.Ace.QUIET,   tr.Ace.LEVEL2);

        _exitValue = 0;
        _nFiles = 0;

        Options  opts = Options.get();
        String[] names = opts.process(args);

        tr.Ace.log("args", args);
        tr.Ace.log("names", names);
        tr.Ace.log("properties", System.getProperties());

        if (opts.emacsOutput) {
            _report = new TerseReport(System.out);
        }
        else {
            _report = new ContextReport(System.out);
        }
        
        _analyzer = new JavadocAnalyzer(_report);

        for (int ni = 0; ni < names.length; ++ni) {
            String name = names[ni];
            process(name);
        }
        
        if (_nFiles > 1) {
            long total = _totalInit.duration + _totalParse.duration + _totalAnalysis.duration;
            tr.Ace.log("total time: " + total);
            tr.Ace.log("init      : " + _totalInit.duration);
            tr.Ace.log("parse     : " + _totalParse.duration);
            tr.Ace.log("analysis  : " + _totalAnalysis.duration);
            tr.Ace.log("#files    : " + _nFiles);
        }
    }

    public int getExitValue() {
        return _exitValue;
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
        ++_nFiles;

        if (initParser(fileName)) {
            ASTCompilationUnit cu = parse(fileName);
            if (cu != null) {
                analyze(cu);
            }
        }
    }

    protected boolean initParser(String fileName) {
        tr.Ace.log("fileName", fileName);

        TimedEvent init = new TimedEvent(_totalInit);
        try {
            _report.reset(new File(fileName));
                
            // = fromIsStdin ? new FileReader(FileDescriptor.in) :
            FileReader     rdr = new FileReader(fileName);
            JavaCharStream jcs = new JavaCharStream(rdr);
            
            _parser = new JavaParser(jcs);

            String src = Options.get().source;
            if (src.equals("1.3")) {
                tr.Ace.log("setting as 1.3");
                _parser.setJDK13();
            }
            else if (src.equals("1.4")) {
                tr.Ace.log("leaving as 1.4");
            }
            else if (src.equals("1.5")) {
                tr.Ace.log("setting as 1.5");
                _parser.setJDK15();
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
            _exitValue = 1;

            return false;
        }
        catch (IOException e) {
            System.out.println("Error opening " + fileName + ": " + e);
            _exitValue = 1;

            return false;
        }
        catch (TokenMgrError tme) {
            System.out.println("Error parsing (tokenizing) " + fileName + ": " + tme.getMessage());
            _exitValue = 1;

            return false;
        }
    }

    protected ASTCompilationUnit parse(String fileName) {
        tr.Ace.log("running parser");
            
        try {
            TimedEvent         parse = new TimedEvent(_totalParse);
            ASTCompilationUnit cu = _parser.CompilationUnit();
            parse.end();

            tr.Ace.log("parse: " + parse.duration);

            return cu;
        }
        catch (ParseException e) {
            System.out.println("Parse error in " + fileName + ": " + e.getMessage());
            _exitValue = 1;

            return null;
        }
    }

    protected void analyze(ASTCompilationUnit cu) {
        TimedEvent analysis = new TimedEvent(_totalAnalysis);
        cu.jjtAccept(_analyzer, null);

        _report.flush();
        analysis.end();

        tr.Ace.log("analysis: " + analysis.duration);
    }

    public static void main(String[] args) {
        tr.Ace.setVerbose(false);
        
        DoctorJ drj = new DoctorJ(args);
        System.exit(drj.getExitValue());
    }

}
