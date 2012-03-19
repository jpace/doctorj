package org.incava.javadoc;

import org.incava.ijdk.text.Location;
import org.incava.text.LineMapping;

/**
 * A tagged element, such as:
 * @since 0.1
 */
public class JavadocTaggedNode extends JavadocElement {
    private final JavadocTag tag;
    private final JavadocElement target;
    private final JavadocElement description;
    private final JavadocElement descriptionNonTarget;
    
    public JavadocTaggedNode(String text, Location start, Location end,
                             JavadocTag tag, JavadocElement target, JavadocElement description, JavadocElement descriptionNonTarget) {
        super(text, start, end);

        this.tag = tag;
        this.target = target;
        this.description = description;
        this.descriptionNonTarget = descriptionNonTarget;
    }

    public JavadocTag getTag() {
        return tag;
    }

    /**
     * Tag targets may be one of three forms:
     *
     * @html <a href="http://www.foo.org/something.html">An HTML Target</a>
     * @quoted "A Quoted Target"
     * @word Word
     */
    public JavadocElement getTarget() {
        return target;
    }

    /**
     * This returns the text following the tag, and including the target.
     */
    public JavadocElement getDescription() {
        return description;
    }

    /**
     * This returns the text following the target.
     */
    public JavadocElement getDescriptionNonTarget() {
        return descriptionNonTarget;
    }
}
