package org.incava.text.spell;


/**
 * Calculates the edit distance between two strings, being completely
 * insensitive as regards to case.
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
