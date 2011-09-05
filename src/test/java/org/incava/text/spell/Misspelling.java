package org.incava.text.spell;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import org.incava.ijdk.util.MultiMap;


public class Misspelling {

    public final String word;

    public final int position;

    public final MultiMap<Integer, String> nearMatches;

    public Misspelling(String word, int position, MultiMap<Integer, String> nearMatches) {
        this.word = word;
        this.position = position;
        this.nearMatches = nearMatches;
    }

    public String toString() {
        return "[" + word + ", " + position + ", {" + nearMatches + "}";
    }

    public String getWord() {
        return this.word;
    }

    public int getPosition() {
        return this.position;
    }

    public MultiMap<Integer, String> getNearMatches() {
        return this.nearMatches;
    }
}
