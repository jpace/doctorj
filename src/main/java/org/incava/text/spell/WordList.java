package org.incava.text.spell;

import java.io.Reader;
import java.io.InputStream;
import java.util.*;
import org.incava.ijdk.io.FileExt;
import org.incava.ijdk.lang.MathExt;
import org.incava.ijdk.lang.StringExt;
import org.incava.ijdk.util.MultiMap;
import static org.incava.ijdk.util.IUtil.*;

public class WordList {
    private final MultiMap<Character, String> words;
    private final SpellingCaseType caseType;

    public WordList(SpellingCaseType caseType) {
        this.words = new MultiMap<Character, String>();
        this.caseType = caseType;
    }

    /**
     * Returns the string, lowering case if appropriate.
     */
    public String getString(String str) {
        return this.caseType == SpellingCaseType.INSENSITIVE ? str.toLowerCase() : str;
    }

    /**
     * Adds the given words.
     */
    public boolean addWords(List<String> words) {
        for (String word : words) {
            if (isTrue(word)) {
                addWord(word);
            }
        }

        return true;
    }

    /**
     * Same as <code>addWords</code>, but assumes that there are no empty strings.
     */
    public boolean addAllWords(List<String> words) {
        for (String word : words) {
            addWord(word);
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

    private void getByCharacter(String wstr, int index, Collection<String> matches) {
        Character ch = StringExt.charAt(wstr, index);
        if (ch == null) {
            return;
        }
        
        Collection<String> atLtr = this.words.get(ch);
        if (isNotNull(atLtr)) {
            matches.addAll(atLtr);
        }
    }

    public boolean hasWord(String word) {
        String wstr = getString(word);
        return getWordsForMatch(wstr).contains(wstr);
    }

    private Collection<String> getWordsForMatch(String wstr) {
        Set<String> matches = new HashSet<String>();
        getByCharacter(wstr, 0, matches);
        getByCharacter(wstr, 1, matches);
        return matches;
    }

    public boolean checkCorrectness(String word, int maxEditDistance, MultiMap<Integer, String> nearMatches) {
        String wstr = getString(word);
        
        if (hasWord(wstr)) {
            return true;
        }
        else if (nearMatches != null) {
            findNearMatches(word, maxEditDistance, nearMatches);
        }
        return false;
    }
    
    public void findNearMatches(String word, int maxEditDistance, MultiMap<Integer, String> nearMatches) {
        String wstr = getString(word);
        
        Collection<String> matches = getWordsForMatch(wstr);
        for (String w : matches) {
            int ed = Spelling.getEditDistance(wstr, w, maxEditDistance);
            if (ed >= 0 && ed <= maxEditDistance) {
                nearMatches.put(ed, w);
            }
        }
    }    
}
