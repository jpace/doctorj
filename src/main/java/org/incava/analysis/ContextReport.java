package org.incava.analysis;

import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.lang.StringExt;
import org.incava.ijdk.util.ANSI;


/**
 * Reports errors in multiple lines, displaying the contextual code, and
 * denoting the code to which a violation applies.
 */
public class ContextReport extends Report {
    
    /**
     * The number of spaces a tab is equivalent to.
     */
    public static int tabWidth = 4;

    /**
     * The end - of - line character/sequence for this OS.
     */
    private final static String EOLN = System.getProperty("line.separator");

    /**
     * The reader associated with the source code, which is used for reproducing
     * the code associated with a violation.
     */
    private Reader sourceReader;

    /**
     * Whether the file name has been written yet.
     */
    private boolean wroteFileName = false;

    /**
     * The contents, separated by new lines, which are included at the end of
     * each string.
     */
    private String[] contents;

    /**
     * Creates a context report for the given writer.
     *
     * @param writer The writer associated with this report.
     */
    public ContextReport(Writer writer) {
        super(writer);
    }

    /**
     * Creates a context report for the given output stream.
     *
     * @param os The output stream associated with this report.
     */
    public ContextReport(OutputStream os) {
        super(os);
    }

    /**
     * Creates a context report for the given writer, and a string source.
     *
     * @param writer The writer associated with this report.
     * @param source The source code to which this report applies.
     */
    public ContextReport(Writer writer, String source) {
        super(writer, source);
    }

    /**
     * Creates a context report for the given writer, and a file source.
     *
     * @param writer The writer associated with this report.
     * @param file The file, containing source code, to which this report applies.
     */
    public ContextReport(Writer writer, File file) {
        super(writer, file);
    }

    /**
     * Creates a context report for the given output stream, and string source.
     *
     * @param os The output stream associated with this report.
     * @param source The source code to which this report applies.
     */
    public ContextReport(OutputStream os, String source) {
        super(os, source);
    }

    /**
     * Creates a context report for the given output stream, and file.
     *
     * @param os The output stream associated with this report.
     * @param file The file, containing source code, to which this report applies.
     */
    public ContextReport(OutputStream os, File file) {
        super(os, file);
    }
    
    /**
     * Associates the given file with the list of violations, including that are
     * adding to this report later, i.e., prior to <code>flush</code>.
     *
     * @param file The file associated with the set of violations.
     */
    public void reset(File file) {
        super.reset(file);
        wroteFileName = false;
        contents = null;
        
        try {
            sourceReader = new FileReader(file);
        }
        catch (IOException ioe) {
            tr.Ace.log("error reading file: " + file);
        }
    }

    /**
     * Associates the given string source with the list of violations, including
     * that are adding to this report later, i.e., prior to <code>flush</code>.
     *
     * @param source The source code associated with the set of violations.
     */
    public void reset(String source) {
        super.reset(source);
        wroteFileName = false;
        contents = null;
        
        sourceReader = new StringReader(source);
    }

    /**
     * Returns a string representing the given violation, consistent with the
     * format of the Report subclass.
     *
     * @param violation The violation to represent as a string.
     */
    protected String toString(Violation violation) {
        StringBuffer buf = new StringBuffer();
        if (!wroteFileName) {
            buf.append("In " + ANSI.BOLD + ANSI.REVERSE + fileName + ANSI.RESET + ":" + EOLN + EOLN);
            wroteFileName = true;
        }

        if (contents == null) {
            try {
                List<String>   cont = new ArrayList<String>();
                BufferedReader br   = new BufferedReader(sourceReader);
                
                String line = br.readLine();
                while (line != null) {
                    cont.add(line);
                    line = br.readLine();
                }

                contents = cont.toArray(new String[cont.size()]);
            }
            catch (IOException ioe) {
                tr.Ace.log("error reading source: " + ioe);
            }
        }

        int beginLine = violation.getBeginLine()   - 1;
        int endLine = violation.getEndLine()     - 1;
        int beginColumn = violation.getBeginColumn() - 1;
        int endColumn = violation.getEndColumn()   - 1;
        
        if (beginLine == endLine) {
            writeLine(buf, beginLine);
            underline(buf, beginLine, beginColumn, endColumn);
        }
        else {
            markToEndOfLine(buf, beginLine, beginColumn);
            for (int lnum = beginLine; lnum <= endLine; ++lnum) {
                writeLine(buf, lnum);
            }
            markToStartPosition(buf, endLine, endColumn);
        }
        
        buf.append("*** " + violation.getMessage() + EOLN);
        buf.append(EOLN);
        
        return buf.toString();
    }

    /**
     * Adds indentation to the buffer, replacing spacing and tabs. Replaces tabs
     * with <code>tabWidth</code> occurrences of <code>ch</code>.
     *
     * @param buf The buffer to be written to.
     * @param line The current line number.
     * @param column The column to indent to.
     * @param ch The character with which to replace spaces and tabs.
     */
    protected void indent(StringBuffer buf, int line, int column, char ch) {
        buf.append("        ");

        // move it over for the column, replacing tabs with spaces
        buf.append(StringExt.repeat(ch, column));
    }

    /**
     * Marks the given line with leading spaces to the column position
     * (inclusive), and from there marking to the end of the line with
     * "<---...".
     *
     * @param buf The buffer to be written to.
     * @param line The current line number.
     * @param column The column to mark to/from.
     */
    protected void markToEndOfLine(StringBuffer buf, int line, int column) {
        indent(buf, line, column, ' ');
        
        int len = contents[line].length();
        
        buf.append('<');
        for (int i = column + 1; i < len; ++i) {
            buf.append('-');
        }
        buf.append(EOLN);
    }

    /**
     * Marks the given line with "...--->" leading to the column position
     * (inclusive).
     *
     * @param buf The buffer to be written to.
     * @param line The current line number.
     * @param column The column to mark to.
     */
    protected void markToStartPosition(StringBuffer buf, int line, int column) {
        indent(buf, line, column, '-');

        buf.append('>');
        buf.append(EOLN);
    }

    /**
     * Underlines ("<--...-->") from <code>beginColumn</code> to
     * <code>endColumn</code> in the given line. If the columns are equal, a
     * single caret is shown.
     *
     * @param buf The buffer to be written to.
     * @param line The current line number.
     * @param beginColumn The column to mark from.
     * @param endColumn The column to mark to.
     */
    protected void underline(StringBuffer buf, int line, int beginColumn, int endColumn) {
        indent(buf, line, beginColumn, ' ');
        
        if (beginColumn == endColumn) {
            buf.append('^');
        }
        else {
            buf.append('<');
            for (int i = beginColumn + 1; i < endColumn; ++i) {
                buf.append('-');
            }
            buf.append('>');
        }
        buf.append(EOLN);
    }

    /**
     * Writes the given line, adding the line number, right - aligned. Appends the
     * end - of - line character/sequence.
     *
     * @param buf The buffer to be written to.
     * @param line The current line number.
     */
    protected void writeLine(StringBuffer buf, int line) {
        StringBuffer lnBuf = new StringBuffer("" + (1 + line));
        while (lnBuf.length() < 6) {
            lnBuf.insert(0, ' ');
        }

        buf.append(lnBuf);
        buf.append(". ");
        buf.append(contents[line]);
        buf.append(EOLN);
    }

}
