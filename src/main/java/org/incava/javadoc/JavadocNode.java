package org.incava.javadoc;

import java.awt.Point;
import java.io.*;
import java.util.*;
import org.incava.text.LineMapping;
import org.incava.text.Location;


/**
 * Represents a Javadoc element.
 */
public class JavadocNode {

    private JavadocDescriptionNode description;

    private JavadocTaggedNode[] tagged;

    private int startLine;

    private int startColumn;

    private int endLine;

    private int endColumn;

    public JavadocNode(JavadocDescriptionNode description, 
                       JavadocTaggedNode[] tagged, 
                       int startLine,
                       int startColumn,
                       int endLine,
                       int endColumn) {
        this.description = description;
        this.tagged = tagged;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    public JavadocDescriptionNode getDescription() {
        return description;
    }

    public JavadocTaggedNode[] getTaggedComments() {
        return tagged;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getEndColumn() {
        return endColumn;
    }

}
