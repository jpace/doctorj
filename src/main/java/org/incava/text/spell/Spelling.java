package org.incava.text.spell;

import org.incava.ijdk.lang.MathExt;


/**
 * Spelling utilities. Mainly just Levenshtein edit distance.
 */
public class Spelling {

    protected static final int COMP_LEN = 20;

    protected static final int ARR_SIZE = COMP_LEN + 1;

    protected static final int ADDITION = 1;

    protected static final int CHANGE = 2;

    protected static final int DELETION = 1;

    protected static final int DEFAULT_EDIT_DISTANCE_MAX = 3;

    /**
     * Computes the Levenshtein edit distance between the two words, with a
     * maximum of 3, at which point the distance is no longer computed.
     */
    public static int getEditDistance(String str1, String str2) {
        return getEditDistance(str1, str2, DEFAULT_EDIT_DISTANCE_MAX);
    }

    /**
     * Computes the Levenshtein edit distance between the two words, with a
     * specified maximum threshold.
     */
    public static int getEditDistance(String str1, String str2, int maximum) {
        int len1 = Math.min(str1.length(), COMP_LEN);
        int len2 = Math.min(str2.length(), COMP_LEN);
        
        // A minimum threshold of three is used for better results with short
        // strings (this is a modification from the original C code).
        
        int threshold = Math.max(maximum, (int)Math.floor((double)1 + (len1 + 2) / 4.0));
        
        int diff = Math.abs(len1 - len2);
        if (diff > threshold) {
            return -1 * diff;
        }
        else {
            return compare(str1, len1, str2, len2);
        }
    }

    /**
     * Compares the two characters. English words should probably be case
     * insensitive; code should not.
     */
    protected static int compare(String str1, int len1, String str2, int len2) {
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
