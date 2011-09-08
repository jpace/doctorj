package org.incava.javadoc;

import java.io.*;
import java.util.*;
import org.incava.text.TextLocation;


/**
 * Represents a Javadoc element.
 */
public class JavadocNode {

    private final JavadocDescriptionNode description;

    private final JavadocTaggedNode[] tagged;

    private final TextLocation start;

    private final TextLocation end;

    public JavadocNode(JavadocDescriptionNode description, 
                       JavadocTaggedNode[] tagged, 
                       TextLocation start,
                       TextLocation end) {
        this.description = description;
        this.tagged = tagged;
        this.start = start;
        this.end = end;
    }

    public JavadocDescriptionNode getDescription() {
        return description;
    }

    public JavadocTaggedNode[] getTaggedComments() {
        return tagged;
    }

    public TextLocation getStart() {
        return start;
    }

    public TextLocation getEnd() {
        return end;
    }

    public int getStartLine() {
        return start.getLine();
    }

    public int getStartColumn() {
        return start.getColumn();
    }

    public int getEndLine() {
        return end.getLine();
    }

    public int getEndColumn() {
        return end.getColumn();
    }

}
