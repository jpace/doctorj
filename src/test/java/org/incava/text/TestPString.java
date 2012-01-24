package org.incava.text;

import junit.framework.TestCase;
import org.incava.ijdk.lang.Range;

public class TestPString extends TestCase {
    private PString str0;

    private PString str1;

    private PString abc;

    private PString tenletters;

    public TestPString(String name) {
        super(name);

        tr.Ace.setVerbose(true);
    }

    public void setUp() {
        str0 = new PString("this is a test");
        str1 = new PString("this too is a test", 12);
        abc  = new PString("abc");
        tenletters = new PString("tenletters");
    }

    public void assertPosition(int exp, PString pstr) {
        String msg = "pstr: " + pstr;
        tr.Ace.log(msg);
        assertEquals(msg, exp, pstr.getPosition());
    }

    public String getMessage(PString pstr, int num) {
        return "pstr: '" + pstr + "'; num: " + num;
    }

    public void assertSubstring(String exp, PString pstr, int num) {
        assertEquals(getMessage(pstr, num), exp, pstr.substring(num));
    }

    public void assertSubstring(String exp, PString pstr, int pos, int num) {
        String msg = "pstr: '" + pstr + "'; pos: " + pos + "; num: " + num;
        assertEquals(getMessage(pstr, num), exp, pstr.substring(pos, num));
    }

    public void assertCurrentChar(Character exp, PString pstr) {
        assertEquals(pstr.toString(), exp, pstr.currentChar());
    }

    public void assertHasChar(boolean exp, PString pstr) {
        assertEquals(pstr.toString(), exp, pstr.hasChar());
    }   

    public void assertHasNumMore(boolean exp, PString pstr, int num) {
        assertEquals(getMessage(pstr, num), exp, pstr.hasNumMore(num));
    }   

    public void assertIsMatch(boolean exp, String str, PString pstr) {
        String msg = "pstr: " + pstr + "; str: '" + str + "'";
        assertEquals(msg, exp, pstr.isMatch(str));
    }

    public void testCtor() {
        assertPosition(0,  str0);
        assertPosition(12, str1);
    }

    public void testSetPosition() {
        str0.setPosition(4);
        assertPosition(4, str0);

        // backward
        str0.setPosition(2);
        assertPosition(2, str0);

        // out of range
        str0.setPosition(123);
        assertPosition(123, str0);
    }

    public void testAdvancePosition() {
        str0.advancePosition();
        assertPosition(1, str0);

        str0.advancePosition();
        assertPosition(2, str0);

        str0.advancePosition(12);
        assertPosition(14, str0);
    }

    public void testSubstring() {
        assertSubstring("abc", abc, 3);
        assertSubstring("ab",  abc, 2);
        assertSubstring("a",   abc, 1);

        abc.advancePosition();

        assertSubstring("bc", abc, 2);
        assertSubstring("b",  abc, 1);
    }

    public void testSubstringWithPosition() {
        assertSubstring("abc", abc, 0, 3);
        assertSubstring("ab",  abc, 0, 2);
        assertSubstring("a",   abc, 0, 1);
        assertSubstring("",    abc, 0, 0);

        assertSubstring("bc",  abc, 1, 2);
        assertSubstring("c", abc, 2, 1);
        assertSubstring("", abc, 3, 0);

        abc.advancePosition();

        assertSubstring("abc", abc, 0, 3);
        assertSubstring("bc", abc, 1, 2);
        assertSubstring("c", abc, 2, 1);
        assertSubstring("", abc, 3, 0);
    }

    public void testCurrentChar() {
        assertCurrentChar('t', str0);

        str0.advancePosition();
        assertCurrentChar('h', str0);

        str0.advancePosition(2);
        assertCurrentChar('s', str0);

        // out of range:
        str0.advancePosition(12);
        assertCurrentChar(null, str0);

        // str1 position starts at 12
        assertCurrentChar('a', str1);

        str1.advancePosition();
        assertCurrentChar(' ', str1);

        str1.advancePosition(3);
        assertCurrentChar('s', str1);
    }

    public void testHasChar() {
        assertHasChar(true, abc); // at 'a'
        // no change
        assertHasChar(true, abc);

        abc.advancePosition();  // at 'b'
        assertHasChar(true, abc);

        abc.advancePosition(); // at 'c'
        assertHasChar(true, abc);

        abc.advancePosition(); // off end
        assertHasChar(false, abc);
    }

    public void testHasNumMore() {
        for (int i : new Range(0, 9)) {
            assertHasNumMore(true, tenletters, i);
        }
    }

    public void testIsMatch() {
        assertIsMatch(true,  "a", abc);
        assertIsMatch(false, "b", abc);
        assertIsMatch(false, "c", abc);

        abc.advancePosition();
        assertIsMatch(false, "a", abc);
        assertIsMatch(true,  "b", abc);
        assertIsMatch(false, "c", abc);

        abc.advancePosition();
        assertIsMatch(false, "a", abc);
        assertIsMatch(false, "b", abc);
        assertIsMatch(true,  "c", abc);

        abc.advancePosition();
        assertIsMatch(false, "a", abc);
        assertIsMatch(false, "b", abc);
        assertIsMatch(false, "c", abc);
    }

    public void testAdvanceTo() {
        assertCurrentChar('a', abc);
        abc.advanceTo("b");
        assertCurrentChar('b', abc);
        abc.advanceTo("c");
        assertCurrentChar('c', abc);
        abc.advanceTo("d");
        assertPosition(3, abc);
        assertCurrentChar(null, abc);

        assertCurrentChar('t', tenletters);
        // the first 'e':
        tenletters.advanceTo("e");

        assertPosition(1, tenletters);
        assertCurrentChar('e', tenletters);

        // does not go to the second 'e'
        tenletters.advanceTo("e");
        assertPosition(1, tenletters);

        tenletters.advancePosition();
        
        // now it goes to the second 'e':
        tenletters.advanceTo("e");
        assertPosition(4, tenletters);
        assertCurrentChar('e', tenletters);
    }

    public void testAdvanceFrom() {
        assertCurrentChar('a', abc);
        abc.advanceFrom("a");
        assertCurrentChar('b', abc);

        // no change
        abc.advanceFrom("c");
        assertCurrentChar('b', abc);

        abc.advancePosition();  // we're on 'c' now:
        assertCurrentChar('c', abc);
        abc.advanceFrom("c");
        
        assertPosition(3, abc);
        assertCurrentChar(null, abc);
    }

    protected void doAdvanceFromToTest(Character expChar, int expPos, PString pstr, String from, String to) {
        pstr.advanceFromTo(from, to);
        assertCurrentChar(expChar, pstr);
        assertPosition(expPos, pstr);
    }

    public void testAdvanceFromTo() {
        assertCurrentChar('a', abc);
        doAdvanceFromToTest('c', 2, abc, "a", "c");

        assertCurrentChar('t', tenletters);

        // no change
        doAdvanceFromToTest('t', 0, tenletters, "e", "n");

        // to first e
        doAdvanceFromToTest('e', 1, tenletters, "t", "e");

        // to second e
        doAdvanceFromToTest('e', 4, tenletters, "e", "e");

        // to second t
        doAdvanceFromToTest('t', 5, tenletters, "e", "t");

        // to third t (next character)
        doAdvanceFromToTest('t', 6, tenletters, "t", "t");

        // to end of string
        doAdvanceFromToTest('s', 9, tenletters, "t", "s");

        // off end of string
        doAdvanceFromToTest(null, 10, tenletters, "s", "j");
    }
}
