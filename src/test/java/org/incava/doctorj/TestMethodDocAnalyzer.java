package org.incava.doctorj;

import junit.framework.TestCase;
import org.incava.analysis.Violation;


public class TestMethodDocAnalyzer extends AbstractDoctorJTestCase {

    public TestMethodDocAnalyzer(String name) {
        super(name);
    }

    public void testReturnOK() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @return What this method returns.\n" +
                 "      */\n" +
                 "    int f() { return 1; }\n" +
                 "}\n");
    }

    public void testReturnFromVoid() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @return \n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n",
                 new Violation(MethodDocAnalyzer.MSG_RETURN_FOR_VOID_METHOD, 4, 9, 4, 15));
    }

    public void testReturnUndescribed() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @return \n" +
                 "      */\n" +
                 "    int f() { return 1; }\n" +
                 "}\n",
                 new Violation(MethodDocAnalyzer.MSG_RETURN_WITHOUT_DESCRIPTION, 4, 9, 4, 15));
    }

    public void testReturnTypeUsed() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @return int This returns an integer.\n" +
                 "      */\n" +
                 "    int f() { return 1; }\n" +
                 "}\n",
                 new Violation(MethodDocAnalyzer.MSG_RETURN_TYPE_USED, 4, 17, 4, 19));
    }

    public void testJava15OK() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /**\n" +
                 "     * Clones an array within the specified bounds.\n" +
                 "     * This method assumes that a is an array.\n" +
                 "     * @param a This is the first parameter.\n" +
                 "     * @param from This is the second parameter.\n" +
                 "     * @param to This is the third parameter.\n" +
                 "     */\n" +
                 "    private static <T> T[] cloneSubarray(T[] a, int from, int to) {\n" +
                 "        int n = to - from;\n" +
                 "        T[] result = (T[])Array.newInstance(a.getClass().getComponentType(), n);\n" +
                 "        System.arraycopy(a, from, result, 0, n);\n" +
                 "        return result;\n" +
                 "    }\n" +
                 "}\n",
                 "1.5");
    }
    
}
