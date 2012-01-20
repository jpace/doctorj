package org.incava.text;

/**
 * A poorly-named string with a position. For processing through it.
 */
public class PosString {
    /**
     * The string.
     */
    public final String str;

    /**
     * The length of the string.
     */
    public final int len;

    /**
     * The current position within the string.
     */
    public int pos;

    public PosString(String str, int pos) {
        this.str = str;
        this.pos = pos;
        this.len = str.length();
    }

    public PosString(String str) {
        this(str, 0);
    }

    public String toString() {
        return "'" + this.str + "' (" + this.pos + ")";
    }

    /**
     * Returns a substring from the current position, of <code>num</code> characters.
     */
    public String substring(int num) {
        return this.str.substring(this.pos, this.pos + num);
    }

    /**
     * Returns the current position.
     */
    public int getPosition() {
        return this.pos;
    }

    /**
     * Sets the current position.
     */
    public void setPosition(int pos) {
        this.pos = pos;
    }

    /**
     * Advances the position <code>num</code> characters.
     */
    public void advancePosition(int num) {
        this.pos += num;
    }

    /**
     * Advances the position one character.
     */
    public void advancePosition() {
        advancePosition(1);
    }    

    /**
     * Returns the character at the current position.
     */
    public Character currentChar() {
        return this.pos < this.len ? this.str.charAt(this.pos) : null;
    }

    /**
     * Returns whether the current position is not at the end of the string.
     */
    public boolean hasChar() {
        return hasNumMore(0);
    }
    
    /**
     * Returns whether the current position is at least <code>num</code>
     * characters from the end of the string.
     */
    public boolean hasNumMore(int num) {
        return this.pos + num < this.len;
    }

    /**
     * Returns whether the substring at the current position matches
     * <code>what</code>.
     */
    public boolean isMatch(String what) {
        return hasNumMore(what.length() - 1) && this.str.substring(this.pos).startsWith(what);
    }

    /**
     * Advances this position to <code>what</code>.
     */
    public void advanceTo(String what) {
        while (hasChar() && !isMatch(what)) {
            advancePosition();
        }
    }

    /**
     * If <code>what</code> is a match at the current position, the position is
     * advanced beyond the substring, and true is returned. Otherwise this
     * returns false.
     */
    public boolean advanceFrom(String what) {
        if (isMatch(what)) {
            advancePosition(what.length());
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Advances until whitespace or a letter or digit is found.
     */
    public void advanceToWord() {
        while (hasChar() && !Character.isWhitespace(currentChar()) && !Character.isLetterOrDigit(currentChar())) {
            ++this.pos;
        }
    }

    /**
     * Advances until whitespace is found.
     */
    protected void advanceToWhitespace() {
        while (hasChar() && !Character.isWhitespace(currentChar())) {
            ++this.pos;
        }
    }
}
