package org.incava.analysis;

import java.io.*;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import net.sourceforge.pmd.ast.Token;


/**
 * Reports errors (violations), in a format that is determined by the subclass.
 */
public abstract class Report
{
    /**
     * The file to which this report currently applies. By default, this is '-',
     * denoting standard output.
     */
    protected String fileName = "-";

    /**
     * The writer to which this report sends output.
     */
    private Writer writer;

    /**
     * The set of violations, which are maintained in sorted order.
     */
    private Set violations = new TreeSet();

    /**
     * Creates a report for the given writer.
     *
     * @param writer The writer associated with this report.
     */
    public Report(Writer writer)
    {
        this.writer = writer;
    }

    /**
     * Creates a report for the given output stream.
     *
     * @param os The output stream associated with this report.
     */
    public Report(OutputStream os)
    {
        this(new OutputStreamWriter(os));
    }

    /**
     * Creates a report for the given writer, and a string source.
     *
     * @param writer The writer associated with this report.
     * @param source The source code to which this report applies.
     */
    public Report(Writer writer, String source)
    {
        this(writer);

        reset(source);
    }

    /**
     * Creates a report for the given writer, and a file source.
     *
     * @param writer The writer associated with this report.
     * @param file The file, containing source code, to which this report applies.
     */
    public Report(Writer writer, File file)
    {
        this(writer);
        
        reset(file);
    }

    /**
     * Creates a report for the given output stream, and string source.
     *
     * @param os The output stream associated with this report.
     * @param source The source code to which this report applies.
     */
    public Report(OutputStream os, String source)
    {
        this(os);
        
        reset(source);
    }

    /**
     * Creates a report for the given output stream, and file.
     *
     * @param os The output stream associated with this report.
     * @param file The file, containing source code, to which this report applies.
     */
    public Report(OutputStream os, File file)
    {
        this(os);
        
        reset(file);
    }
    
    /**
     * Associates the given file with the list of violations, including that are
     * adding to this report later, i.e., prior to <code>flush</code>.
     *
     * @param file The file associated with the set of violations.
     */
    public void reset(File file)
    {
        tr.Ace.log("file", file);
        
        try {
            fileName = file.getCanonicalPath();
        }
        catch (IOException ioe) {
        }
    }

    /**
     * Associates the given string source with the list of violations, including
     * that are adding to this report later, i.e., prior to <code>flush</code>.
     *
     * @param source The source code associated with the set of violations.
     */
    public void reset(String source)
    {
        tr.Ace.log("source", source);
        
        fileName = "-";
    }

    /**
     * Writes all violations, and clears the list.
     */
    public void flush()
    {
        try {
            tr.Ace.stack("flushing");
            
            Iterator it = violations.iterator();
            while (it.hasNext()) {
                Object    obj = it.next();
                Violation v   = (Violation)obj;
                String    str = toString(v);
                tr.Ace.log("v", v);
                tr.Ace.log("str", str);
                writer.write(str);
            }
            // we can't close STDOUT
            writer.flush();
            // writer.close();
        }
        catch (IOException ioe) {
        }
        violations = new TreeSet();
    }

    /**
     * Adds the given violation.
     *
     * @param v The violation being added.
     */
    public void addViolation(Violation v)
    {
        tr.Ace.stack("v", v);
        violations.add(v);
    }

    /**
     * Exists only for testing.
     */
    public Set getViolations()
    {
        return violations;
    }
    
    /**
     * Returns a string representing the given violation, consistent with the
     * format of the Report subclass.
     *
     * @param violation The violation to represent as a string.
     */
    protected abstract String toString(Violation violation);

    /**
     * Sends the given string to the writer associated with this Report.
     *
     * @param str The string to be written.
     */
    protected void write(String str)
    {
        tr.Ace.log("str", str);
        try {
            writer.write(str);
        }
        catch (IOException ioe) {
        }
    }
}
