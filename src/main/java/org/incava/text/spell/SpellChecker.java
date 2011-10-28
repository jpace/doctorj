package org.incava.text.spell;

import java.io.*;
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

    public enum CaseType { CASE_SENSITIVE, CASE_INSENSITIVE };

    private final MultiMap<Character, String> words;

    private final CaseType caseType;

    public SpellChecker(CaseType caseType) {
        this.words = new MultiMap<Character, String>();
        this.caseType = caseType;
    }

    /**
     * Returns the string, lowering case if appropriate.
     */
    public String getString(String str) {
        return this.caseType == CaseType.CASE_INSENSITIVE ? str.toLowerCase() : str;
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
    public boolean addDictionary(String dictionary) {
        tr.Ace.log("adding dictionary: " + dictionary);

        for (String line : FileExt.readLines(dictionary)) {
            if (isTrue(line)) {
                addWord(line);
            }
        }

        return true;
    }

    public void addWord(String word) {
        String wstr = getString(word);
        addByCharacter(wstr, 0);
        addByCharacter(wstr, 1);
    }

    private void addByCharacter(String wstr, int index) {
        Character ch = StringExt.charAt(wstr, index);
        if (ch != null) {
            this.words.put(ch, wstr);
        }
    }

    private Collection<String> getByCharacter(String wstr, int index) {
        Character ch = StringExt.charAt(wstr, index);
        Collection<String> atLtr = null;

        if (ch != null) {
            atLtr = this.words.get(ch);
        }

        return atLtr == null ? new ArrayList<String>() : atLtr;
    }

    public boolean hasWord(String word) {
        String wstr = getString(word);
        return getWordsForMatch(wstr).contains(wstr);
    }

    private Collection<String> getWordsForMatch(String wstr) {
        Set<String> matches = new TreeSet<String>();

        matches.addAll(getByCharacter(wstr, 0));
        matches.addAll(getByCharacter(wstr, 1));

        return matches;
    }

    /**
     * @param nearMatches a map from edit distances to matches.
     */
    public boolean isCorrect(String word, int maxEditDistance, MultiMap<Integer, String> nearMatches) {
        tr.Ace.log("word", word);
        String wstr = getString(word);
        
        if (hasWord(wstr)) {
            return true;
        }
        else if (nearMatches != null) {
            Collection<String> matches = getWordsForMatch(wstr);
            for (String w : matches) {
                int ed = Spelling.getEditDistance(wstr, w, maxEditDistance);
                if (ed >= 0 && ed <= maxEditDistance) {
                    nearMatches.put(ed, w);
                }
            }
        }
        return false;
    }
    
    public boolean isCorrect(String word, MultiMap<Integer, String> nearMatches) {
        return isCorrect(word, DEFAULT_MAX_DISTANCE, nearMatches);
    }

}
