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

            LineMapping lines       = new LineMapping(text, startLine, startColumn);
            Location    endLocation = lines.getLocation(text.length() - 1);

            JavadocDescriptionNode  description = null;
            List<JavadocTaggedNode> taggedNodes = new ArrayList<JavadocTaggedNode>();

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

                for (int i = 0; it.hasNext(); ++i) {
                    Point      point     = it.next();
                    TextRange  locations = lines.getLocations(point);
                    
                    taggedNodes.add(JavadocTaggedNode.create(text.substring(point.x, point.y), locations.getStart(), locations.getEnd()));
                }
            }

            TextLocation start = new TextLocation(TextLocation.UNDEFINED, startLine, startColumn);
            TextLocation end   = new TextLocation(TextLocation.UNDEFINED, endLocation.line, endLocation.column);

            return new JavadocNode(description, taggedNodes.toArray(new JavadocTaggedNode[taggedNodes.size()]), start, end);
        }
    }

    private static TextLocation skipWhitespace(String text, TextLocation tl, int len) {
        int pos = tl.getPosition();
        while (pos < len && Character.isWhitespace(text.charAt(pos))) {
            ++pos;
        }
        return new TextLocation(pos, 0, 0);
    }

    private static boolean isCommentCharacter(String text, int pos) {
        return Character.isWhitespace(text.charAt(pos)) || text.charAt(pos) == '*';
    }
    
    private static TextLocation skipCommentCharacters(String text, int pos, int len) {
        while (pos < len && isCommentCharacter(text, pos)) {
            ++pos;
        }
        return new TextLocation(pos, 0, 0);
    }

    private static int getEndOfCommentLocation(String text, int idx) {
        while (idx >= 0) {
            if (isCommentCharacter(text, idx)) {
                --idx;
            }
            else if (idx > 0 && text.startsWith("*/", idx - 1)) {
                idx -= 2;
            }
            else {
                break;
            }
        }
        // this will be the first character *beyond* the string, that is, equal
        // to the last, and not the index of the final character.
        return idx + 1;
    }

    public static List<Point> parse(String text, int startLine, int startColumn) {
        int len = text.length();
        List<Point> ary = new ArrayList<Point>();

        TextLocation tl = new TextLocation(0, startLine, startColumn);

        tl = skipWhitespace(text, tl, len);
        
        int pos = tl.getPosition();

        if (pos + 3 < len && text.startsWith("/**")) {  // unmangle Emacs: */
            tl = skipCommentCharacters(text, pos + 3, len);
            pos = tl.getPosition();

            // rewind end through comment characters

            len = getEndOfCommentLocation(text, len - 1);
            
            if (pos < len) {
                // the description
                if (text.charAt(pos) == '@') {
                    // null means no description
                    ary.add(null);
                }
                else {
                    tl = readDescription(ary, text, pos, len);
                    pos = tl.getPosition();
                }

                // now, the tagged comments:
                pos = readTagList(ary, text, pos, len);
            }

            return ary;
        }
        else {
            return null;
        }
    }

    private static TextLocation readDescription(List<Point> ary, String text, int pos, int len) {
        Point desc = new Point(pos, -1);

        TextLocation tl = new TextLocation(pos, TextLocation.UNDEFINED, TextLocation.UNDEFINED);
        
        tl = read(desc, text, tl, len);

        pos = tl.getPosition();

        ary.add(desc);

        return new TextLocation(pos, TextLocation.UNDEFINED, TextLocation.UNDEFINED);
    }

    private static int readTagList(List<Point> ary, String text, int pos, int len) {
        while (pos < len && text.charAt(pos) == '@') {
            Point tag = new Point(pos, -1);
            
            ++pos;

            TextLocation tl = new TextLocation(pos, TextLocation.UNDEFINED, TextLocation.UNDEFINED);
            
            tl = read(tag, text, tl, len);

            pos = tl.getPosition();
            
            ary.add(tag);
        }

        return pos;
    }

    /**
     * Reads to the next Javadoc field, or to the end of the comment.
     */
    private static TextLocation read(Point pt, String text, TextLocation tl, int len) {
        int pos = tl.getPosition();
        
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
            pos = skipCommentCharacters(text, pos, len).getPosition();
        }

        ++pt.y;

        return new TextLocation(pos, tl.getLine(), tl.getColumn());
    }
}
