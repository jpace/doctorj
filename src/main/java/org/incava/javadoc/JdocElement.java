package org.incava.javadoc;

import org.incava.text.TextLocation;


/**
 * A field within a Javadoc comment.
 */
public class JdocElement {

    /**
     * The javadoc comment (string) whence plunked this element be.
     */
    private final String comment;

    private final TextLocation startLoc;

    private final TextLocation endLoc;

    public JdocElement(String comment, TextLocation startLoc, TextLocation endLoc) {
        this.comment = comment;
        this.startLoc = startLoc;
        this.endLoc = endLoc;
    }

    public String getComment() {
        return this.comment;
    }

    public TextLocation getStartLocation() {
        return this.startLoc;
    }

    public TextLocation getEndLocation() {
        return this.endLoc;
    }

    public String getText() {
        return this.comment.substring(startLoc.getPosition(), endLoc.getPosition() + 1);
    }

    public String toString() {
        return "class: " + getClass() + "; start: " + startLoc + "; end: " + endLoc + "; text: " + getText();
    }
}
