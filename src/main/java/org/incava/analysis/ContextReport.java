package org.incava.analysis;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.List;
import org.incava.ijdk.io.FileExt;
import org.incava.ijdk.io.ReadOptionType;
import org.incava.ijdk.io.ReaderExt;
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
    private List<String> contents;

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
        StringBuilder sb = new StringBuilder();
        if (!wroteFileName) {
            sb.append("In " + ANSI.BOLD + ANSI.REVERSE + fileName + ANSI.RESET + ":" + EOLN + EOLN);
            wroteFileName = true;
        }

        if (contents == null) {
            contents = ReaderExt.readLines(sourceReader, EnumSet.noneOf(ReadOptionType.class));
        }

        int beginLine = violation.getBeginLine() - 1;
        int endLine = violation.getEndLine() - 1;
        int beginColumn = violation.getBeginColumn() - 1;
        int endColumn = violation.getEndColumn() - 1;

        final int lineNumWidth = 6;
        
        if (beginLine == endLine) {
            writeLine(sb, beginLine, lineNumWidth);
            underline(sb, beginLine, beginColumn, endColumn);
        }
        else {
            markToEndOfLine(sb, beginLine, beginColumn);
            for (int lnum = beginLine; lnum <= endLine; ++lnum) {
                writeLine(sb, lnum, lineNumWidth);
            }
            markToStartPosition(sb, endLine, endColumn);
        }
        
        sb.append("*** " + violation.getMessage() + EOLN);
        sb.append(EOLN);
        
        return sb.toString();
    }

    /**
     * Adds indentation to the buffer.
     *
     * @param sb The buffer to be written to.
     * @param line The current line number.
     * @param column The column to indent to.
     * @param ch The character with which to replace spaces and tabs.
     */
    protected void indent(StringBuilder sb, int line, int column, char ch) {
        sb.append(StringExt.repeat(' ', 8));
        sb.append(StringExt.repeat(ch, column));
    }

    /**
     * Marks the given line with leading spaces to the column position
     * (inclusive), and from there marking to the end of the line with
     * "<---...".
     *
     * @param sb The buffer to be written to.
     * @param line The current line number.
     * @param column The column to mark to/from.
     */
    protected void markToEndOfLine(StringBuilder sb, int line, int column) {
        indent(sb, line, column, ' ');
        
        int len = contents.get(line).length();
        
        sb.append('<');
        sb.append(StringExt.repeat('-', len - column - 1));
        sb.append(EOLN);
    }

    /**
     * Marks the given line with "...--->" leading to the column position
     * (inclusive).
     *
     * @param sb The buffer to be written to.
     * @param line The current line number.
     * @param column The column to mark to.
     */
    protected void markToStartPosition(StringBuilder sb, int line, int column) {
        indent(sb, line, column, '-');

        sb.append('>');
        sb.append(EOLN);
    }

    /**
     * Underlines ("<--...-->") from <code>beginColumn</code> to
     * <code>endColumn</code> in the given line. If the columns are equal, a
     * single caret is shown.
     *
     * @param sb The buffer to be written to.
     * @param line The current line number.
     * @param beginColumn The column to mark from.
     * @param endColumn The column to mark to.
     */
    protected void underline(StringBuilder sb, int line, int beginColumn, int endColumn) {
        indent(sb, line, beginColumn, ' ');
        
        if (beginColumn == endColumn) {
            sb.append('^');
        }
        else {
            sb.append('<');
            sb.append(StringExt.repeat('-', endColumn - beginColumn - 1));
            sb.append('>');
        }
        sb.append(EOLN);
    }

    /**
     * Writes the given line, adding the line number, right-aligned. Appends the
     * end-of-line character/sequence. Assumes that lines are zero-indexed, and
     * that the output should be one-indexed.
     *
     * @param sb The buffer to be written to.
     * @param lineNum The current line number.
     * @param lineNumWidth The number of characters for the line number.
     */
    protected void writeLine(StringBuilder sb, int lineNum, int lineNumWidth) {
        Formatter fmtr = new Formatter(sb);
        fmtr.format("%" + lineNumWidth + "d. %s%s", lineNum + 1, this.contents.get(lineNum), EOLN);
    }
}
