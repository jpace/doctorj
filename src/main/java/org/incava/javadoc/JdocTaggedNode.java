package org.incava.javadoc;


/**
 * A field within a Javadoc comment.
 */
public class JdocTaggedNode {

    private final JdocElement tag;

    private final JdocElement target;

    private final JdocElement description;

    private final JdocElement fullDescription;
    
    public JdocTaggedNode(JdocElement tag, JdocElement target, JdocElement description, JdocElement fullDescription) {
        this.tag = tag;
        this.target = target;
        this.description = description;
        this.fullDescription = fullDescription;
    }

    public JdocElement getTag() {
        return this.tag;
    }

    public JdocElement getTarget() {
        return this.target;
    }

    public JdocElement getDescription() {
        return this.description;
    }

    public JdocElement getFullDescription() {
        return this.fullDescription;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("tag: ").append(this.tag);
        sb.append("; ");
        sb.append("target: ").append(this.target);
        sb.append("; ");
        sb.append("description: ").append(this.description);
        sb.append("; ");
        sb.append("fullDescription: ").append(this.fullDescription);

        return sb.toString();
    }
}
