package org.incava.javadoc;

import java.awt.Point;
import java.util.*;
import org.incava.ijdk.lang.Pair;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.TextLocation;
import org.incava.ijdk.text.TextRange;
import org.incava.ijdk.util.ListExt;
import org.incava.text.LineMapping;

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
        return subs == null ? null : createNode(subs, text, startLine, startColumn);
    }

    private static Pair<Point, TextRange> getPointAndLocation(List<Point> subs, int idx, LineMapping lines) {
        Point pt = ListExt.get(subs, idx);
        if (pt == null) {
            return null;
        }
        TextRange locs = lines.getLocations(pt);
        return new Pair<Point, TextRange>(pt, locs);
    }

    private static JavadocDescriptionNode createDescription(List<Point> subs, String text, LineMapping lines) {
        Pair<Point, TextRange> ptLoc = getPointAndLocation(subs, 0, lines);
        return ptLoc == null ? null : new JavadocDescriptionNode(text.substring(ptLoc.getFirst().x, ptLoc.getFirst().y), ptLoc.getSecond());
    }

    private static JavadocTaggedNode createTaggedNode(List<Point> subs, String text, int idx, LineMapping lines) {
        Pair<Point, TextRange> ptLoc = getPointAndLocation(subs, idx, lines);
        return ptLoc == null ? null : createTaggedNode(text.substring(ptLoc.getFirst().x, ptLoc.getFirst().y), ptLoc.getSecond());
    }

    private static JavadocNode createNode(List<Point> subs, String text, int startLine, int startColumn) {
        // store line positions, for converting string positions (which are
        // 0-based) to line:column (which are 1-based)
        LineMapping lines       = new LineMapping(text, startLine, startColumn);
        Location    endLocation = lines.getLocation(text.length() - 1);

        JavadocDescriptionNode  description = createDescription(subs, text, lines);
        List<JavadocTaggedNode> taggedNodes = new ArrayList<JavadocTaggedNode>();

        for (int si = 1; si < subs.size(); ++si) {
            taggedNodes.add(createTaggedNode(subs, text, si, lines));
        }

        TextLocation start = new TextLocation(TextLocation.UNDEFINED, startLine, startColumn);
        TextLocation end   = new TextLocation(TextLocation.UNDEFINED, endLocation.line, endLocation.column);

        return new JavadocNode(description, taggedNodes, start, end);
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

    private static TextLocation readFromPoint(Point pt, List<Point> ary, String text, int pos, int len) {
        TextLocation tl = new TextLocation(pos, TextLocation.UNDEFINED, TextLocation.UNDEFINED);            
        tl  = read(pt, text, tl, len);
        pos = tl.getPosition();
        ary.add(pt);
        return new TextLocation(pos, TextLocation.UNDEFINED, TextLocation.UNDEFINED);
    }

    private static TextLocation readDescription(List<Point> ary, String text, int pos, int len) {
        Point desc = new Point(pos, -1);
        return readFromPoint(desc, ary, text, pos, len);
    }

    private static int readTagList(List<Point> ary, String text, int pos, int len) {
        while (pos < len && text.charAt(pos) == '@') {
            Point tag = new Point(pos, -1);
            TextLocation tl = readFromPoint(tag, ary, text, pos + 1, len);
            pos = tl.getPosition();
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

    public static JavadocTaggedNode createTaggedNode(String text, TextRange range) {
        return createTaggedNode(text, range.getStart(), range.getEnd());
    }

    public static JavadocTaggedNode createTaggedNode(String text, Location start, Location end) {
        JavadocTag tag = null;
        JavadocElement target = null;
        JavadocElement description = null;
        JavadocElement descriptionNonTarget = null;

        int pos = 0;
        int line = start.line;
        int col = start.column;
        int len = text.length();

        // has to be a tag first
        while (pos < len && !Character.isWhitespace(text.charAt(pos))) {
            ++pos;
        }

        tag = new JavadocTag(text.substring(0, pos),
                             new TextLocation(TextLocation.UNDEFINED, line, start.column), 
                             new TextLocation(TextLocation.UNDEFINED, line, pos - 1 + start.column));

        LineMapping lines = new LineMapping(text, start.line, start.column);

        // skip non text
        while (pos < len && (Character.isWhitespace(text.charAt(pos)) || text.charAt(pos) == '*')) {
            ++pos;
        }

        if (pos < len) {
            // target types:
            final int HTML = 0;
            final int QUOTED = 1;
            final int WORD = 2;

            int targetStart = pos;
                
            int type;
            if (pos + 2 < len && text.substring(pos, pos + 2).equalsIgnoreCase("<a")) {
                type = HTML;
            }
            else if (text.charAt(pos) == '"') {
                type = QUOTED;
            }
            else {
                type = WORD;
            }

            // Also handle targets with balanced parentheses, for example:
            //     @see set(int, double, java.net.Socket)
            // These can't be nested.

            boolean inParen = false;

            while (pos < len) {
                char ch = text.charAt(pos);
                if (ch == '\\' && pos + 1 < len) {
                    ++pos;
                }
                else if (type == WORD) {
                    if (ch == '(') {
                        inParen = true;
                    }
                    else if (inParen && ch == ')') {
                        inParen = false;
                    }
                    if (!inParen) {
                        if (pos + 1 == len) {
                            // we'll never get a space, because we're at the end
                            ++pos;
                            break;
                        }
                        else if (Character.isWhitespace(ch)) {
                            // we have a space between the target and the next word
                            break;
                        }
                    }
                }
                else if (type == HTML && ch == '>' && Character.toLowerCase(text.charAt(pos - 1)) == 'a') {
                    // HTML target
                    ++pos;
                    break;
                }
                else if (type == QUOTED && ch == '"') {
                    // quoted target
                    ++pos;
                    break;
                }
                ++pos;
            }

            // even unbalanced HTML or double-quoted strings will get a target:

            TextRange tgtRange = lines.getLocations(targetStart, pos - 1);

            target = new JavadocElement(text.substring(targetStart, pos), tgtRange.getStart(), tgtRange.getEnd());

            // skip non text
            while (pos < len && (Character.isWhitespace(text.charAt(pos)) || text.charAt(pos) == '*')) {
                ++pos;
            }

            if (pos == len) {
                // no description beyond target
                descriptionNonTarget = null;
                description = new JavadocElement(text.substring(targetStart, len), tgtRange.getStart(), end);
            }
            else if (pos < len && !Character.isWhitespace(text.charAt(pos))) {
                Location dntStart = lines.getLocation(pos);
                descriptionNonTarget = new JavadocElement(text.substring(pos, len), dntStart, end);
                description = new JavadocElement(text.substring(targetStart, len), tgtRange.getStart(), end);
            }
        }

        return new JavadocTaggedNode(text, start, end, tag, target, description, descriptionNonTarget);
    }

}
