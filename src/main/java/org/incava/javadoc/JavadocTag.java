package org.incava.javadoc;

import org.incava.ijdk.text.Location;

/**
 * A Javadoc tag.
 */
public class JavadocTag extends JavadocElement {
    public JavadocTag(String text, Location start, Location end) {
        super(text, start, end);
    }
}
