package org.incava.javadoc;

import java.util.*;
import org.incava.text.TextLocation;


/**
 * A complete Javadoc comment.
 */
public class JdocComment extends JdocElement {

    private final JdocElement description;

    private final List<JdocTaggedNode> taggedNodes;

    public JdocComment(String comment, TextLocation startLoc, TextLocation endLoc, JdocElement description, List<JdocTaggedNode> taggedNodes) {
        super(comment, startLoc, endLoc);

        this.description = description;
        this.taggedNodes = taggedNodes;
    }

    public JdocElement getDescription() {
        return this.description;
    }

    public List<JdocTaggedNode> getTaggedNodes() {
        return this.taggedNodes;
    }

}
