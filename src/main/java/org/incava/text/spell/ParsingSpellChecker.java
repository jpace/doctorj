package org.incava.text.spell;

import java.io.*;
import java.util.*;
import org.incava.ijdk.io.FileExt;
import org.incava.ijdk.util.MultiMap;


public class ParsingSpellChecker {

    /**
     * Whether we can spell check, which we can't until we get a word or a
     * dictionary (which is a thing with a lot of words).
     */
    private boolean canCheck;

    /**
     * The spell checker, which doesn't parse.
     */
    private final SpellChecker checker;

    /**
     * The current description we're working on.
     */
    private String desc;

    /**
     * The length of the current description.
     */
    private int len;

    /**
     * The current position within the description.
     */
    private int pos;
    
    public ParsingSpellChecker(SpellChecker checker) {
        this.checker = checker;
        this.canCheck = false;
    }

    public boolean addDictionary(String dictionary) {
        this.canCheck = this.checker.addDictionary(dictionary) || this.canCheck;
        return this.canCheck;
    }

    public void addWord(String word) {
        this.checker.addWord(word);
        this.canCheck = true;
    }

    public void check(String description) {
        if (this.canCheck) {
            this.desc = description;
            this.len = this.desc.length();
            this.pos = 0;
    
            while (hasMore()) {
                skipToWord();
                if (hasMore()) {
                    if (Character.isLetter(currentChar())) {
                        checkCurrentWord();
                    }
                    else {
                        // not at an alpha character. Might be some screwy formatting or
                        // a nonstandard tag.
                        skipThroughWord();
                    }
                }
            }
        }
    }

    /**
     * Skips content between pairs of brackets, a la HTML, such as, oh,
     * "<html>foobar</html>." Doesn't handle slash-gt, such as "<html
     * style="lackthereof" />".
     */
    protected void skipBracketedSection(String section) {
        if (consume("<" + section + ">")) {
            consumeTo("</" + section + ">");
        }
    }

    protected void skipLink() {
        if (consume("{@link")) {
            consumeTo("}");
        }
    }
    
    protected void skipBlanks() {
        while (this.pos + 2 < this.len && currentChar() != '<' && !this.desc.substring(this.pos, this.pos + 2).equals("{@") && !Character.isLetterOrDigit(currentChar())) {
            ++this.pos;
        }
    }
    
    protected void skipToWord() {
        skipBracketedSection("code");
        skipBracketedSection("pre");
        skipLink();
        consume("&nbsp;");
    }

    protected void checkWord(String word, int position) {
        MultiMap<Integer, String> nearMatches = new MultiMap<Integer, String>();
        boolean valid = this.checker.isCorrect(word, nearMatches);
        if (!valid) {
            wordMisspelled(word, position, nearMatches);
        }
    }

    protected void wordMisspelled(String word, int position, MultiMap<Integer, String> nearMatches) {
        int nPrinted = 0;
        final int printGoal = 15;
        for (int i = 0; nPrinted < printGoal && i < 4; ++i) { // 4 == max edit distance
            Collection<String> matches = nearMatches.get(i);
            if (matches != null) {
                for (String match : matches) {
                    // This is not debugging output -- this is actually wanted. 
                    // But I often run "glark '^\s*System.out' to find all my
                    // print statements, so we'll hide this very sneakily:
                    /* escond */ System.out.println("    near match '" + match + "': " + i);
                    ++nPrinted;
                }
            }
        }
    }

    protected boolean consume(String what) {
        skipBlanks();
        if (this.pos + what.length() < this.len && this.desc.substring(this.pos).startsWith(what)) {
            this.pos += what.length();
            return true;
        }
        else {
            return false;
        }
    }

    protected void consumeTo(String what) {
        int len = this.desc.length();
        while (this.pos < len && this.pos + what.length() < len && !this.desc.substring(this.pos).startsWith(what)) {
            ++this.pos;
        }
    }

    protected Character currentChar() {
        return this.desc.charAt(this.pos);
    }

    protected boolean hasMore() {
        return this.pos < this.len;
    }

    protected void checkCurrentWord() {
        StringBuffer word = new StringBuffer();
        word.append(currentChar());
        
        int startingPosition = this.pos;
        boolean canCheck = true;

        ++this.pos;

        // spell check words that do not have:
        //     - mixed case (varName)
        //     - embedded punctuation ("wouldn't", "pkg.foo")
        //     - numbers (M16, BR549)
        while (hasMore()) {
            char ch = currentChar();
            if (Character.isWhitespace(ch)) {
                break;
            }
            else if (Character.isLowerCase(ch)) {
                word.append(ch);
                ++this.pos;
            }
            else if (Character.isUpperCase(ch)) {
                skipThroughWord();
                return;
            }
            else if (Character.isDigit(ch)) {
                skipThroughWord();
                return;
            }
            else {
                // must be punctuation, which we can check it if there's nothing
                // but punctuation up to the next space or end of description.
                if (this.pos + 1 == this.len) {
                    // that's OK to check
                    break;
                }
                else {
                    ++this.pos;
                    while (hasMore() && !Character.isWhitespace(currentChar()) && !Character.isLetterOrDigit(currentChar())) {
                        // skipping through punctuation
                        ++this.pos;
                    }
                    if (this.pos == this.len || Character.isWhitespace(currentChar())) {
                        // punctuation ended the word, so we can check this
                        break;
                    }
                    else {
                        // punctuation did NOT end the word, so we can NOT check this
                        skipThroughWord();
                        return;
                    }
                }
            }
        }

        // has to be more than one character:
        if (canCheck && this.pos - startingPosition > 1) {
            checkWord(word.toString(), startingPosition);
        }
    }

    protected void skipThroughWord() {
        ++this.pos;
        while (hasMore() && !Character.isWhitespace(currentChar())) {
            ++this.pos;
        }
    }

}
