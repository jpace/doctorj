package org.incava.javadoc;

import java.util.List;
import org.incava.ijdk.text.TextLocation;

/**
 * Represents a Javadoc element.
 */
public class JavadocNode {
    private final JavadocDescriptionNode description;

    private final List<JavadocTaggedNode> taggedComments;

    private final TextLocation start;

    private final TextLocation end;

    public JavadocNode(JavadocDescriptionNode description, List<JavadocTaggedNode> taggedComments, TextLocation start, TextLocation end) {
        this.description = description;
        this.taggedComments = taggedComments;
        this.start = start;
        this.end = end;
    }

    public JavadocDescriptionNode getDescription() {
        return this.description;
    }

    public List<JavadocTaggedNode> getTaggedComments() {
        return this.taggedComments;
    }

    public TextLocation getStart() {
        return this.start;
    }

    public TextLocation getEnd() {
        return this.end;
    }

    public int getStartLine() {
        return this.start.getLine();
    }

    public int getStartColumn() {
        return this.start.getColumn();
    }

    public int getEndLine() {
        return this.end.getLine();
    }

    public int getEndColumn() {
        return this.end.getColumn();
    }
}
