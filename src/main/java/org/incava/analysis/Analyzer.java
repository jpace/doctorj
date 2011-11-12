package org.incava.analysis;

import net.sourceforge.pmd.ast.Token;
import org.incava.text.Location;

/**
 * Bridge between violations and reports.
 */
public class Analyzer {

    /**
     * The report to which violations will be sent.
     */
    private Report report;

    /**
     * Creates an analyzer with a report.
     * 
     * @param r The report that this analyzer sends violations to.
     */
    public Analyzer(Report r) {
        report = r;
    }

    /**
     * Adds a violation with a single token. The addViolation methods
     * return false, for usage in conditionals.
     *
     * @param message The violation message.
     * @param token The token to which the violation applies.
     */
    public boolean addViolation(String message, Token token) {
        report.addViolation(new Violation(message, token));
        return false;
    }

    /**
     * Adds a violation spanning from one token to another.
     *
     * @param message The violation message.
     * @param firstToken The first token this violation spans.
     * @param lastToken The last token this violation spans, inclusive.
     */
    public boolean addViolation(String message, Token firstToken, Token lastToken) {
        report.addViolation(new Violation(message, firstToken, lastToken));
        return false;
    }

    /**
     * Adds a violation from one location to another.
     *
     * @param message The violation message.
     * @param start Where this violation begins.
     * @param end Where this violation ends, inclusive.
     */
    public boolean addViolation(String message, Location start, Location end) {
        report.addViolation(new Violation(message, start.line, start.column, end.line, end.column));
        return false;
    }

    /**
     * Adds a violation from a beginning position to an ending position.
     *
     * @param message The violation message.
     * @param beginLine The line where this violation begins.
     * @param beginColumn The column where this violation begins.
     * @param endLine The line where this violation ends.
     * @param endColumn The column where this violation ends.
     */
    public boolean addViolation(String message, int beginLine, int beginColumn, int endLine, int endColumn) {
        report.addViolation(new Violation(message, beginLine, beginColumn, endLine, endColumn));
        return false;
    }

    /**
     * Returns the report used by this analyzer.
     */
    protected Report getReport() {
        return report;
    }
}
