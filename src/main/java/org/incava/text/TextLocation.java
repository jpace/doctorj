package org.incava.text;


/**
 * Location of text in a file, denoted by both relative position (0-indexed
 * point in a string) and a line and column number (both of which are
 * 1-indexed).
 */
public class TextLocation {

    private final int position;

    private final int line;
    
    private final int column;

    public TextLocation(int position, int line, int column) {
        this.position = position;
        this.line = line;
        this.column = column;
    }

    public int getPosition() {
        return this.position;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    public String toString() {
        return "[position: " + position + ", line: " + line + ", column: " + column + "]";
    }

    public boolean equals(Object obj) {
        return obj instanceof TextLocation && equals((TextLocation)obj);
    }

    public boolean equals(TextLocation other) {
        return other.position == position && other.line == line && other.column == column;
    }

}
