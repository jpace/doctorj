package org.incava.text.spell;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import org.incava.ijdk.util.MultiMap;

public class TestParsingSpellChecker extends TestCase {
    static class TestableParsingSpellChecker extends ParsingSpellChecker {
        private List<Misspelling> misspellings = new ArrayList<Misspelling>();

        public TestableParsingSpellChecker() {
            super(new NoCaseSpellChecker());
        }

        public void check(String desc) {
            misspellings = new ArrayList<Misspelling>();
            super.check(desc);
        }
            
        protected void wordMisspelled(String word, int position, MultiMap<Integer, String> nearMatches) {
            misspellings.add(new Misspelling(word, position, nearMatches));
        }
    }

    private static TestableParsingSpellChecker tcsc = new TestableParsingSpellChecker();

    static {
        Locale locale = Locale.getDefault();
        tr.Ace.log("locale", locale);
        String wordListFile = "words." + locale;
        tr.Ace.log("wordListFile", wordListFile);

        InputStream wordStream = ClassLoader.getSystemResourceAsStream(wordListFile);
        tr.Ace.log("wordStream", wordStream);

        tcsc.addDictionary(wordStream);
    }
    
    public TestParsingSpellChecker(String name) {
        super(name);

        tr.Ace.setVerbose(true);
    }

    public void runSpellTest(String comment, int nMisspellings) {
        tr.Ace.log("comment", comment);
        tcsc.check(comment);
        assertEquals(nMisspellings, tcsc.misspellings.size());
    }

    public void testOK() {
        runSpellTest("// This is a comment.", 0);
    }
    
    public void testMisspelling() {
        runSpellTest("// This is comment has a mispelled word.", 1);
    }

    public void testMisspellings() {
        runSpellTest("// This is comment has twoo mispelled word.", 2);
    }

    public void testOKCapitalized() {
        runSpellTest("// This is a Comment.", 0);
    }

    public void testOKPreBlock() {
        runSpellTest("/** This is a comment.\n" +
                     "  *\n" +
                     "  *<pre>\n" +
                     "  * nuffim eh?\n" +
                     "  *</pre>\n" +
                     "  */\n",
                     0);
            
        runSpellTest("/** This is a comment.\n" +
                     "  *\n" +
                     "  *<pre>\n" +
                     "  * nuttin in heer shuld bie checkt\n" +
                     "  *</pre>\n" +
                     "  */\n",
                     0);

        runSpellTest("/** This is a comment.\n" +
                     "  *\n" +
                     "  *<pre>\n" +
                     "  * This pre block has no end.\n" +
                     "  */\n",
                     0);
    }

    public void testOKCodeBlock() {
        runSpellTest("/** This is a comment that refers to <code>str</code>. */", 0);
        runSpellTest("/** This is a comment that refers to <code>somethingthatdoesnotend */", 0);
    }

    public void testOKLink() {
        runSpellTest("/** This is a comment that refers to a {@link tosomewhere}. */", 0);
        runSpellTest("/** This is a comment that refers to a {@link tosomewherefarbeyond */", 0);     // "}" -- for Emacs
    }

    public void testOKPreThenCode() {
        runSpellTest("/** This is a comment.\n" +
                     "  *\n" +
                     "  *<pre>\n" +
                     "  * nuffim eh?\n" +
                     "  *</pre>\n" +
                     "  *<code>\n" +
                     "  * rredd eh?\n" +
                     "  *</code>\n" +
                     "  */\n",
                     0);
    }
}
