package org.incava.ijdk.lang;


public class ObjectExt {

    /**
     * Returns whether the objects are equal, including whether they are both
     * null.
     */
    public static boolean areEqual(Object a, Object b) {
        return a == null ? b == null : b == null ? false : a.equals(b);
    }
    
}
