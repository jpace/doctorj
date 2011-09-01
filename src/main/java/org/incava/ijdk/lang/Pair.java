package org.incava.ijdk.lang;


public class Pair<FirstType, SecondType> implements Comparable<Pair<FirstType, SecondType>> {

    public static <X, Y> Pair<X, Y> create(X first, Y second) {
        return new Pair<X, Y>(first, second);
    }

    public static <X extends Comparable<? super X>, Y extends Comparable<? super Y>> Pair<X, Y> create(X first, Y second) {
        return new Pair<X, Y>(first, second);
    }

    private final FirstType first;
    
    private final SecondType second;
    
    public Pair(FirstType first, SecondType second) {
        this.first  = first;
        this.second = second;
    }

    public FirstType getFirst() {
        return first;
    }

    public SecondType getSecond() {
        return second;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            Pair other = (Pair)obj;
            return ObjectExt.areEqual(getFirst(), other.getFirst()) && ObjectExt.areEqual(getSecond(), other.getSecond());
        }
        else {
            return false;
        }
    }

    public int compareTo(Pair<FirstType, SecondType> other) {
        int cmp = compare(getFirst(), other.getFirst());

        if (cmp == 0) {
            cmp = compare(getSecond(), other.getSecond());
        }

        return cmp;
    }

    @SuppressWarnings("unchecked")
    public <Type> int compare(Type a, Type b) {
        if (a == null) {
            return b == null ? 0 : 1;
        }
        else if (b == null) {
            return -1;
        }
        else if (a instanceof Comparable) {
            return ((Comparable)a).compareTo((Comparable)b);
        }
        else {
            return a.equals(b) ? 0 : -1;
        }
    }

    public String toString() {
        String firstStr  = getFirst()  == null ? null : getFirst().toString();
        String secondStr = getSecond() == null ? null : getSecond().toString();
        return firstStr + ", " + secondStr;
    }

    public int hashCode() {
        int firstHc  = getFirst()  == null ? 0 : getFirst().hashCode();
        int secondHc = getSecond() == null ? 0 : getSecond().hashCode();
        return firstHc * 31 + secondHc;
    }

}
