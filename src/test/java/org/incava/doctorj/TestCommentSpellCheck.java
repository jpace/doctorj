package org.incava.doctorj;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import org.incava.ijdk.util.MultiMap;


public class TestCommentSpellCheck extends TestCase {
    static class TestableCommentSpellCheck extends CommentSpellCheck {
        class Misspelling {
            public String word;

            public int position;

            public Map nearMatches;

            public Misspelling(String word, int position, Map nearMatches) {
                this.word = word;
                this.position = position;
                this.nearMatches = nearMatches;
            }

            public String toString() {
                return "[" + word + ", " + position + ", {" + nearMatches + "}";
            }
        }

        private List misspellings = new ArrayList();

        public void check(String desc) {
            misspellings = new ArrayList();
            super.check(desc);
        }
            
        protected void wordMisspelled(String word, int position, MultiMap<Integer, String> nearMatches) {
            misspellings.add(new Misspelling(word, position, nearMatches));
        }
    }

    private static TestableCommentSpellCheck tcsc = new TestableCommentSpellCheck();
    
    static {
        tcsc.addDictionary("/home/jpace/proj/doctorj/etc/words.en_US");
    }
    
    public TestCommentSpellCheck(String name) {
        super(name);
    }

    public void runSpellTest(String comment, int nMisspellings) {
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
        runSpellTest(
            "/** This is a comment.\n" +
            "  *\n" +
            "  *<pre>\n" +
            "  * nuffim eh?\n" +
            "  *</pre>\n" +
            "  */\n",
            0);
            
        runSpellTest(
            "/** This is a comment.\n" +
            "  *\n" +
            "  *<pre>\n" +
            "  * nuttin in heer shuld bie checkt\n" +
            "  *</pre>\n" +
            "  */\n",
            0);

        runSpellTest(
            "/** This is a comment.\n" +
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
    
}
