package org.incava.util;

import java.util.*;


/**
 * Collects a collections into a collection.
 */
public abstract class Collect extends ArrayList
{
    /**
     * Creates a new collection, where the condition passes the condition.
     *
     * @param c The collection from which to build the new collection.
     */
    public Collect(Collection c) 
    {
        Iterator it = c.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (where(obj)) {
                add(block(obj));
            }
        }
    }

    /**
     * Ditto, but for real arrays.
     */
    public Collect(Object[] ary) 
    {
        for (int i = 0; i < ary.length; ++i) {
            Object obj = ary[i];
            if (where(obj)) {
                add(block(obj));
            }
        }
    }

    /**
     * Must be defined to return where the given object satisfies the condition.
     *
     * @param obj An object from the collection passed to the constructor.
     */
    public abstract boolean where(Object obj);
    
    /**
     * Returns the object to add to the collection.
     *
     * @param obj An object from the collection passed to the constructor.
     */
    public Object block(Object obj) 
    {
        return obj;
    }
}
