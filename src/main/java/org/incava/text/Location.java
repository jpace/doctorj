package org.incava.text;


/**
 * Code location.
 */
public class Location {

    public final int line;
    
    public final int column;

    public Location(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    public String toString() {
        return "[line: " + line + ", column: " + column + "]";
    }

    public boolean equals(Object obj) {
        return obj instanceof Location && equals((Location)obj);
    }

    public boolean equals(Location other) {
        return other.line == line && other.column == column;
    }

}
