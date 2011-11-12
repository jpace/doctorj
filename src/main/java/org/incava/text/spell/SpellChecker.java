package org.incava.text.spell;

import java.io.Reader;
import java.io.InputStream;
import java.util.*;
import org.incava.ijdk.io.FileExt;
import org.incava.ijdk.lang.MathExt;
import org.incava.ijdk.lang.StringExt;
import org.incava.ijdk.util.MultiMap;
import static org.incava.ijdk.util.IUtil.*;

/**
 * Calculates the edit distance between two strings.
 */
public class SpellChecker {
    public static final int DEFAULT_MAX_DISTANCE = 4;

    private final SpellingCaseType caseType;
    private final Set<WordList> wordLists;
    private final WordList wordList;

    public SpellChecker(SpellingCaseType caseType) {
        this.wordLists = new HashSet<WordList>();
        this.caseType = caseType;
        this.wordList = new WordList(caseType);
        this.wordLists.add(this.wordList);
    }

    /**
     * Returns the string, lowering case if appropriate.
     */
    public String getString(String str) {
        return this.caseType == SpellingCaseType.INSENSITIVE ? str.toLowerCase() : str;
    }

    public boolean nearMatch(String str1, String str2) {
        int edist = Spelling.getEditDistance(str1, str2);
        
        // the edit distance is misleading for very short words, so it must be
        // no more than the length of either word:
        return MathExt.isWithin(edist, 0, DEFAULT_MAX_DISTANCE) && edist < str1.length() && edist < str2.length();
    }

    /**
     * Adds the given dictionary. Returns whether it could be read and had content.
     */
    public boolean addDictionary(String dictName) {
        this.wordLists.add(new Dictionary(this.caseType, dictName));
        return true;
    }

    /**
     * Adds the given dictionary. Returns whether it could be read and had content.
     */
    public boolean addDictionary(Reader dictReader) {
        this.wordLists.add(new Dictionary(this.caseType, dictReader));
        return true;
    }

    /**
     * Adds the given dictionary. Returns whether it could be read and had content.
     */
    public boolean addDictionary(InputStream dictStream) {
        this.wordLists.add(new Dictionary(this.caseType, dictStream));
        return true;
    }

    /**
     * Adds the given words.
     */
    public boolean addWords(String[] words) {
        for (String word : words) {
            if (isTrue(word)) {
                addWord(word);
            }
        }
        return true;
    }

    public void addWord(String word) {
        String wstr = getString(word);
        this.wordList.addWord(wstr);
    }

    public boolean checkCorrectness(String word, int maxEditDistance, MultiMap<Integer, String> nearMatches) {
        String wstr = getString(word);

        for (WordList wordList : wordLists) {
            if (wordList.checkCorrectness(wstr, maxEditDistance, nearMatches)) {
                return true;
            }
        }
        return false;
    }
    
    public void findNearMatches(String word, int maxEditDistance, MultiMap<Integer, String> nearMatches) {
        for (WordList wordList : wordLists) {
            wordList.findNearMatches(word, maxEditDistance, nearMatches);
        }
    }
    
    public boolean checkCorrectness(String word, MultiMap<Integer, String> nearMatches) {
        return checkCorrectness(word, DEFAULT_MAX_DISTANCE, nearMatches);
    }
}
