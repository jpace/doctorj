package org.incava.text;

/**
 * A processable/iterable string with a position.
 */
public class PString {
    /**
     * The string.
     */
    public final String str;

    /**
     * The length of the string.
     */
    public final int length;

    /**
     * The current position within the string.
     */
    public int position;

    public PString(String str, int position) {
        this.str = str;
        this.position = position;
        this.length = str.length();
    }

    public PString(String str) {
        this(str, 0);
    }

    public String toString() {
        return "'" + this.str + "' (" + this.position + ")";
    }

    /**
     * Returns a substring from the current position, of <code>num</code> characters.
     */
    public String substring(int num) {
        return this.str.substring(this.position, this.position + num);
    }

    /**
     * Returns the current position.
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * Sets the current position.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Advances the position <code>num</code> characters.
     */
    public void advancePosition(int num) {
        this.position += num;
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
        return this.position < this.length ? this.str.charAt(this.position) : null;
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
        return this.position + num < this.length;
    }

    /**
     * Returns whether the substring at the current position matches
     * <code>what</code>.
     */
    public boolean isMatch(String what) {
        return hasNumMore(what.length() - 1) && this.str.substring(this.position).startsWith(what);
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
     * Advances from the first string the second one, if there is a current
     * match.
     *
     * @see #isMatch
     */
    public void advanceFromTo(String from, String to) {
        if (advanceFrom(from)) {
            advanceTo(to);
        }
    }

    /**
     * Advances until whitespace or a letter or digit is found.
     */
    public void advanceToWord() {
        while (hasChar() && !Character.isWhitespace(currentChar()) && !Character.isLetterOrDigit(currentChar())) {
            ++this.position;
        }
    }

    /**
     * Advances until whitespace is found.
     */
    protected void advanceToWhitespace() {
        while (hasChar() && !Character.isWhitespace(currentChar())) {
            ++this.position;
        }
    }
}
