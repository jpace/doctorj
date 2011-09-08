package org.incava.javadoc;

import java.io.*;
import java.util.*;
import org.incava.text.*;


/**
 * A tagged element, such as:
 * @since 0.1
 */
public class JavadocTaggedNode extends JavadocElement {
    
    private boolean parsed = false;

    private JavadocTag tag = null;

    private JavadocElement target = null;

    private JavadocElement description = null;

    private JavadocElement descriptionNonTarget = null;
    
    public JavadocTaggedNode(String text, Location start, Location end) {
        super(text, start, end);
    }

    public JavadocTag getTag() {
        parse();
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
        parse();
        return target;
    }

    /**
     * This returns the text following the tag, and including the target.
     */
    public JavadocElement getDescription() {
        parse();
        return description;
    }

    /**
     * This returns the text following the target.
     */
    public JavadocElement getDescriptionNonTarget() {
        parse();
        return descriptionNonTarget;
    }

    protected void parse() {
        if (!parsed) {
            int pos = 0;
            int line = start.line;
            int col = start.column;
            int len = text.length();

            // has to be a tag first
            while (pos < len && !Character.isWhitespace(text.charAt(pos))) {
                ++pos;
            }

            // tr.Ace.log("after tag, pos: " + pos);
            // tr.Ace.log("tag line : " + line);
            // tr.Ace.log("start col: " + start.column);
            // tr.Ace.log("end col  : " + (pos - 1 + start.column));

            tag = new JavadocTag(text.substring(0, pos),
                                 new TextLocation(TextLocation.UNDEFINED, line, start.column), 
                                 new TextLocation(TextLocation.UNDEFINED, line, pos - 1 + start.column));

            // tr.Ace.log("created tag '" + tag.text + "'");

            LineMapping lines = new LineMapping(text, start.line, start.column);

            // skip non text
            while (pos < len && (Character.isWhitespace(text.charAt(pos)) || text.charAt(pos) == '*')) {
                ++pos;
            }

            // tr.Ace.log("position: " + pos);
            // tr.Ace.log("current char: " + text.charAt(pos));

            if (pos < len) {
                // tr.Ace.log("parsing target ...");

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

                // tr.Ace.log("target type: " + type);

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

                Location[] targetLocations = lines.getLocations(targetStart, pos - 1);

                // tr.Ace.log("creating target ...");
                target = new JavadocElement(text.substring(targetStart, pos), targetLocations[0], targetLocations[1]);
                // tr.Ace.log("target: " + target);

                // skip non text
                while (pos < len && (Character.isWhitespace(text.charAt(pos)) || text.charAt(pos) == '*')) {
                    ++pos;
                }

                if (pos == len) {
                    // no description beyond target
                    // tr.Ace.log("no description beyond target");
                    descriptionNonTarget = null;
                    description = new JavadocElement(text.substring(targetStart, len), targetLocations[0], end);
                }
                else if (pos < len && !Character.isWhitespace(text.charAt(pos))) {
                    // tr.Ace.log("creating description non-target");
                    Location dntStart = lines.getLocation(pos);
                    descriptionNonTarget = new JavadocElement(text.substring(pos, len), dntStart, end);
                    // tr.Ace.log("created description non-target: " + descriptionNonTarget);
                    description = new JavadocElement(text.substring(targetStart, len), targetLocations[0], end);
                }
            }
        }
    }

}
