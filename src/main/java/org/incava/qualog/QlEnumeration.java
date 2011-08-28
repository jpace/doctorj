package org.incava.qualog;

import java.util.*;


/**
 * Wraps Enumerations for output.
 */
public class QlEnumeration
{
    public static boolean stack(QlLevel level, 
                                ANSIColor[] msgColors,
                                String name,
                                Enumeration en,
                                ANSIColor fileColor,
                                ANSIColor classColor,
                                ANSIColor methodColor,
                                int numFrames)
    {
        Collection ary = new ArrayList();
        while (en.hasMoreElements()) {
            ary.add(en.nextElement());
        }

        return QlCollection.stack(level, msgColors, name, ary, fileColor, classColor, methodColor, numFrames);
    }
}

