package org.incava.analysis;

import net.sourceforge.pmd.ast.Token;
import org.incava.text.Location;
import org.incava.text.LocationRange;

/**
 * An error or a warning, associated with a file by a starting and ending
 * position, and a message.
 */
public class Violation implements Comparable<Violation> {
    /**
     * The message for this violation. This should be only one line, because it
     * is used in single-line reports.
     */
    private final String message;

    private final LocationRange location;

    /**
     * Creates a violation from a message and begin and end positions.
     */
    public Violation(String message, LocationRange location) {
        this.message = message;
        this.location = location;
        
        // tr.Ace.log("[" + this.beginLine + ":" + this.beginColumn + " .. " + this.endLine + ":" + this.endColumn + "] (" + this.message + ")");
    }

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
        this(message, new LocationRange(new Location(beginLine, beginColumn), new Location(endLine, endColumn)));
        
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
     * Returns the location of the violation.
     */
    public LocationRange getLocation() {
        return this.location;
    }

    /**
     * Returns the beginning of the violation.
     */
    public Location getStart() {
        return getLocation().getStart();
    }

    /**
     * Returns the end of the violation.
     */
    public Location getEnd() {
        return getLocation().getEnd();
    }
   
    /**
     * Returns the line where the violation starts.
     */
    public int getBeginLine() {
        return getStart().getLine();
    }

    /**
     * Returns the column where the violation starts.
     */
    public int getBeginColumn() {
        return getStart().getColumn();
    }

    /**
     * Returns the line where the violation ends.
     */
    public int getEndLine() {
        return getEnd().getLine();
    }

    /**
     * Returns the column where the violation ends.
     */
    public int getEndColumn() {
        return getEnd().getColumn();
    }

    /**
     * Compares this violation to another. Violations are sorted in order by
     * their beginning locations, then their end locations.
     *
     * @param obj The violation to compare this to.
     * @return -1, 0, or 1, for less than, equivalent to, or greater than.
     */
    public int compareTo(Violation other) {
        int cmp = getLocation().compareTo(other.getLocation());
        if (cmp != 0) {
            return cmp;
        }
        
        return getMessage().compareTo(other.getMessage());
    }

    /**
     * Returns whether the other object is equal to this one. Note that messages
     * are not compared, only line and column numbers.
     *
     * @param obj The violation to compare this to.
     * @return Whether the other violation is equal to this one.
     */
    public boolean equals(Object obj) {
        return obj instanceof Violation && compareTo((Violation)obj) == 0;
    }
    
    /**
     * Returns this violation, as a string.
     */
    public String toString() {
        return "[" + this.location + "] (" + this.message + ")";
    }
}
