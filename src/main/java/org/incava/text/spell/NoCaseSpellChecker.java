package org.incava.text.spell;

import java.io.*;
import java.util.*;


/**
 * Calculates the edit distance between two strings.
 */
public class NoCaseSpellChecker extends SpellChecker {

    public int compare(String str1, int len1, String str2, int len2) {
        return super.compare(str1.toLowerCase(), len1, str2.toLowerCase(), len2);
    }
    
    public boolean hasWord(String word) {
        return super.hasWord(word.toLowerCase());
    }

    public void addWord(String word) {
        super.addWord(word.toLowerCase());
    }
}
