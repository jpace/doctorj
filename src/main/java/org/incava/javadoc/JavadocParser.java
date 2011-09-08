package org.incava.javadoc;

import java.awt.Point;
import java.util.*;
import org.incava.ijdk.lang.Pair;
import org.incava.text.*;


/**
 * Parses Javadoc into a list of points, which represent the locations of
 * description and tagged comments in a Javadoc comment block.
 */
public class JavadocParser {

    /**
     * Parses itself from the given text.
     */
    public static JavadocNode parseJavadocNode(String text, int startLine, int startColumn) {
        List<Point> subs = parse(text, startLine, startColumn);

        if (subs == null) {
            return null;
        }
        else {
            // store line positions, for converting string positions (which are
            // 0-based) to line:column (which are 1-based)

            LineMapping lines = new LineMapping(text, startLine, startColumn);

            Location end = lines.getLocation(text.length() - 1);
            JavadocDescriptionNode description = null;
            JavadocTaggedNode[] taggedNodes = new JavadocTaggedNode[0];

            if (subs.size() > 0) {
                Iterator<Point> it = subs.iterator();

                Point descPos = it.next();
                if (descPos != null) {
                    TextRange descRange = lines.getLocations(descPos);

                    // we could trim whitespace, so that the following descriptions are equivalent:

                    // /** \n
                    //   * This is a test. \n
                    //   */

                    // /** \n
                    //   * This is a test. \n
                    //   * @tag something
                    //   */

                    description = new JavadocDescriptionNode(text.substring(descPos.x, descPos.y), descRange.getStart(), descRange.getEnd());
                }

                taggedNodes = new JavadocTaggedNode[subs.size() - 1];
                for (int i = 0; it.hasNext(); ++i) {
                    Point      pos       = it.next();
                    TextRange  locations = lines.getLocations(pos);
                    
                    taggedNodes[i] = new JavadocTaggedNode(text.substring(pos.x, pos.y), locations.getStart(), locations.getEnd());
                }
            }

            return new JavadocNode(description, taggedNodes, startLine, startColumn, end.line, end.column);
        }
    }
    
    /**
     * Parses the Javadoc in the text. Assumes a start line of 1 and a start
     * column of 1.
     */
    public static List<Point> parse(String text) {
        return parse(text, 1, 1);
    }

    public static List<Point> parse(String text, int startLine, int startColumn) {
        int len = text.length();
        List<Point> ary = new ArrayList<Point>();
        int pos = 0;

        while (pos < len && Character.isWhitespace(text.charAt(pos))) {
            ++pos;
        }
        
        if (pos + 3 < len && text.startsWith("/**")) {  // unmangle Emacs: */
            // tr.Ace.log("got comment start");
            pos += 3;

            while (pos < len && (Character.isWhitespace(text.charAt(pos)) || text.charAt(pos) == '*')) {
                ++pos;
            }

            --len;
            while (len >= 0) {
                // tr.Ace.log("char[" + len + "]: '" + text.charAt(len) + "'");
                if (Character.isWhitespace(text.charAt(len)) || text.charAt(len) == '*') {
                    // tr.Ace.log("star or WS; (text: '" + text.charAt(len) + "')");
                    --len;
                }
                else if (len > 0 && text.charAt(len) == '/' && text.charAt(len - 1) == '*') {
                    // tr.Ace.log("at end of comment");
                    len -= 2;
                }
                else {
                    break;
                }
            }
            ++len;

            // tr.Ace.log("pos: " + pos + "; len: " + len);
            
            if (pos < len) {
                // the description
                if (text.charAt(pos) == '@') {
                    // tr.Ace.log("got tag start -- no description");
                    // null means no description
                    ary.add(null);
                }
                else {
                    // tr.Ace.log("at description start: " + pos);

                    Point desc = new Point(pos, -1);

                    pos = read(desc, text, pos, len);
                
                    // tr.Ace.log("at end, pos: " + pos + "; desc pos   : " + desc);

                    ary.add(desc);
                }

                // now, the tagged comments:
                while (pos < len && text.charAt(pos) == '@') {
                    // tr.Ace.log("tag starting.");

                    Point tag = new Point(pos, -1);

                    ++pos;
                    
                    pos = read(tag, text, pos, len);

                    // tr.Ace.log("tag pos   : " + tag);

                    ary.add(tag);
                }
            }
            
            // tr.Ace.log("returning: " + ary);

            return ary;
        }
        else {
            // tr.Ace.log("no Javadoc comment in this string.");
            return null;
        }
    }

    /**
     * Reads to the next Javadoc field, or to the end of the comment.
     */
    private static int read(Point pt, String text, int pos, int len) {
        tr.Ace.cyan("pt", pt);
        tr.Ace.cyan("text", text);
        tr.Ace.cyan("pos", pos);
        tr.Ace.cyan("len", len);
        
        pt.y = pos;
        while (pos < len && (text.charAt(pos) != '@' || (pos >= 0 && text.charAt(pos - 1) == '{'))) {
            pt.y = pos;

            ++pos;
                        
            // read to end of line, or end of text.
            // Mac: \r, Unix: \n, DOS: \r\n:
            if (text.charAt(pos) == '\r') {
                if (pos + 1 < len && text.charAt(pos + 1) == '\n') {
                    ++pos;
                }
            }
            else if (text.charAt(pos) != '\n') {
                continue;
            }

            // now, we're at the start of a new line:
            while (pos < len && (Character.isWhitespace(text.charAt(pos)) || text.charAt(pos) == '*')) {
                ++pos;
            }
        }

        ++pt.y;

        tr.Ace.cyan("pt", pt);
        tr.Ace.cyan("text", text);
        tr.Ace.cyan("pos", pos);
        tr.Ace.cyan("len", len);

        return pos;
    }

}
