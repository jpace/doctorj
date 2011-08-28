package org.incava.util;

import java.util.Comparator;


/**
 * A comparator for reversed order.
 */
public class ReverseComparator implements Comparator
{
    /**
     * Compares o2 to o1. <code>o2</code> must implement Comparable.
     */
    public int compare(Object o1, Object o2)
    {
        if (o2 instanceof Comparable) {
            Comparable c2 = (Comparable)o2;
            return c2.compareTo(o1);
        }
        else {
            throw new IllegalArgumentException("argument " + o2.getClass() + " does not implement Comparable");
        }
    }
    
    /**
     * Returns <code>o1.equals(o2)</code>.
     */
    public boolean equals(Object o1, Object o2) 
    {
        return o1.equals(o2);
    }
}
