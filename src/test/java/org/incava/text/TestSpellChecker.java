package org.incava.text;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import org.incava.ijdk.lang.Pair;
import org.incava.ijdk.util.MultiMap;


public class TestSpellChecker extends AbstractTestSpellChecker {

    public TestSpellChecker(String name) {
        super(name);

        tr.Ace.setVerbose(true);
    }

    public SpellChecker createSpellChecker() {
        return new SpellChecker();
    }

    public Collection<Pair<String, String>> getSameWords() {
        List<Pair<String, String>> sameWords = new ArrayList<Pair<String, String>>();

        sameWords.add(new Pair<String, String>("this", "this"));
        sameWords.add(new Pair<String, String>("THIS", "THIS"));
        sameWords.add(new Pair<String, String>("repository", "repository"));

        return sameWords;
    }

    public MultiMap<Integer, Pair<Pair<String, String>, Integer>> getDifferentWords() {
        MultiMap<Integer, Pair<Pair<String, String>, Integer>> diffWords = new MultiMap<Integer, Pair<Pair<String, String>, Integer>>();

        // additions
        addWord(diffWords, 1, "the", "they");
        addWord(diffWords, 2, "the", "their");
        addWord(diffWords, 3, "they", "they're");
        addWord(diffWords, 4, "the",   "theater", 5);

        // deletions
        addWord(diffWords, 1, "they", "the");
        addWord(diffWords, 2, "their", "the");
        addWord(diffWords, 3, "they're", "they");
        addWord(diffWords, 4, "theatre", "the", 5);
        addWord(diffWords, 4, "theater", "the", 5);
        
        // changes
        addWord(diffWords, 2, "theater", "theatre");
        addWord(diffWords, 2, "center", "centre");
        addWord(diffWords, 2, "realize", "realise");
        addWord(diffWords, 4, "realize", "reality", 5);

        // miscellaneous
        addWord(diffWords, 1, "here", "there");
        addWord(diffWords, 5, "hit",   "miss",   5);
        addWord(diffWords, 6, "up",    "down",   6);
        addWord(diffWords, 7, "feast", "famine", 7);
     
        return diffWords;
    }

    public Collection<String> getDictionaryWords() {
        List<String> dictWords = new ArrayList<String>();

        dictWords.add("locate");
        dictWords.add("logarithm");

        return dictWords;
    }

    public Collection<String> getNotInDictionaryWords() {
        List<String> noDictWords = new ArrayList<String>();

        noDictWords.add("eujifferous"); // alas

        return noDictWords;
    }
    
}
