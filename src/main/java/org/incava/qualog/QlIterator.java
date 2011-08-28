package org.incava.qualog;

import java.io.*;
import java.util.*;


/**
 * Wraps Iterators for output.
 */
public class QlIterator
{
    public static boolean stack(QlLevel level, 
                                ANSIColor[] msgColors,
                                String name,
                                Iterator it,
                                ANSIColor fileColor,
                                ANSIColor classColor,
                                ANSIColor methodColor,
                                int numFrames)
    {
        Collection ary = new ArrayList();
        while (it.hasNext()) {
            ary.add(it.next());
        }

        return QlCollection.stack(level, msgColors, name, ary, fileColor, classColor, methodColor, numFrames);
    }
}

