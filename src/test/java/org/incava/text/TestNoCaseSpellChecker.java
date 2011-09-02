package org.incava.text;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import org.incava.ijdk.lang.Pair;
import org.incava.ijdk.util.MultiMap;


public class TestNoCaseSpellChecker extends AbstractTestSpellChecker {

    public TestNoCaseSpellChecker(String name) {
        super(name);
    }

    public SpellChecker createSpellChecker() {
        return new NoCaseSpellChecker();
    }

    public Collection<Pair<String, String>> getSameWords() {
        List<Pair<String, String>> sameWords = new ArrayList<Pair<String, String>>();

        sameWords.add(new Pair<String, String>("this", "this"));
        sameWords.add(new Pair<String, String>("THIS", "THIS"));
        sameWords.add(new Pair<String, String>("repository", "repository"));

        sameWords.add(new Pair<String, String>("thIs", "This"));
        sameWords.add(new Pair<String, String>("this", "THIS"));
        sameWords.add(new Pair<String, String>("repoSITORY", "REPOsitory"));

        return sameWords;
    }

    public MultiMap<Integer, Pair<Pair<String, String>, Integer>> getDifferentWords() {
        MultiMap<Integer, Pair<Pair<String, String>, Integer>> diffWords = new MultiMap<Integer, Pair<Pair<String, String>, Integer>>();

        // additions
        addWord(diffWords, 1, "the",   "THEY");
        addWord(diffWords, 2, "The",   "their");
        addWord(diffWords, 3, "thEy",  "THEY're");
        addWord(diffWords, 4, "THE",   "theaTre", 5);
        addWord(diffWords, 4, "the",   "THEAter", 5);

        // deletions
        addWord(diffWords, 1, "thEy",    "tHe");
        addWord(diffWords, 2, "thEIR",   "The");
        addWord(diffWords, 3, "ThEy'Re", "ThEy");
        addWord(diffWords, 4, "tHeaTre", "tHe", 5);
        addWord(diffWords, 4, "thEatEr", "ThE", 5);
        
        // changes
        addWord(diffWords, 2, "tHeaTER", "theAtre");
        addWord(diffWords, 2, "cenTER",  "cEntre");
        addWord(diffWords, 2, "reAlize", "reALISE");
        addWord(diffWords, 4, "rEaLiZE", "Reality", 5);

        // miscellaneous
        addWord(diffWords, 1, "Here",  "There");
        addWord(diffWords, 5, "hIt",   "miSS",   5);
        addWord(diffWords, 6, "up",    "dOWn",   6);
        addWord(diffWords, 7, "fEast", "fAMINE", 7);

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

        noDictWords.add("eujiffEROUS"); // alas

        return noDictWords;
    }
    
}
