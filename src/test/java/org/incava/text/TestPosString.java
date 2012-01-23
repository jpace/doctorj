package org.incava.text;

import junit.framework.TestCase;
import org.incava.ijdk.lang.Range;

public class TestPosString extends TestCase {
    private PosString str0;

    private PosString str1;

    private PosString abc;

    private PosString tenletters;

    public TestPosString(String name) {
        super(name);

        tr.Ace.setVerbose(true);
    }

    public void setUp() {
        str0 = new PosString("this is a test");
        str1 = new PosString("this too is a test", 12);
        abc  = new PosString("abc");
        tenletters = new PosString("tenletters");
    }

    public void assertPosition(int exp, PosString pstr) {
        String msg = "pstr: " + pstr;
        tr.Ace.log(msg);
        assertEquals(msg, exp, pstr.getPosition());
    }

    public String getMessage(PosString pstr, int num) {
        return "pstr: '" + pstr + "'; num: " + num;
    }

    public void assertSubstring(String exp, PosString pstr, int num) {
        assertEquals(getMessage(pstr, num), exp, pstr.substring(num));
    }

    public void assertCurrentChar(Character exp, PosString pstr) {
        assertEquals(pstr.toString(), exp, pstr.currentChar());
    }

    public void assertHasChar(boolean exp, PosString pstr) {
        assertEquals(pstr.toString(), exp, pstr.hasChar());
    }   

    public void assertHasNumMore(boolean exp, PosString pstr, int num) {
        assertEquals(getMessage(pstr, num), exp, pstr.hasNumMore(num));
    }   

    public void assertIsMatch(boolean exp, String str, PosString pstr) {
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
        assertSubstring("this", str0, 4);
        assertSubstring("this", str0, 4);

        assertSubstring("a ",   str1, 2);
        assertSubstring("a ",   str1, 2);
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

    protected void doAdvanceFromToTest(Character expChar, int expPos, PosString pstr, String from, String to) {
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
