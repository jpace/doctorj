package org.incava.doctorj;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import org.incava.analysis.Violation;


public class TestParameterDocAnalyzer extends AbstractDoctorJTestCase {

    public TestParameterDocAnalyzer(String name) {
        super(name);
    }

    public void testMethodParametersOK() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      */\n" +
                 "    void method() {}\n" +
                 "}\n");

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param i This describes the i parameter.\n" +
                 "      */\n" +
                 "    void method(int i) {}\n" +
                 "}\n");
    }

    public void testMethodParametersNoParamsInCode() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param\n" +
                 "      */\n" +
                 "    void method() {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETERS_DOCUMENTED_BUT_NO_CODE_PARAMETERS, 4, 9, 4, 14));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param i\n" +
                 "      */\n" +
                 "    void method() {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETERS_DOCUMENTED_BUT_NO_CODE_PARAMETERS, 4, 9, 4, 14));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param i This describes the i parameter.\n" +
                 "      */\n" +
                 "    void method() {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETERS_DOCUMENTED_BUT_NO_CODE_PARAMETERS, 4, 9, 4, 14));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param i This describes the i parameter.\n" +
                 "      * @param j This describes the j parameter.\n" +
                 "      */\n" +
                 "    void method() {}\n" +
                 "}\n",
                 // should be only one error:
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETERS_DOCUMENTED_BUT_NO_CODE_PARAMETERS, 4, 9, 4, 14));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param i This describes the i parameter.\n" +
                 "      * @param j This describes the j parameter.\n" +
                 "      * @param k This describes the k parameter.\n" +
                 "      * @param l This describes the l parameter.\n" +
                 "      * @param m This describes the m parameter.\n" +
                 "      */\n" +
                 "    void method() {}\n" +
                 "}\n",
                 // should be only one error:
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETERS_DOCUMENTED_BUT_NO_CODE_PARAMETERS, 4, 9, 4, 14));
    }

    public void testMethodParametersParamWithoutTarget() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param\n" +
                 "      */\n" +
                 "    void method(int i) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_WITHOUT_NAME, 4, 9, 4, 14),
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_NOT_DOCUMENTED, 6, 21, 6, 21));
    }

    public void testMethodParametersParamMisspelled() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param st The string in question.\n" +
                 "      */\n" +
                 "    void method(String str) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_MISSPELLED, 4, 16, 4, 17));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param s The string in question.\n" +
                 "      */\n" +
                 "    void method(String str) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_MISSPELLED, 4, 16, 4, 16));
    }

    public void testMethodParametersParamNotDocumented() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param comp A Comparator of some sort.\n" +
                 "      */\n" +
                 "    void method(String str, Comparator comp) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_NOT_DOCUMENTED, 6, 24, 6, 26));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param price The price of tea in China.\n" +
                 "      */\n" +
                 "    void method(String str, int price, Comparator comp) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_NOT_DOCUMENTED, 6, 24, 6, 26),
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_NOT_DOCUMENTED, 6, 51, 6, 54));
    }

    public void testMethodParametersMisordered() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param comp A Comparator of some sort.\n" +
                 "      * @param str The string in question.\n" +
                 "      */\n" +
                 "    void method(String str, Comparator comp) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_NOT_IN_CODE_ORDER, 5, 16, 5, 18));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param size The height and width of something.\n" +
                 "      * @param comp A Comparator of some sort.\n" +
                 "      * @param str The string in question.\n" +
                 "      */\n" +
                 "    void method(java.awt.Dimension size, String str, Comparator comp) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_NOT_IN_CODE_ORDER, 6, 16, 6, 18));
    }

    public void testMethodParametersTypeUsed() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param String The string in question.\n" +
                 "      */\n" +
                 "    void method(String str) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_TYPE_USED, 4, 16, 4, 21));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param String The string in question.\n" +
                 "      * @param Comparator A Comparator of some sort.\n" +
                 "      */\n" +
                 "    void method(String str, Comparator comp) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_TYPE_USED, 4, 16, 4, 21),
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_TYPE_USED, 5, 16, 5, 25));
    }

    // Same as above, but for constructors.

    public void testCtorParametersOK() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      */\n" +
                 "    Test() {}\n" +
                 "}\n");

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param i This describes the i parameter.\n" +
                 "      */\n" +
                 "    Test(int i) {}\n" +
                 "}\n");
    }

    public void testCtorParametersNoParamsInCode() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param\n" +
                 "      */\n" +
                 "    Test() {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETERS_DOCUMENTED_BUT_NO_CODE_PARAMETERS, 4, 9, 4, 14));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param i\n" +
                 "      */\n" +
                 "    Test() {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETERS_DOCUMENTED_BUT_NO_CODE_PARAMETERS, 4, 9, 4, 14));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param i This describes the i parameter.\n" +
                 "      */\n" +
                 "    Test() {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETERS_DOCUMENTED_BUT_NO_CODE_PARAMETERS, 4, 9, 4, 14));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param i This describes the i parameter.\n" +
                 "      * @param j This describes the j parameter.\n" +
                 "      */\n" +
                 "    Test() {}\n" +
                 "}\n",
                 // should be only one error:
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETERS_DOCUMENTED_BUT_NO_CODE_PARAMETERS, 4, 9, 4, 14));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param i This describes the i parameter.\n" +
                 "      * @param j This describes the j parameter.\n" +
                 "      * @param k This describes the k parameter.\n" +
                 "      * @param l This describes the l parameter.\n" +
                 "      * @param m This describes the m parameter.\n" +
                 "      */\n" +
                 "    Test() {}\n" +
                 "}\n",
                 // should be only one error:
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETERS_DOCUMENTED_BUT_NO_CODE_PARAMETERS, 4, 9, 4, 14));
    }

    public void testCtorParametersParamWithoutTarget() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param\n" +
                 "      */\n" +
                 "    Test(int i) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_WITHOUT_NAME, 4, 9, 4, 14),
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_NOT_DOCUMENTED, 6, 14, 6, 14));
    }

    public void testCtorParametersParamMisspelled() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param st The string in question.\n" +
                 "      */\n" +
                 "    Test(String str) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_MISSPELLED, 4, 16, 4, 17));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param s The string in question.\n" +
                 "      */\n" +
                 "    Test(String str) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_MISSPELLED, 4, 16, 4, 16));
    }

    public void testCtorParametersParamNotDocumented() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param comp A Comparator of some sort.\n" +
                 "      */\n" +
                 "    Test(String str, Comparator comp) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_NOT_DOCUMENTED, 6, 17, 6, 19));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param price The price of tea in China.\n" +
                 "      */\n" +
                 "    Test(String str, int price, Comparator comp) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_NOT_DOCUMENTED, 6, 17, 6, 19),
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_NOT_DOCUMENTED, 6, 44, 6, 47));
    }

    public void testCtorParametersMisordered() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param comp A Comparator of some sort.\n" +
                 "      * @param str The string in question.\n" +
                 "      */\n" +
                 "    Test(String str, Comparator comp) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_NOT_IN_CODE_ORDER, 5, 16, 5, 18));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param size The height and width of something.\n" +
                 "      * @param comp A Comparator of some sort.\n" +
                 "      * @param str The string in question.\n" +
                 "      */\n" +
                 "    Test(java.awt.Dimension size, String str, Comparator comp) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_NOT_IN_CODE_ORDER, 6, 16, 6, 18));
    }

    public void testCtorParametersTypeUsed() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param String The string in question.\n" +
                 "      */\n" +
                 "    Test(String str) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_TYPE_USED, 4, 16, 4, 21));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @param String The string in question.\n" +
                 "      * @param Comparator A Comparator of some sort.\n" +
                 "      */\n" +
                 "    Test(String str, Comparator comp) {}\n" +
                 "}\n",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_TYPE_USED, 4, 16, 4, 21),
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_TYPE_USED, 5, 16, 5, 25));
    }

    public void testParamterNotInCode() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /**\n" +
                 "     * Calculate the bundles along the search path from the base bundle to the\n" +
                 "     * bundle specified by baseName and locale.\n" +
                 "     * @param baseName the base bundle name\n" +
                 "     * @param locale the locale\n" +
                 "     * @param names the vector used to return the names of the bundles along\n" +
                 "     * the search path.\n" +
                 "     *\n" +
                 "     */\n" +
                 "    private static Vector calculateBundleNames(String baseName, Locale locale) {\n" +
                 "    }\n" +
                 "\n" +
                 "}\n",
                 "1.5",
                 new Violation(ParameterDocAnalyzer.MSG_PARAMETER_NOT_IN_CODE, 8, 15, 8, 19));
    }
}
