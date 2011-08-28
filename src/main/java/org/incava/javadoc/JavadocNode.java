package org.incava.javadoc;

import java.awt.Point;
import java.io.*;
import java.util.*;
import org.incava.text.LineMapping;
import org.incava.text.Location;


/**
 * Represents a Javadoc element.
 */
public class JavadocNode
{
    private JavadocDescriptionNode description = null;

    private JavadocTaggedNode[] tagged = new JavadocTaggedNode[0];

    private int startLine;

    private int startColumn;

    private int endLine;

    private int endColumn;

    /**
     * Parses itself from the given text.
     */
    public static JavadocNode parse(String text, int startLine, int startColumn)
    {
        JavadocParser parser = new JavadocParser();
        List subs = parser.parse(text, startLine, startColumn);

        if (subs == null) {
            return null;
        }
        else {
            // store line positions, for converting string positions (which are
            // 0-based) to line:column (which are 1-based)

            LineMapping lines = new LineMapping(text, startLine, startColumn);
            JavadocNode jd    = new JavadocNode();

            jd.startLine = startLine;
            jd.startColumn = startColumn;

            Location end = lines.getLocation(text.length() - 1);
            jd.endLine   = end.line;
            jd.endColumn = end.column;

            if (subs.size() > 0) {
                Iterator it = subs.iterator();

                Point descPos = (Point)it.next();
                if (descPos != null) {
                    Location[] descLocations = lines.getLocations(descPos);

                    // we could trim whitespace, so that the following descriptions are equivalent:

                    // /** \n
                    //   * This is a test. \n
                    //   */

                    // /** \n
                    //   * This is a test. \n
                    //   * @tag something
                    //   */

                    jd.description = new JavadocDescriptionNode(text.substring(descPos.x, descPos.y), descLocations[0], descLocations[1]);
                }

                jd.tagged = new JavadocTaggedNode[subs.size() - 1];
                for (int i = 0; it.hasNext(); ++i) {
                    Point      pos       = (Point)it.next();
                    Location[] locations = lines.getLocations(pos);
                    
                    jd.tagged[i] = new JavadocTaggedNode(text.substring(pos.x, pos.y), locations[0], locations[1]);
                }
            }

            return jd;
        }
    }

    public JavadocDescriptionNode getDescription()
    {
        return description;
    }

    public JavadocTaggedNode[] getTaggedComments()
    {
        return tagged;
    }

    public int getStartLine()
    {
        return startLine;
    }

    public int getStartColumn()
    {
        return startColumn;
    }

    public int getEndLine()
    {
        return endLine;
    }

    public int getEndColumn()
    {
        return endColumn;
    }

}
