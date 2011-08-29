package org.incava.analysis;

import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.Token;


/**
 * An error or a warning, associated with a file by a starting and ending
 * position, and a message.
 */
public class Violation implements Comparable
{
    /**
     * The message for this violation. This should be only one line, because it
     * is used in single - line reports.
     */
    private String _message;
   
    /**
     * The line where the violation starts.
     */
    private int _beginLine;

    /**
     * The column where the violation starts.
     */
    private int _beginColumn;

    /**
     * The line where the violation ends.
     */
    private int _endLine;

    /**
     * The column where the violation ends.
     */
    private int _endColumn;

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
        _message = message;
        _beginLine = beginLine;
        _beginColumn = beginColumn;
        _endLine = endLine;
        _endColumn = endColumn;

        tr.Ace.log("[" + _beginLine + ":" + _beginColumn + " .. " + _endLine + ":" + _endColumn + "] (" + _message + ")");
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
        return _message;
    }
   
    /**
     * Returns the line where the violation starts.
     */
    public int getBeginLine() {
        return _beginLine;
    }

    /**
     * Returns the column where the violation starts.
     */
    public int getBeginColumn() {
        return _beginColumn;
    }

    /**
     * Returns the line where the violation ends.
     */
    public int getEndLine() {
        return _endLine;
    }

    /**
     * Returns the column where the violation ends.
     */
    public int getEndColumn() {
        return _endColumn;
    }

    /**
     * Compares this violation to another. Violations are sorted in order by
     * their beginning locations, then their end locations.
     *
     * @param obj The violation to compare this to.
     * @return - 1, 0, or 1, for less than, equivalent to, or greater than.
     */
    public int compareTo(Object obj) {
        if (equals(obj)) {
            return 0;
        }
        else {
            Violation v = (Violation)obj;
            int[][] nums = new int[][] {
                { _beginLine,   v.getBeginLine() },
                { _beginColumn, v.getBeginColumn() },
                { _endLine,     v.getEndLine() },
                { _endColumn,   v.getEndColumn() }
            };

            for (int ni = 0; ni < nums.length; ++ni) {
                int diff = nums[ni][0] - nums[ni][1];
                if (diff != 0) {
                    return diff;
                }
            }
            
            return _message.compareTo(v.getMessage());
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
        return (_beginLine == v.getBeginLine()   &&
                _beginColumn == v.getBeginColumn() &&
                _endLine == v.getEndLine()     &&
                _endColumn == v.getEndColumn());
    }

    /**
     * Returns this violation, as a string.
     *
     * @return This violation, as a string.
     */
    public String toString() {
        return "[" + _beginLine + ":" + _beginColumn + " .. " + _endLine + ":" + _endColumn + "] (" + _message + ")";
    }

}
