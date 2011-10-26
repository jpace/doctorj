package org.incava.text.spell;

import java.io.*;
import java.util.*;
import org.incava.ijdk.lang.MathExt;
import org.incava.ijdk.util.MultiMap;


/**
 * Calculates the edit distance between two strings.
 */
public class SpellChecker {

    public static final int DEFAULT_MAX_DISTANCE = 4;

    protected static final int COMP_LEN = 20;

    protected static final int ARR_SIZE = COMP_LEN + 1;

    private final MultiMap<String, String> words;

    public SpellChecker() {
        words = new MultiMap<String, String>();
    }

    /**
     * Computes the Levenstein edit distance between the two words, with a
     * maximum of 3, at which point the distance is no longer computed.
     */
    public int editDistance(String str1, String str2) {
        return editDistance(str1, str2, 3);
    }

    /**
     * Computes the Levenstein edit distance between the two words.
     */
    public int editDistance(String str1, String str2, int maximum) {
        int len1 = Math.min(str1.length(), COMP_LEN);
        int len2 = Math.min(str2.length(), COMP_LEN);
        
        // A minimum threshold of three is used for better results with short
        // strings (A modification to the original C code.)
        
        int threshold = Math.max(maximum, (int)Math.floor((double)1 + (len1 + 2) / 4.0));
        
        int diff = Math.abs(len1 - len2);
        if (diff > threshold) {
            return - 1 * diff;
        }
        else {
            return compare(str1, len1, str2, len2);
        }
    }

    public boolean nearMatch(String str1, String str2) {
        int edist = editDistance(str1, str2);
        
        // the edit distance is misleading for very short words, so it must be
        // no more than the length of either word:
        return edist >= 0 && edist <= DEFAULT_MAX_DISTANCE && edist < str1.length() && edist < str2.length();
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
        String key = getKey(word);
        this.words.put(key, word);
    }

    public boolean hasWord(String word) {
        String key = getKey(word);
        Collection<String> atLtrs = this.words.get(key);
        return atLtrs != null && atLtrs.contains(word);
    }

    /**
     * @param nearMatches a map from edit distances to matches.
     */
    public boolean isCorrect(String word, int maxEditDistance, MultiMap<Integer, String> nearMatches) {
        if (hasWord(word)) {
            return true;
        }
        else if (nearMatches != null) {
            char[]             wordChars = word.toCharArray();
            int                wordLen   = wordChars.length;
            String             key       = getKey(word);        
            Collection<String> wds       = this.words.get(key);

            //$$$ here is a good place for iterator(...)
            if (wds != null) {
                for (String w : wds) {
                    int ed = editDistance(word, w, maxEditDistance);
                    if (ed >= 0 && ed <= maxEditDistance) {
                        nearMatches.put(ed, w);
                    }
                }
            }
        }
        return false;
    }
    
    public boolean isCorrect(String word, MultiMap<Integer, String> nearMatches) {
        return isCorrect(word, DEFAULT_MAX_DISTANCE, nearMatches);
    }

    /**
     * Compares the two characters. English words should probably be case
     * insensitive; code should not.
     */
    protected int compare(String str1, int len1, String str2, int len2) {
        final int ADDITION = 1;
        final int CHANGE = 2;
        final int DELETION = 1;

        int[][] distance = new int[ARR_SIZE][ARR_SIZE];
        distance[0][0] = 0;
    
        for (int j = 1; j < ARR_SIZE; ++j) {
            distance[0][j] = distance[0][j - 1] + ADDITION;
            distance[j][0] = distance[j - 1][0] + DELETION;
        }
    
        for (int i = 1; i <= len1; ++i) {
            for (int j = 1; j <= len2; ++j) {
                distance[i][j] = MathExt.min(distance[i - 1][j - 1] + (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : CHANGE),
                                             distance[i][j - 1] + ADDITION,
                                             distance[i - 1][j] + DELETION);
            }
        }
        
        return distance[len1][len2];
    }

}
