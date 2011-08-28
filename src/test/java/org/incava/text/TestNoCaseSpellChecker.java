package org.incava.text;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;


public class TestNoCaseSpellChecker extends TestCase
{
    public TestNoCaseSpellChecker(String name)
    {
        super(name);
    }

    public void testSame()
    {
        NoCaseSpellChecker sc = new NoCaseSpellChecker();
        assertEquals(0, sc.editDistance("this", "this"));
        assertEquals(0, sc.editDistance("THIS", "THIS"));
        assertEquals(0, sc.editDistance("repository", "repository"));
    }
    
    public void testDifferent()
    {
        NoCaseSpellChecker sc = new NoCaseSpellChecker();

        // additions
        assertEquals(1, sc.editDistance("the",   "THEY"));
        assertEquals(2, sc.editDistance("The",   "their"));
        assertEquals(3, sc.editDistance("thEy",  "THEY're"));
        assertEquals(4, sc.editDistance("THE",   "theaTre", 5));
        assertEquals(4, sc.editDistance("the",   "THEAter", 5));

        // deletions
        assertEquals(1, sc.editDistance("thEy",    "tHe"));
        assertEquals(2, sc.editDistance("thEIR",   "The"));
        assertEquals(3, sc.editDistance("ThEy'Re", "ThEy"));
        assertEquals(4, sc.editDistance("tHeaTre", "tHe", 5));
        assertEquals(4, sc.editDistance("thEatEr", "ThE", 5));
        
        // changes
        assertEquals(2, sc.editDistance("tHeaTER", "theAtre"));
        assertEquals(2, sc.editDistance("cenTER",  "cEntre"));
        assertEquals(2, sc.editDistance("reAlize", "reALISE"));
        assertEquals(4, sc.editDistance("rEaLiZE", "Reality", 5));

        // miscellaneous
        assertEquals(1, sc.editDistance("Here",  "There"));
        assertEquals(5, sc.editDistance("hIt",   "miSS",   5));
        assertEquals(6, sc.editDistance("up",    "dOWn",   6));
        assertEquals(7, sc.editDistance("fEast", "fAMINE", 7));
    }

    public void testDictionary()
    {
        NoCaseSpellChecker sc = new NoCaseSpellChecker();
        sc.addDictionary("/home/jpace/proj/doctorj/etc/words.en_US");
        
        assertTrue(sc.hasWord("Locate"));
        assertTrue(sc.hasWord("LogAritHM"));
        assertFalse(sc.hasWord("EuJiffEroUs")); // alas.

        Map nearMatches = new TreeMap();
        boolean isOK = sc.isCorrect("badd", nearMatches);
        tr.Ace.log("isOK: " + isOK);
        tr.Ace.log("nearMatches", nearMatches);
    }
    
}
