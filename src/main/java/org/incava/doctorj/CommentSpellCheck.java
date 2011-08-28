package org.incava.doctorj;

import java.io.*;
import java.util.*;
import org.incava.io.FileExt;
import org.incava.text.*;


public class CommentSpellCheck
{
    public static CommentSpellCheck instance = null;

    public static CommentSpellCheck getInstance()
    {
        if (instance == null) {
            instance = new CommentSpellCheck();
        }
        return instance;
    }

    private boolean _canCheck;

    private SpellChecker _checker;

    /**
     * The current description we're working on.
     */
    private String _desc;

    /**
     * The length of the current description.
     */
    private int _len;

    /**
     * The current position within the description.
     */
    private int _pos;
    
    protected CommentSpellCheck()
    {
        _checker = new NoCaseSpellChecker();
        _canCheck = false;
    }

    public boolean addDictionary(String dictionary)
    {
        _canCheck = _checker.addDictionary(dictionary) || _canCheck;
        return _canCheck;
    }

    public void addWord(String word)
    {
        _checker.addWord(word);
        _canCheck = true;
    }

    public void check(String description)
    {
        if (_canCheck) {
            // tr.Ace.log("checking '" + description + "'");

            _desc = description;
            _len  = _desc.length();
            _pos  = 0;
    
            while (_pos < _len) {
                skipToWord();
                if (_pos < _len) {
                    if (Character.isLetter(_desc.charAt(_pos))) {
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

    protected void skipSection(String section)
    {
        // tr.Ace.log("section: " + section);
        if (consume("<" + section + ">")) {
            // tr.Ace.log("got section: " + section);
            consumeTo("</" + section + ">");
        }
    }
    
    protected void skipLink()
    {
        if (consume("{@link")) {
            consumeTo("}");
        }
    }

    protected void skipBlanks()
    {
        while (_pos + 2 < _len && _desc.charAt(_pos) != '<' && !_desc.substring(_pos, _pos + 2).equals("{@") && !Character.isLetterOrDigit(_desc.charAt(_pos))) {
            ++_pos;
        }
    }
    
    protected void skipToWord()
    {
        skipSection("code");
        skipSection("pre");
        skipLink();
        consume("&nbsp;");
    }

    protected void checkWord(String word, int position)
    {
        // tr.Ace.log("checking word '" + word + "' at position " + position);

        Map nearMatches = new TreeMap();
        boolean valid = _checker.isCorrect(word, nearMatches);
        if (!valid) {
            wordMisspelled(word, position, nearMatches);
        }
    }

    protected void wordMisspelled(String word, int position, Map nearMatches)
    {
        int nPrinted = 0;
        final int printGoal = 15;
        for (int i = 0; nPrinted < printGoal && i < 4; ++i) { // 4 == max edit distance
            Integer eDist = new Integer(i);
            List matches = (List)nearMatches.get(eDist);
            if (matches != null) {
                Iterator it = matches.iterator();
                while (it.hasNext()) {
                    // This is not debugging output -- this is actually wanted. 
                    // But I often run "glark '^\s*System.out' to find all my
                    // print statements, so we'll hide this very sneakily:
                    /* escond */ System.out.println("    near match '" + it.next() + "': " + i);
                    ++nPrinted;
                }
            }
        }
    }

    protected boolean consume(String what)
    {
        skipBlanks();
        if (_pos + what.length() < _len && _desc.substring(_pos).startsWith(what)) {
            _pos += what.length();
            return true;
        }
        else {
            return false;
        }
    }

    protected void consumeTo(String what)
    {
        int _len = _desc.length();
        while (_pos < _len && _pos + what.length() < _len && !_desc.substring(_pos).startsWith(what)) {
            ++_pos;
        }
    }

    protected void checkCurrentWord()
    {
        StringBuffer word = new StringBuffer();
        word.append(_desc.charAt(_pos));
        
        int startingPosition = _pos;
        boolean canCheck = true;

        ++_pos;

        // spell check words that do not have:
        //     - mixed case (varName)
        //     - embedded punctuation ("wouldn't", "pkg.foo")
        //     - numbers (M16, BR549)
        while (_pos < _len) {
            char ch = _desc.charAt(_pos);
            if (Character.isWhitespace(ch)) {
                break;
            }
            else if (Character.isLowerCase(ch)) {
                word.append(ch);
                ++_pos;
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
                if (_pos + 1 == _len) {
                    // that's OK to check
                    break;
                }
                else {
                    ++_pos;
                    while (_pos < _len && !Character.isWhitespace(_desc.charAt(_pos)) && !Character.isLetterOrDigit(_desc.charAt(_pos))) {
                        // skipping through punctuation
                        ++_pos;
                    }
                    if (_pos == _len || Character.isWhitespace(_desc.charAt(_pos))) {
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

        // tr.Ace.log("word: '" + word + "'");
    
        // has to be more than one character:
        if (canCheck && _pos - startingPosition > 1) {
            checkWord(word.toString(), startingPosition);
        }
    }

    protected void skipThroughWord()
    {
        ++_pos;
        while (_pos < _len && !Character.isWhitespace(_desc.charAt(_pos))) {
            ++_pos;
        }
    }

}
