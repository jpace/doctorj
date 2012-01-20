package org.incava.text;

import junit.framework.TestCase;

public class TestPosString extends TestCase {
    private PosString str0;

    private PosString str1;

    private PosString abc;

    public TestPosString(String name) {
        super(name);

        tr.Ace.setVerbose(true);
    }

    public void setUp() {
        str0 = new PosString("this is a test");
        str1 = new PosString("this too is a test", 12);
        abc = new PosString("abc");
    }

    public void assertPosition(int exp, PosString pstr) {
        String msg = "pstr: '" + pstr + "'";
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

    public void assertHasMore(boolean exp, PosString pstr) {
        assertEquals(pstr.toString(), exp, pstr.hasMore());
    }   

    public void assertHasNumMore(boolean exp, PosString pstr, int num) {
        assertEquals(getMessage(pstr, num), exp, pstr.hasMore());
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

    public void testHasMore() {
        assertHasMore(true, str0);
        // no change
        assertHasMore(true, str0);

        str0.advancePosition();
        assertHasMore(true, str0);

        str0.advancePosition(12);
        assertHasMore(true, str0);

        str0.advancePosition();
        assertHasMore(false, str0);
    }

    public void testHasNumMore() {
        assertHasNumMore(true, str0, 0);
        assertHasNumMore(true, str0, 1);

        str0.advancePosition();
        assertHasNumMore(true, str0, 1);

        str0.advancePosition(11);
        assertHasNumMore(true,  str0, 1);

        assertHasNumMore(true,  str0, 1);
        assertHasNumMore(false, str0, 2);

        str0.advancePosition();
        assertHasNumMore(true, str0, 0);
    }
}
