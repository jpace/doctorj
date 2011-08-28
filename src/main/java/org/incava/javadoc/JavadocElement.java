package org.incava.javadoc;

import org.incava.text.Location;


/**
 * A field within a Javadoc comment.
 */
public class JavadocElement
{
    public String text;

    public Location start;

    public Location end;

    public JavadocElement(String text, Location start, Location end)
    {
        this.text = text;
        this.start = start;
        this.end = end;

        // tr.Ace.log("created: " + this);
    }

    public boolean equals(Object obj)
    {
        return obj instanceof JavadocElement && equals((JavadocElement)obj);
    }

    public boolean equals(JavadocElement other)
    {
        return (other.text.equals(text) && 
                other.start.equals(start) && 
                other.end.equals(end));
    }

    public String toString()
    {
        return "[" + start + " .. " + end + "]: '" + text + "'";
    }
}
