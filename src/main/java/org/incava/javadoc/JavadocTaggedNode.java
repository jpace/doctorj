package org.incava.javadoc;

import java.io.*;
import java.util.*;
import org.incava.text.*;

/**
 * A tagged element, such as:
 * @since 0.1
 */
public class JavadocTaggedNode extends JavadocElement {
    public static JavadocTaggedNode create(String text, TextRange range) {
        return create(text, range.getStart(), range.getEnd());
    }

    public static JavadocTaggedNode create(String text, Location start, Location end) {
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

        // tr.Ace.log("created tag '" + tag.text + "'");

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
                // tr.Ace.log("no description beyond target");
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
    
    private final JavadocTag tag;

    private final JavadocElement target;

    private final JavadocElement description;

    private final JavadocElement descriptionNonTarget;
    
    public JavadocTaggedNode(String text, Location start, Location end,
                             JavadocTag tag, JavadocElement target, JavadocElement description, JavadocElement descriptionNonTarget) {
        super(text, start, end);

        this.tag = tag;
        this.target = target;
        this.description = description;
        this.descriptionNonTarget = descriptionNonTarget;
    }

    public JavadocTag getTag() {
        return tag;
    }

    /**
     * Tag targets may be one of three forms:
     *
     * @html <a href="http://www.foo.org/something.html">An HTML Target</a>
     * @quoted "A Quoted Target"
     * @word Word
     */
    public JavadocElement getTarget() {
        return target;
    }

    /**
     * This returns the text following the tag, and including the target.
     */
    public JavadocElement getDescription() {
        return description;
    }

    /**
     * This returns the text following the target.
     */
    public JavadocElement getDescriptionNonTarget() {
        return descriptionNonTarget;
    }
}
