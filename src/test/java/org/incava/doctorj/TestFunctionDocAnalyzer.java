package org.incava.doctorj;

import junit.framework.TestCase;
import org.incava.analysis.Violation;


public class TestFunctionDocAnalyzer extends Tester {

    public TestFunctionDocAnalyzer(String name) {
        super(name);
    }

    public void testMethodSerialDataOK() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @serialData Something.\n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void testCtorSerialDataOK() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @serialData Something.\n" +
                 "      */\n" +
                 "    Test() {}\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void testMethodSerialDataUndescribed() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @serialData \n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(FunctionDocAnalyzer.MSG_SERIALDATA_WITHOUT_DESCRIPTION, 4, 9, 4, 19)
                 });
    }

    public void testCtorSerialDataUndescribed() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @serialData \n" +
                 "      */\n" +
                 "    Test() {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(FunctionDocAnalyzer.MSG_SERIALDATA_WITHOUT_DESCRIPTION, 4, 9, 4, 19)
                 });
    }
    
}
