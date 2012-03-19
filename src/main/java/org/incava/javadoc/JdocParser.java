package org.incava.javadoc;

import java.util.*;
import org.incava.ijdk.text.TextLocation;
import static org.incava.ijdk.util.IUtil.*;

/**
 * Parses Javadoc comments. This is in development, and not yet used.
 */
public class JdocParser {
    // protected final static List<Character> WSCHARS = Arrays.asList(new Character[] { '\r', "\n", "\t", "\f", " " };

    private String str;

    private TextLocation location;

    private TextLocation lastNonCommentWhitespace;

    private boolean atBeginningOfLine;

    private int length;

    public JdocParser() {
        this.str = null;
        this.location = null;
        this.lastNonCommentWhitespace = null;
        this.atBeginningOfLine = true;
        this.length = 0;
    }

    public String getString() {
        return this.str;
    }

    public TextLocation getLocation() {
        return this.location;
    }

    protected void updateLocation(int posOffset, int lnumOffset, int colOffset) {
        this.location = new TextLocation(this.location.getPosition() + posOffset, this.location.getLine() + lnumOffset, this.location.getColumn() + colOffset);
    }

    protected void setAtBeginningOfLine() {
        this.location = new TextLocation(this.location.getPosition(), this.location.getLine() + 1, 1);
        this.atBeginningOfLine = true;
    }

    protected int getPosition() {
        return location.getPosition();
    }

    protected boolean atEndOfLine() {
        return currentCharIs('\n') || nextCharIs('\r');
    }

    protected Character getChar(int offset) {
        int pos = getPosition() + offset;
        return pos < this.length ? this.str.charAt(pos) : null;
    }

    protected Character currentChar() {
        return getChar(0);
    }

    protected boolean charIs(int offset, char ch) {
        Character curr = getChar(offset);
        return curr != null && curr == ch;
    }

    protected boolean currentCharIs(char ch) {
        return charIs(0, ch);
    }

    protected boolean nextCharIs(char ch) {
        return charIs(1, ch);
    }

    public boolean isCurrentCharWhitespace() {
        Character curr = currentChar();
        return curr != null && Character.isWhitespace(curr);
    }

    public boolean isCurrentCharCommentOrWhitespace() {
        return isCurrentCharWhitespace() || (this.atBeginningOfLine && currentCharIs('*'));
    }

    protected boolean hasMore() {
        return getPosition() < this.length;
    }

    protected boolean atCommentEnd() {
        return currentCharIs('*') && nextCharIs('/');
    }

    protected void gotoNextChar() {
        if (currentCharIs('\n')) {
            if (nextCharIs('\r')) {
                updateLocation(1, 0, 0);
            }

            setAtBeginningOfLine();
        }
        else if (currentCharIs('\n')) {
            setAtBeginningOfLine();
        }
        else {
            if (!currentCharIs(' ') && !(this.atBeginningOfLine && currentCharIs('*'))) {
                lastNonCommentWhitespace = this.location;
            }
            
            // to next column
            updateLocation(0, 0, 1);
        }

        updateLocation(1, 0, 0);
    }

    protected boolean gotoJdocStart() {
        while (hasMore()) {
            while (isCurrentCharWhitespace()) {
                gotoNextChar();
            }
            
            // this can be simplified ...

            if (currentCharIs('/')) {
                gotoNextChar();

                if (currentCharIs('*')) {
                    gotoNextChar();

                    if (currentCharIs('*')) {
                        gotoNextChar();

                        if (currentCharIs('/')) {
                            // end of comment
                            break;
                        }
                        else {
                            while (isCurrentCharWhitespace()) {
                                gotoNextChar();
                            }
                        }
                        return true;
                    }
                    else {
                        break;
                    }
                }
                else {
                    break;
                }
            }
        }
        
        return false;
    }

    protected JdocElement getDescription() {
        TextLocation descStart = null;
        TextLocation descEnd = null;
        
        while (hasMore() && !atCommentEnd()) {
            if (isCurrentCharCommentOrWhitespace()) {
                gotoNextChar();
            }
            else if (currentCharIs('@')) {
                break;
            }
            else {
                descEnd = this.location;

                // should be
                descStart = elvis(descStart, this.location);

                gotoNextChar();
            }
        }

        if (descStart != null) {
            return new JdocElement(this.str, descStart, descEnd);
        }
        else {
            return null;
        }
    }

    protected List<JdocTaggedNode> getTaggedComments() {
        List<JdocTaggedNode> taggedNodes = new ArrayList<JdocTaggedNode>();

        while (hasMore() && !atCommentEnd()) {
            if (currentCharIs('@')) {
                JdocTaggedNode jtn = getTaggedComment();
                taggedNodes.add(jtn);
            }
            else {
                gotoNextChar();
            }
        }

        return taggedNodes;
    }

    protected JdocTaggedNode getTaggedComment() {
        // we assume we're at a @

        TextLocation tagStart = this.location;
        TextLocation tagEnd = this.location;

        while (hasMore() && !isCurrentCharCommentOrWhitespace()) {
            tagEnd = this.location;
            gotoNextChar();
        }

        JdocElement tag = new JdocElement(this.str, tagStart, tagEnd);
        
        while (hasMore() && !atCommentEnd() && isCurrentCharCommentOrWhitespace()) {
            gotoNextChar();
        }
        
        JdocElement tgt = atCommentEnd() ? null : getTarget();

        JdocElement desc = null;
        JdocElement fullDesc = null;

        if (isTrue(tgt)) {
            if (!atCommentEnd()) {
                while (isCurrentCharWhitespace()) {
                    gotoNextChar();
                }
            }
        
            TextLocation descStart = null;            
        
            // now, to end of line or end of comment.
            while (hasMore() && !atCommentEnd() && !atEndOfLine()) {
                descStart = elvis(descStart, this.location);
                gotoNextChar();
            }

            if (isTrue(descStart) && descStart.getPosition() < lastNonCommentWhitespace.getPosition()) {
                desc = new JdocElement(this.str, descStart, lastNonCommentWhitespace);
            }
        
            fullDesc = new JdocElement(this.str, tgt.getStartLocation(), lastNonCommentWhitespace);
        }

        return new JdocTaggedNode(tag, tgt, desc, fullDesc);
    }

    protected JdocElement getTarget() {
        return new JdocTargetParser(this).parse();
    }

    public JdocComment parse(String str) {
        this.location = new TextLocation(0, 1, 1);

        this.str = str;

        this.length = this.str.length();

        TextLocation jdocNodeStart = null;
        TextLocation jdocNodeEnd = null;

        JdocElement jdocDesc = null;

        List<JdocTaggedNode> jdocTaggedCmts = new ArrayList<JdocTaggedNode>();

        if (gotoJdocStart()) {
            jdocNodeStart  = this.location;
            jdocDesc       = getDescription();
            jdocTaggedCmts = getTaggedComments();
            jdocNodeEnd    = lastNonCommentWhitespace;
        }
        else {
            return null;
        }    

        return new JdocComment(this.str, jdocNodeStart, jdocNodeEnd, jdocDesc, jdocTaggedCmts);
    }
}
