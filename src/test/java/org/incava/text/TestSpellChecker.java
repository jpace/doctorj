package org.incava.text;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;


public class TestSpellChecker extends TestCase
{
    public TestSpellChecker(String name)
    {
        super(name);
    }

    public void testSame()
    {
        SpellChecker sc = new SpellChecker();
        assertEquals(0, sc.editDistance("this", "this"));
        assertEquals(0, sc.editDistance("THIS", "THIS"));
        assertEquals(0, sc.editDistance("repository", "repository"));
    }
    
    public void testDifferent()
    {
        SpellChecker sc = new SpellChecker();

        // additions
        assertEquals(1, sc.editDistance("the",   "they"));
        assertEquals(2, sc.editDistance("the",   "their"));
        assertEquals(3, sc.editDistance("they",  "they're"));
        assertEquals(4, sc.editDistance("the",   "theatre", 5));
        assertEquals(4, sc.editDistance("the",   "theater", 5));

        // deletions
        assertEquals(1, sc.editDistance("they",    "the"));
        assertEquals(2, sc.editDistance("their",   "the"));
        assertEquals(3, sc.editDistance("they're", "they"));
        assertEquals(4, sc.editDistance("theatre", "the", 5));
        assertEquals(4, sc.editDistance("theater", "the", 5));
        
        // changes
        assertEquals(2, sc.editDistance("theater", "theatre"));
        assertEquals(2, sc.editDistance("center",  "centre"));
        assertEquals(2, sc.editDistance("realize", "realise"));
        assertEquals(4, sc.editDistance("realize", "reality", 5));

        // miscellaneous
        assertEquals(1, sc.editDistance("here",  "there"));
        assertEquals(5, sc.editDistance("hit",   "miss",   5));
        assertEquals(6, sc.editDistance("up",    "down",   6));
        assertEquals(7, sc.editDistance("feast", "famine", 7));
    }

    public void testDictionary()
    {
        SpellChecker sc = new SpellChecker();
        sc.addDictionary("/home/jpace/proj/doctorj/etc/words.en_US");
        
        assertTrue(sc.hasWord("locate"));
        assertTrue(sc.hasWord("logarithm"));
        assertFalse(sc.hasWord("eujifferous")); // alas.

        Map nearMatches = new TreeMap();
        boolean isOK = sc.isCorrect("badd", nearMatches);
        tr.Ace.log("isOK: " + isOK);
        tr.Ace.log("nearMatches", nearMatches);
    }
    
}
