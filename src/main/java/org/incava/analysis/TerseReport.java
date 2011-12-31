package org.incava.analysis;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Reports errors in a single line, AKA the format expected by Emacs (!c).
 */
public class TerseReport extends Report {
    /**
     * Creates a terse report for the given writer.
     *
     * @param writer The writer associated with this report.
     */
    public TerseReport(Writer writer) {
        super(writer);
    }

    /**
     * Creates a terse report for the given output stream.
     *
     * @param os The output stream associated with this report.
     */
    public TerseReport(OutputStream os) {
        super(os);
    }

    /**
     * Creates a terse report for the given writer, and a string source.
     *
     * @param writer The writer associated with this report.
     * @param source The source code to which this report applies.
     */
    public TerseReport(Writer writer, String source) {
        super(writer, source);
    }

    /**
     * Creates a terse report for the given writer, and a file source.
     *
     * @param writer The writer associated with this report.
     * @param file The file, containing source code, to which this report applies.
     */
    public TerseReport(Writer writer, File file) {
        super(writer, file);
    }

    /**
     * Creates a terse report for the given output stream, and string source.
     *
     * @param os The output stream associated with this report.
     * @param source The source code to which this report applies.
     */
    public TerseReport(OutputStream os, String source) {
        super(os, source);
    }

    /**
     * Creates a terse report for the given output stream, and file.
     *
     * @param os The output stream associated with this report.
     * @param file The file, containing source code, to which this report applies.
     */
    public TerseReport(OutputStream os, File file) {
        super(os, file);
    }

    /**
     * Returns the given violation, in single - line format. For example:
     *
     * <pre>
     *     TerseReport.java:77:22:77:29: Undocumented protected method
     * </pre>
     *
     * @param violation The violation to represent as a single - line violation.
     * @return The violation, in single - line format.
     */
    protected String toString(Violation violation) {
        return (fileName + ":" + 
                violation.getBeginLine() + ":" + violation.getBeginColumn() + ":" + 
                violation.getEndLine()   + ":" + violation.getEndColumn()   + ": " + 
                violation.getMessage() + System.getProperty("line.separator"));
    }
}
