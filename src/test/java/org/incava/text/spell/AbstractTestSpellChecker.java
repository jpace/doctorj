package org.incava.text.spell;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import org.incava.ijdk.lang.Pair;
import org.incava.ijdk.util.MultiMap;


public abstract class AbstractTestSpellChecker extends TestCase {

    public AbstractTestSpellChecker(String name) {
        super(name);
    }

    public abstract SpellChecker createSpellChecker();

    public void testSame() {
        SpellChecker sc = createSpellChecker();

        Collection<Pair<String, String>> sameWords = getSameWords();

        for (Pair<String, String> sameWord : sameWords) {
            assertEditDistance(sc, 0, sameWord.getFirst(), sameWord.getSecond(), null);
        }
    }

    public abstract Collection<Pair<String, String>> getSameWords();

    public abstract MultiMap<Integer, Pair<Pair<String, String>, Integer>> getDifferentWords();

    public void assertEditDistance(SpellChecker sc, int expDist, String from, String to, Integer max) {
        String msg = "from: " + from + "; to: " + to + "; max: " + max;

        // tr.Ace.yellow("msg", msg);
        int actDist = max == null ? Spelling.getEditDistance(from, to) : Spelling.getEditDistance(from, to, max);
        assertEquals(msg, expDist, actDist);
    }
    
    public void testDifferent() {
        SpellChecker sc = createSpellChecker();

        MultiMap<Integer, Pair<Pair<String, String>, Integer>> diffs = getDifferentWords();

        for (Map.Entry<Integer, Collection<Pair<Pair<String, String>, Integer>>> diff : diffs.entrySet()) {
            Integer expDist = diff.getKey();

            for (Pair<Pair<String, String>, Integer> fromToMax : diff.getValue()) {

                String from = fromToMax.getFirst().getFirst();
                String to   = fromToMax.getFirst().getSecond();
                Integer max = fromToMax.getSecond();

                assertEditDistance(sc, expDist, from, to, max);
            }
        }
    }

    public abstract Collection<String> getDictionaryWords();

    public abstract Collection<String> getNotInDictionaryWords();

    public void addWord(MultiMap<Integer, Pair<Pair<String, String>, Integer>> map, Integer dist, String from, String to, Integer max) {
        Pair<Pair<String, String>, Integer> val = new Pair<Pair<String, String>, Integer>(new Pair<String, String>(from, to), max);
        map.put(dist, val);
    }

    public void addWord(MultiMap<Integer, Pair<Pair<String, String>, Integer>> map, Integer dist, String from, String to) {
        addWord(map, dist, from, to, null);
    }

    public void testDictionary() {
        SpellChecker sc = new SpellChecker(SpellChecker.CaseType.CASE_SENSITIVE);
        sc.addDictionary("/home/jpace/proj/doctorj/etc/words.en_US");

        for (String word : getDictionaryWords()) {
            assertTrue("word in dictionary: " + word, sc.hasWord(word));
        }

        for (String word : getNotInDictionaryWords()) {
            assertFalse("word not in dictionary: " + word, sc.hasWord(word));
        }

        MultiMap<Integer, String> nearMatches = new MultiMap<Integer, String>();
        boolean isOK = sc.isCorrect("badd", nearMatches);
        // tr.Ace.log("isOK: " + isOK);
        // tr.Ace.log("nearMatches", nearMatches);
    }
    
}
