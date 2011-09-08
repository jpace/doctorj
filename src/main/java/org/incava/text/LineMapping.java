package org.incava.text;

import java.awt.Point;
import java.io.*;
import java.util.*;


/**
 * Converts from 0-indexed string positions to line:column values. Lines and
 * columns are 1-indexed, matching the Java parser.
 */
public class LineMapping extends ArrayList<LineMapping.PositionToLocation> {

    private static final long serialVersionUID = 1;

    public class PositionToLocation {

        public final int position;
        
        public final int line;

        public final int column;

        public PositionToLocation(int position, int line, int column) {
            this.position = position;
            this.line = line;
            this.column = column;
        }

        public String toString() {
            return "{ position: " + position + " => { line: " + line + ", column: " + column + " } }";
        }
    }

    public LineMapping(String text, int startLine, int startColumn) {
        add(new PositionToLocation(0, startLine, startColumn));

        int len = text.length();

        // figure out where the lines start:
        for (int pos = 0, line = startLine; pos < len; ++pos) {
            // Mac: \r, Unix: \n, DOS: \r\n
            if (text.charAt(pos) == '\r') {
                // might be DOS:
                if (pos + 1 < len && text.charAt(pos + 1) == '\n') {
                    ++pos;
                }
                // otherwise, it's Mac (at least, before OS X)
            }
            else if (text.charAt(pos) == '\n') {
                // Unix
            }
            else {
                // we're not at a newline, so go to the next character.
                continue;
            }

            ++line;

            add(new PositionToLocation(pos + 1, line, 1));
        }
    }

    /**
     * Converts the string position to a line:column start and end location.
     */
    public TextRange getLocations(Point pos) {
        return getLocations(pos.x, pos.y);
    }

    /**
     * Converts the string position to a line:column start and end location.
     */
    public TextRange getLocations(int startPos, int endPos) {
        // tr.Ace.log("parsing description");
        TextLocation start = null;
        TextLocation end = null;
        
        // go backward
        ListIterator<PositionToLocation> lit = listIterator(size());
        while ((start == null || end == null) && lit.hasPrevious()) {
            PositionToLocation pl = lit.previous();
            if (end == null && endPos >= pl.position) {
                end = new TextLocation(TextLocation.UNDEFINED, pl.line, pl.column + endPos - pl.position);
            }
            if (start == null && startPos >= pl.position) {
                start = new TextLocation(TextLocation.UNDEFINED, pl.line, pl.column + startPos - pl.position);
            }
        }

        // attn Sun: tuples, please!
        return new TextRange(start, end);
    }
    
    /**
     * Converts the string position to a line:column location.
     */
    public TextLocation getLocation(int pos) {
        // go backward
        ListIterator<PositionToLocation> lit = listIterator(size());
        while (lit.hasPrevious()) {
            PositionToLocation pl = lit.previous();
            if (pos >= pl.position) {
                // tr.Ace.log("creating location");
                return new TextLocation(TextLocation.UNDEFINED, pl.line, pl.column + pos - pl.position);
            }
        }

        return null;
    }
    
}
