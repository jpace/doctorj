package org.incava.doctorj;

import junit.framework.TestCase;
import org.incava.analysis.Violation;


public class TestCtorDocAnalyzer extends Tester {

    public TestCtorDocAnalyzer(String name) {
        super(name);
    }

    public void testReturnTag() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @return Something\n" +
                 "      */\n" +
                 "    Test() {}\n" +
                 "}\n",
                 new Violation[] {
                     new Violation("Tag not valid for constructor", 4, 9, 4, 15)
                 });
    }

}
