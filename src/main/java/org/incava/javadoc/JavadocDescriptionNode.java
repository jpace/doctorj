package org.incava.javadoc;

import org.incava.text.Location;


/**
 * The description section of a Javadoc comment.
 */
public class JavadocDescriptionNode extends JavadocElement {
    
    public final String description;

    public JavadocDescriptionNode(String description, Location start, Location end) {
        super(description, start, end);
        this.description = description;
    }

}
