package org.incava.text.spell;

import java.io.InputStream;
import java.util.*;
import org.incava.ijdk.util.MultiMap;
import org.incava.text.PString;

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
     * The string currently spell-checked.
     */
    private PString pstr;
    
    public ParsingSpellChecker(SpellChecker checker) {
        this(checker, false);
    }

    public ParsingSpellChecker(SpellChecker checker, boolean canCheck) {
        this.checker = checker;
        this.canCheck = canCheck;
    }

    protected boolean hasMore() {
        return this.pstr.hasChar();
    }

    protected boolean atBlank() {
        return this.pstr.hasChar() && !this.pstr.isMatch("<") && !this.pstr.isMatch("{@") && !Character.isLetterOrDigit(currentChar());
    }
    
    protected void skipBlanks() {
        while (atBlank()) {
            this.pstr.advancePosition();
        }
    }

    public boolean addDictionary(String dictionary) {
        this.canCheck = this.checker.addDictionary(dictionary) || this.canCheck;
        return this.canCheck;
    }

    public boolean addDictionary(InputStream dictionary) {
        this.canCheck = this.checker.addDictionary(dictionary) || this.canCheck;
        return this.canCheck;
    }

    public void addWord(String word) {
        this.checker.addWord(word);
        this.canCheck = true;
    }

    public void check(String str) {
        if (!this.canCheck) {
            return;
        }

        this.pstr = new PString(str);
    
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

    /**
     * Consumes from one string to another.
     */
    protected void consumeFromTo(String from, String to) {
        if (consumeFrom(from)) {
            consumeTo(to);
        }
    }

    /**
     * Skips content between pairs of brackets, a la HTML, such as, oh,
     * "<html>foobar</html>." Doesn't handle slash-gt, such as "<html
     * style="lackthereof" />".
     */
    protected void skipBracketedSection(String section) {
        consumeFromTo("<" + section + ">", "</" + section + ">");
    }

    protected void skipLink() {
        consumeFromTo("{@link", "}");
    }
    
    protected void skipToWord() {
        skipBracketedSection("code");
        skipBracketedSection("pre");
        skipLink();
        consumeFrom("&nbsp;");
    }

    protected void checkWord(String word, int position) {
        MultiMap<Integer, String> nearMatches = new MultiMap<Integer, String>();
        boolean valid = this.checker.checkCorrectness(word, nearMatches);
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

    protected boolean consumeFrom(String what) {
        skipBlanks();
        return this.pstr.advanceFrom(what);
    }

    protected void consumeTo(String what) {
        this.pstr.advanceTo(what);
    }

    protected Character currentChar() {
        return this.pstr.currentChar();
    }

    protected void checkCurrentWord() {
        StringBuffer word = new StringBuffer();
        word.append(currentChar());
        
        int startingPosition = this.pstr.getPosition();
        boolean canCheck = true;

        this.pstr.advancePosition();

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
                this.pstr.advancePosition();
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
                // but punctuation up to the next space or end of string.
                if (this.pstr.getPosition() + 1 == this.pstr.getLength()) {
                    // that's OK to check
                    break;
                }
                else {
                    this.pstr.advancePosition();
                    while (hasMore() && !Character.isWhitespace(currentChar()) && !Character.isLetterOrDigit(currentChar())) {
                        // skipping through punctuation
                        this.pstr.advancePosition();
                    }
                    if (this.pstr.getPosition() == this.pstr.getLength() || Character.isWhitespace(currentChar())) {
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
        if (canCheck && this.pstr.getPosition() - startingPosition > 1) {
            checkWord(word.toString(), startingPosition);
        }
    }

    protected void skipThroughWord() {
        this.pstr.advancePosition();
        while (hasMore() && !Character.isWhitespace(currentChar())) {
            this.pstr.advancePosition();
        }
    }
}
