package org.incava.text.spell;


/**
 * Calculates the edit distance between two strings, being completely
 * insensitive as regards to case.
 */
public class NoCaseSpellChecker extends SpellChecker {

    public NoCaseSpellChecker() {
        super(SpellChecker.CaseType.CASE_INSENSITIVE);
    }
}
