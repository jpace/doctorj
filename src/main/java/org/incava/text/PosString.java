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
    protected boolean hasMore() {
        return this.pos < this.len;
    }
    
    /**
     * Returns whether the current position is at least <code>num</code>
     * characters from the end of the string.
     */
    protected boolean hasNumMore(int num) {
        return this.pos + num < this.len;
    }

    /**
     * Advances from one substring to another.
     */
    public void advanceFromTo(String from, String to) {
        if (advancePast(from)) {
            advanceThrough(to);
        }
    }

    /**
     * If the current substring matches <code>what</code>, the position is
     * advanced through <code>what</code> and true is returned. Otherwise, this
     * returns false.
     */
    public boolean advancePast(String what) {
        if (this.pos + what.length() < this.len && this.str.substring(this.pos).startsWith(what)) {
            this.pos += what.length();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Advances this position through <code>what</code>.
     */
    protected void advanceThrough(String what) {
        int len = this.str.length();
        while (this.pos < len && this.pos + what.length() < len && !this.str.substring(this.pos).startsWith(what)) {
            ++this.pos;
        }
    }

    /**
     * Advances until whitespace or a letter or digit is found.
     */
    protected void advanceToWord() {
        while (hasMore() && !Character.isWhitespace(currentChar()) && !Character.isLetterOrDigit(currentChar())) {
            ++this.pos;
        }
    }

    /**
     * Advances until whitespace is found.
     */
    protected void advanceToWhitespace() {
        while (hasMore() && !Character.isWhitespace(currentChar())) {
            ++this.pos;
        }
    }
}
