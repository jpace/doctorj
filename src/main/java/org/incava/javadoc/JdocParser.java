package org.incava.javadoc;

import java.util.*;
import org.incava.text.TextLocation;


/**
 * Parses Javadoc comments.
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

    protected boolean isCurrentCharWhitespace() {
        Character curr = currentChar();
        return curr != null && Character.isWhitespace(curr);
    }

    protected boolean isCurrentCharCommentOrWhitespace() {
        return isCurrentCharWhitespace() || (this.atBeginningOfLine && currentCharIs('*'));
    }

    protected boolean hasMore() {
        return getPosition() < this.length;
    }

}
