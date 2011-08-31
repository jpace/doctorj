package org.incava.text;

import java.awt.Point;
import java.io.*;
import java.util.*;


/**
 * Converts from 0 - indexed string positions to line:column values. Lines and
 * columns are 1 - indexed, matching the Java parser.
 */
public class LineMapping extends ArrayList {

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
    public Location[] getLocations(Point pos) {
        return getLocations(pos.x, pos.y);
    }

    /**
     * Converts the string position to a line:column start and end location.
     */
    public Location[] getLocations(int startPos, int endPos) {
        // tr.Ace.log("parsing description");
        Location start = null;
        Location end = null;

        // tr.Ace.log("position: " + startPos + ", " + endPos);
        
        // go backward
        ListIterator lit = listIterator(size());
        while ((start == null || end == null) && lit.hasPrevious()) {
            PositionToLocation pl = (PositionToLocation)lit.previous();
            // tr.Ace.log("considering position/location " + pl);
            if (end == null && endPos >= pl.position) {
                // tr.Ace.log("assigning end");
                end = new Location(pl.line, pl.column + endPos - pl.position);
            }
            if (start == null && startPos >= pl.position) {
                // tr.Ace.log("assigning start");
                start = new Location(pl.line, pl.column + startPos - pl.position);
            }
        }

        // attn Sun: tuples, please!
        return new Location[] { start, end };
    }
    
    /**
     * Converts the string position to a line:column location.
     */
    public Location getLocation(int pos) {
        Location location = null;

        // tr.Ace.log("position: " + pos);
        
        // go backward
        ListIterator lit = listIterator(size());
        while (location == null && lit.hasPrevious()) {
            PositionToLocation pl = (PositionToLocation)lit.previous();
            // tr.Ace.log("considering position/location " + pl);
            if (location == null && pos >= pl.position) {
                // tr.Ace.log("creating location");
                return new Location(pl.line, pl.column + pos - pl.position);
            }
        }

        return null;
    }
    
}
