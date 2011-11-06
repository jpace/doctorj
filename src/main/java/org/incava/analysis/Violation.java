package org.incava.analysis;

import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.lang.ObjectExt;


/**
 * An error or a warning, associated with a file by a starting and ending
 * position, and a message.
 */
public class Violation implements Comparable {

    /**
     * The message for this violation. This should be only one line, because it
     * is used in single-line reports.
     */
    private String message;
   
    /**
     * The line where the violation starts.
     */
    private int beginLine;

    /**
     * The column where the violation starts.
     */
    private int beginColumn;

    /**
     * The line where the violation ends.
     */
    private int endLine;

    /**
     * The column where the violation ends.
     */
    private int endColumn;

    /**
     * Creates a violation from a message and begin and end positions.
     *
     * @param message     The message applying to this violation.
     * @param beginLine   The line where the violation begins.
     * @param beginColumn The column where the violation begins.
     * @param endLine     The line where the violation ends.
     * @param endColumn   The column where the violation ends.
     */
    public Violation(String message, int beginLine, int beginColumn, int endLine, int endColumn) {
        this.message = message;
        this.beginLine = beginLine;
        this.beginColumn = beginColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;

        // tr.Ace.log("[" + this.beginLine + ":" + this.beginColumn + " .. " + this.endLine + ":" + this.endColumn + "] (" + this.message + ")");
    }

    /**
     * Creates a violation from a message and beginning and ending token.
     *
     * @param message     The message applying to this violation.
     * @param beginToken  The token where the violation begins.
     * @param endToken    The token where the violation ends.
     */
    public Violation(String message, Token beginToken, Token endToken) {
        this(message, beginToken.beginLine, beginToken.beginColumn, endToken.endLine, endToken.endColumn);
    }

    /**
     * Creates a violation from a message and a token. The token image is
     * considered to be the entire length of the violation, i.e., the ending
     * location is <code>token + token.image.length() - 1</code>.
     *
     * @param message The message applying to this violation.
     * @param token   The token to which the violation applies.
     */
    public Violation(String message, Token token) {
        this(message, token.beginLine, token.beginColumn, token.beginLine, token.beginColumn + token.image.length() - 1);
    }

    /**
     * Returns the message for this violation. This should be only one line,
     * because it is used in single - line reports.
     */
    public String getMessage() {
        return this.message;
    }
   
    /**
     * Returns the line where the violation starts.
     */
    public int getBeginLine() {
        return this.beginLine;
    }

    /**
     * Returns the column where the violation starts.
     */
    public int getBeginColumn() {
        return this.beginColumn;
    }

    /**
     * Returns the line where the violation ends.
     */
    public int getEndLine() {
        return this.endLine;
    }

    /**
     * Returns the column where the violation ends.
     */
    public int getEndColumn() {
        return this.endColumn;
    }

    /**
     * Compares this violation to another. Violations are sorted in order by
     * their beginning locations, then their end locations.
     *
     * @param obj The violation to compare this to.
     * @return -1, 0, or 1, for less than, equivalent to, or greater than.
     */
    public int compareTo(Object obj) {
        if (equals(obj)) {
            return 0;
        }
        else {
            Violation v = (Violation)obj;
            int[][] nums = new int[][] {
                { this.beginLine,   v.getBeginLine() },
                { this.beginColumn, v.getBeginColumn() },
                { this.endLine,     v.getEndLine() },
                { this.endColumn,   v.getEndColumn() }
            };

            for (int ni = 0; ni < nums.length; ++ni) {
                int diff = nums[ni][0] - nums[ni][1];
                if (diff != 0) {
                    return diff;
                }
            }
            
            return this.message.compareTo(v.getMessage());
        }
    }

    /**
     * Returns whether the other object is equal to this one. Note that messages
     * are not compared, only line and column numbers.
     *
     * @param obj The violation to compare this to.
     * @return Whether the other violation is equal to this one.
     */
    public boolean equals(Object obj) {
        Violation v = (Violation)obj;
        return (this.beginLine == v.getBeginLine()   &&
                this.beginColumn == v.getBeginColumn() &&
                this.endLine == v.getEndLine()     &&
                this.endColumn == v.getEndColumn() && 
                ObjectExt.areEqual(this.message, v.getMessage()));
    }

    /**
     * Returns this violation, as a string.
     */
    public String toString() {
        return "[" + this.beginLine + ":" + this.beginColumn + " .. " + this.endLine + ":" + this.endColumn + "] (" + this.message + ")";
    }
}
