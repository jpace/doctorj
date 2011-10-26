package org.incava.text.spell;

import java.io.*;
import java.util.*;
import org.incava.ijdk.lang.MathExt;
import org.incava.ijdk.util.MultiMap;
import static org.incava.ijdk.util.IUtil.*;


/**
 * Calculates the edit distance between two strings.
 */
public class SpellChecker {

    public static final int DEFAULT_MAX_DISTANCE = 4;

    public enum CaseType { CASE_SENSITIVE, CASE_INSENSITIVE };

    private final MultiMap<String, String> words;

    private final CaseType caseType;

    public SpellChecker(CaseType caseType) {
        this.words = new MultiMap<String, String>();
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

        try {
            BufferedReader br = new BufferedReader(new FileReader(dictionary));
            
            while (true) {
                String word = br.readLine();
                if (word == null) {
                    break;
                }
                else {
                    addWord(word);
                }
            }
            return true;
        }
        catch (IOException ioe) {
            tr.Ace.log("ioe", ioe);
            return false;
        }
    }

    public String getKey(String word) {
        char[] chars = word.toCharArray();
        if (chars.length > 1) {
            char c0 = chars[0];
            char c1 = chars[1];
            if (c0 > c1) {
                char t = c0;
                c0 = c1;
                c1 = t;
            }
            return "" + c0 + c1;
        }
        else if (chars.length > 0) {
            return "" + chars[0];
        }
        else {
            return null;
        }
    }

    public void addWord(String word) {
        String wstr = getString(word);
        String key = getKey(wstr);
        this.words.put(key, wstr);
    }

    public boolean hasWord(String word) {
        String wstr = getString(word);
        String key = getKey(wstr);
        Collection<String> atLtrs = this.words.get(key);
        return atLtrs != null && atLtrs.contains(wstr);
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
            char[]             wordChars = wstr.toCharArray();
            int                wordLen   = wordChars.length;
            String             key       = getKey(wstr);        
            Collection<String> wds       = this.words.get(key);

            for (String w : iter(wds)) {
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
