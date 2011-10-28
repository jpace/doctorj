package org.incava.doctorj;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import org.incava.analysis.Violation;


public class TestItemDocAnalyzer extends AbstractDoctorJTestCase {

    static {
        ItemDocAnalyzer.addDictionary("/home/jpace/proj/doctorj/etc/words.en_US");
    }
    
    public TestItemDocAnalyzer(String name) {
        super(name);
    }

    // presence of Javadoc

    public void testDocumentedOuterConcreteNonPublicClass() {
        evaluate("class Test {\n" +
                 "}\n",
                 new Violation("Undocumented class", 1, 7, 1, 10));
    }

    public void testDocumentedOuterConcretePublicClass() {
        evaluate("public class TestHasJavadoc {\n" +
                 "}\n",
                 new Violation("Undocumented public class", 1, 14, 1, 27));
    }

    public void testDocumentedOuterAbstractNonPublicClass() {
        evaluate("abstract class TestHasJavadoc {\n" +
                 "}\n",
                 new Violation("Undocumented abstract class", 1, 16, 1, 29));
    }

    public void testDocumentedOuterAbstractPublicClass() {
        evaluate("public abstract class TestHasJavadoc {\n" +
                 "}\n",
                 new Violation("Undocumented public abstract class", 1, 23, 1, 36));
    }

    public void testDocumentedOuterNonPublicInterface() {
        evaluate("interface TestHasJavadoc {\n" +
                 "}\n",
                 new Violation("Undocumented interface", 1, 11, 1, 24));
    }

    public void testDocumentedOuterPublicInterface() {
        evaluate("public interface TestHasJavadoc {\n" +
                 "}\n",
                 new Violation("Undocumented public interface", 1, 18, 1, 31));
    }

    public void testDocumentedInnerConcreteNonPublicClass() {
        evaluate("/** this class is commented. */\n" +
                 "class Test {\n" +
                 "    class InnerTestHasJavadoc {\n" +
                 "    }\n" + 
                 "}\n",
                 new Violation("Undocumented class", 3, 11, 3, 29));
    }

    public void testDocumentedInnerConcretePublicClass() {
        evaluate("/** this class is commented. */\n" +
                 "class Test {\n" +
                 "    public class InnerTestHasJavadoc {\n" +
                 "    }\n" + 
                 "}\n",
                 new Violation("Undocumented public class", 3, 18, 3, 36));
    }

    public void testDocumentedInnerAbstractNonPublicClass() {
        evaluate("/** this class is commented. */\n" +
                 "class Test {\n" +
                 "    abstract class InnerTestHasJavadoc {\n" +
                 "    }\n" + 
                 "}\n",
                 new Violation("Undocumented abstract class", 3, 20, 3, 38));
    }

    public void testDocumentedInnerAbstractPublicClass() {
        evaluate("/** this class is commented. */\n" +
                 "class Test {\n" +
                 "    public abstract class InnerTestHasJavadoc {\n" +
                 "    }\n" + 
                 "}\n",
                 new Violation("Undocumented public abstract class", 3, 27, 3, 45));
    }

    public void testDocumentedInnerNonPublicInterface() {
        evaluate("/** this interface is commented. */\n" +
                 "interface TestHasJavadoc {\n" +
                 "    interface InnerTestHasJavadoc {\n" +
                 "    }\n" + 
                 "}\n",
                 new Violation("Undocumented interface", 3, 15, 3, 33));
    }

    public void testDocumentedInnerPublicInterface() {
        evaluate("/** this interface is commented. */\n" +
                 "interface TestHasJavadoc {\n" +
                 "    public interface InnerTestHasJavadoc {\n" +
                 "    }\n" + 
                 "}\n",
                 new Violation("Undocumented public interface", 3, 22, 3, 40));
    }

    public void testDocumentedNonPublicConstructor() {
        evaluate("/** this class is commented. */\n" +
                 "public class TestHasJavadoc {\n" +
                 "    Test() {} \n" +
                 "}\n",
                 new Violation("Undocumented constructor", 3, 5, 3, 8));
    }

    public void testDocumentedPublicConstructor() {
        evaluate("/** this class is commented. */\n" +
                 "public class TestHasJavadoc {\n" +
                 "    public TestHasJavadoc() {} \n" +
                 "}\n",
                 new Violation("Undocumented public constructor", 3, 12, 3, 25));
    }

    public void testDocumentedNonPublicMethod() {
        evaluate("/** this class is commented. */\n" +
                 "public class TestHasJavadoc {\n" +
                 "    void f() {} \n" +
                 "}\n",
                 new Violation("Undocumented method", 3, 10, 3, 10));
    }

    public void testDocumentedPublicMethod() {
        evaluate("/** this class is commented. */\n" +
                 "public class TestHasJavadoc {\n" +
                 "    public void f() {} \n" +
                 "}\n",
                 new Violation("Undocumented public method", 3, 17, 3, 17));
    }

    public void testDocumentedNonPublicField() {
        evaluate("/** this class is commented. */\n" +
                 "public class TestHasJavadoc {\n" +
                 "    String s;\n" +
                 "}\n",
                 new Violation("Undocumented field", 3, 12, 3, 12));
    }

    public void testDocumentedPublicField() {
        evaluate("/** this class is commented. */\n" +
                 "public class TestHasJavadoc {\n" +
                 "    public String s;\n" +
                 "}\n",
                 new Violation("Undocumented public field", 3, 19, 3, 19));
    }

    public void testDocumentedPublicFieldMultipleVariables() {
        evaluate("/** this class is commented. */\n" +
                 "public class TestHasJavadoc {\n" +
                 "    public String s, t, u, v = \"foo\";\n" +
                 "}\n",
                 new Violation("Undocumented public field", 3, 19, 3, 28));
    }

    // deprecated
    
    public void testDeprecatedClassWithText() {
        evaluate("/** This is a description.\n" +
                 "  * @deprecated Use something else.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n");
    }

    public void testDeprecatedClassWithoutText() {
        evaluate("/** This is a description.\n" +
                 "  * @deprecated\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_DEPRECATED_WITHOUT_TEXT, 2, 5, 2, 15));
    }
    
    public void testDeprecatedClassWithoutTextSpaces() {
        evaluate("/** This is a description.\n" +
                 "  * @deprecated   \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_DEPRECATED_WITHOUT_TEXT, 2, 5, 2, 15));
    }

    public void testDeprecatedInterfaceWithText() {
        evaluate("/** This is a description.\n" +
                 "  * @deprecated Use something else.\n" +
                 "  */\n" +
                 "interface TestDeprecatedTag {\n" +
                 "}\n");
    }

    public void testDeprecatedInterfaceWithoutText() {
        evaluate("/** This is a description.\n" +
                 "  * @deprecated\n" +
                 "  */\n" +
                 "interface TestDeprecatedTag {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_DEPRECATED_WITHOUT_TEXT, 2, 5, 2, 15));
    }
    
    public void testDeprecatedInterfaceWithoutTextSpaces() {
        evaluate("/** This is a description.\n" +
                 "  * @deprecated   \n" +
                 "  */\n" +
                 "interface TestDeprecatedTag {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_DEPRECATED_WITHOUT_TEXT, 2, 5, 2, 15));
    }

    public void testDeprecatedMethodWithText() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @deprecated Use something else.\n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n");
    }

    public void testDeprecatedMethodWithoutText() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @deprecated\n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_DEPRECATED_WITHOUT_TEXT, 5, 9, 5, 19));
    }
    
    public void testDeprecatedMethodWithoutTextSpaces() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @deprecated    \n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_DEPRECATED_WITHOUT_TEXT, 5, 9, 5, 19));
    }

    public void testDeprecatedFieldWithText() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @deprecated Use something else.\n" +
                 "      */\n" +
                 "    int f;\n" +
                 "}\n");
    }

    public void testDeprecatedFieldWithoutText() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @deprecated\n" +
                 "      */\n" +
                 "    int f;\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_DEPRECATED_WITHOUT_TEXT, 5, 9, 5, 19));
    }
    
    public void testDeprecatedFieldWithoutTextSpaces() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @deprecated    \n" +
                 "      */\n" +
                 "    int f;\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_DEPRECATED_WITHOUT_TEXT, 5, 9, 5, 19));
    }

    // see

    public void testSeeClassWithText() {
        evaluate("/** This is a description.\n" +
                 "  * @see something else\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n");

        evaluate("/** This is a description.\n" +
                 "  * @see elsewhere\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n");
    }

    public void testSeeClassWithoutText() {
        evaluate("/** This is a description.\n" +
                 "  * @see\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SEE_WITHOUT_REFERENCE, 2, 5, 2, 8));
    }
    
    public void testSeeClassWithoutTextSpaces() {
        evaluate("/** This is a description.\n" +
                 "  * @see   \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SEE_WITHOUT_REFERENCE, 2, 5, 2, 8));
    }

    public void testSeeInterfaceWithText() {
        evaluate("/** This is a description.\n" +
                 "  * @see something else\n" +
                 "  */\n" +
                 "interface TestSeeTag {\n" +
                 "}\n");

        evaluate("/** This is a description.\n" +
                 "  * @see elsewhere\n" +
                 "  */\n" +
                 "interface TestSeeTag {\n" +
                 "}\n");
    }

    public void testSeeInterfaceWithoutText() {
        evaluate("/** This is a description.\n" +
                 "  * @see\n" +
                 "  */\n" +
                 "interface TestSeeTag {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SEE_WITHOUT_REFERENCE, 2, 5, 2, 8));
    }
    
    public void testSeeInterfaceWithoutTextSpaces() {
        evaluate("/** This is a description.\n" +
                 "  * @see   \n" +
                 "  */\n" +
                 "interface TestSeeTag {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SEE_WITHOUT_REFERENCE, 2, 5, 2, 8));
    }

    public void testSeeMethodWithText() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @see something else\n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n");

        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @see elsewhere\n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n");
    }

    public void testSeeMethodWithoutText() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @see\n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SEE_WITHOUT_REFERENCE, 5, 9, 5, 12));
    }
    
    public void testSeeMethodWithoutTextSpaces() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @see    \n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SEE_WITHOUT_REFERENCE, 5, 9, 5, 12));
    }

    public void testSeeFieldWithText() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @see something else\n" +
                 "      */\n" +
                 "    int f;\n" +
                 "}\n");

        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @see elsewhere\n" +
                 "      */\n" +
                 "    int f;\n" +
                 "}\n");
    }

    public void testSeeFieldWithoutText() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @see\n" +
                 "      */\n" +
                 "    int f;\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SEE_WITHOUT_REFERENCE, 5, 9, 5, 12));
    }
    
    public void testSeeFieldWithoutTextSpaces() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @see    \n" +
                 "      */\n" +
                 "    int f;\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SEE_WITHOUT_REFERENCE, 5, 9, 5, 12));
    }

    // since

    public void testSinceClassWithText() {
        evaluate("/** This is a description.\n" +
                 "  * @since some version\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n");

        evaluate("/** This is a description.\n" +
                 "  * @since 3.17\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n");
    }

    public void testSinceClassWithoutText() {
        evaluate("/** This is a description.\n" +
                 "  * @since\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SINCE_WITHOUT_TEXT, 2, 5, 2, 10));
    }
    
    public void testSinceClassWithoutTextSpaces() {
        evaluate("/** This is a description.\n" +
                 "  * @since   \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SINCE_WITHOUT_TEXT, 2, 5, 2, 10));
    }

    public void testSinceInterfaceWithText() {
        evaluate("/** This is a description.\n" +
                 "  * @since some version\n" +
                 "  */\n" +
                 "interface SinceTestTag {\n" +
                 "}\n");

        evaluate("/** This is a description.\n" +
                 "  * @since 3.17\n" +
                 "  */\n" +
                 "interface SinceTestTag {\n" +
                 "}\n");
    }

    public void testSinceInterfaceWithoutText() {
        evaluate("/** This is a description.\n" +
                 "  * @since\n" +
                 "  */\n" +
                 "interface SinceTestTag {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SINCE_WITHOUT_TEXT, 2, 5, 2, 10));
    }
    
    public void testSinceInterfaceWithoutTextSpaces() {
        evaluate("/** This is a description.\n" +
                 "  * @since   \n" +
                 "  */\n" +
                 "interface SinceTestTag {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SINCE_WITHOUT_TEXT, 2, 5, 2, 10));
    }

    public void testSinceMethodWithText() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @since some version\n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n");

        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @since 3.17\n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n");
    }

    public void testSinceMethodWithoutText() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @since\n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SINCE_WITHOUT_TEXT, 5, 9, 5, 14));
    }
    
    public void testSinceMethodWithoutTextSpaces() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @since    \n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SINCE_WITHOUT_TEXT, 5, 9, 5, 14));
    }

    public void testSinceFieldWithText() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @since some version\n" +
                 "      */\n" +
                 "    int f;\n" +
                 "}\n");

        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @since 3.17\n" +
                 "      */\n" +
                 "    int f;\n" +
                 "}\n");
    }

    public void testSinceFieldWithoutText() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @since\n" +
                 "      */\n" +
                 "    int f;\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SINCE_WITHOUT_TEXT, 5, 9, 5, 14));
    }
    
    public void testSinceFieldWithoutTextSpaces() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @since    \n" +
                 "      */\n" +
                 "    int f;\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SINCE_WITHOUT_TEXT, 5, 9, 5, 14));
    }

    // tag order

    public void testNoTags() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "}\n");
    }

    public void testProperOrder() {
        evaluate("/**\n" +
                 "  * This is a description. \n" +
                 "  * @author me \n" +
                 "  * @version 0.1.2 \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n");


        evaluate("/**\n" +
                 "  * This is a description. \n" +
                 "  * @version 0.1.2 \n" +
                 "  * @since 0.1.1 \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n");

        JavadocTags.add(new JavadocTags.TagDescription("@todo", 10, true, true, true, true));
        evaluate("/**\n" +
                 "  * This is a description. \n" +
                 "  * @version 0.1.2 \n" +
                 "  * @since 0.1.1 \n" +
                 "  * @todo fix exception when index < 0 \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n");
    }

    public void testImproperOrder() {
        evaluate("/**\n" +
                 "  * This is a description. \n" +
                 "  * @version 0.1.2 \n" +
                 "  * @author me \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_TAG_IMPROPER_ORDER, 4, 5, 4, 11));

        evaluate("/**\n" +
                 "  * This is a description. \n" +
                 "  * @since 0.1.2 \n" +
                 "  * @author me \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_TAG_IMPROPER_ORDER, 4, 5, 4, 11));

        evaluate("/**\n" +
                 "  * This is a description. \n" +
                 "  * @author me \n" +
                 "  * @deprecated stop using this class \n" +
                 "  * @since 0.1.2 \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_TAG_IMPROPER_ORDER, 5, 5, 5, 10));
    }

    // tag validity

    public void testValidityClassTags() {
        evaluate("/** This is a description.\n" +
                 "  * @author me\n" +
                 "  * @version 1.10\n" +
                 "  * @see Spot#run() \n" +
                 "  * @since whenever\n" +
                 "  * @deprecated Use something better than this.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n");

        evaluate("/** This is a description.\n" +
                 "  * @author me\n" +
                 "  * @version 1.10\n" +
                 "  * @throws NullPointerException Although Java doesn't have pointers.\n" +
                 "  * @see Spot#run() \n" +
                 "  * @since whenever\n" +
                 "  * @deprecated Use something better than this.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation("Tag not valid for class", 4, 5, 4, 11));
    }

    public void testValidityInnerClassTags() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @author me\n" +
                 "      * @version 1.10\n" +
                 "      * @see Spot#run() \n" +
                 "      * @since whenever\n" +
                 "      * @deprecated Use something better than this.\n" +
                 "      */\n" +
                 "    class Inner {\n" +
                 "    }\n" +
                 "}\n");

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @author me\n" +
                 "      * @version 1.10\n" +
                 "      * @throws NullPointerException Although Java doesn't have pointers.\n" +
                 "      * @see Spot#run() \n" +
                 "      * @since whenever\n" +
                 "      * @deprecated Use something better than this.\n" +
                 "      */\n" +
                 "    class Inner {\n" +
                 "    }\n" +
                 "}\n",
                 new Violation("Tag not valid for class", 6, 9, 6, 15));
    }

    public void testValidityInterfaceTags() {
        evaluate("/** This is a description.\n" +
                 "  * @author me\n" +
                 "  * @version 1.10\n" +
                 "  * @see Spot#run() \n" +
                 "  * @since whenever\n" +
                 "  * @deprecated Use something better than this.\n" +
                 "  */\n" +
                 "interface TestValidTag {\n" +
                 "}\n");

        evaluate("/** This is a description.\n" +
                 "  * @author me\n" +
                 "  * @version 1.10\n" +
                 "  * @throws NullPointerException Impossible because Java doesn't have pointers.\n" +
                 "  * @see Spot#run() \n" +
                 "  * @since whenever\n" +
                 "  * @deprecated Use something better than this.\n" +
                 "  */\n" +
                 "interface TestValidTag {\n" +
                 "}\n",
                 new Violation("Tag not valid for interface", 4, 5, 4, 11));
    }

    public void testValidityInnerInterfaceTags() {
        evaluate("/** This is a description. */\n" +
                 "interface Outer {\n" +
                 "    /** This is a description.\n" +
                 "      * @author me\n" +
                 "      * @version 1.10\n" +
                 "      * @see Spot#run() \n" +
                 "      * @since whenever\n" +
                 "      * @deprecated Use something better than this.\n" +
                 "      */\n" +
                 "    interface Inner {\n" +
                 "    }\n" +
                 "}\n");

        evaluate("/** This is a description. */\n" +
                 "interface Outer {\n" +
                 "    /** This is a description.\n" +
                 "      * @author me\n" +
                 "      * @version 1.10\n" +
                 "      * @throws NullPointerException Although Java doesn't have pointers.\n" +
                 "      * @see Spot#run() \n" +
                 "      * @since whenever\n" +
                 "      * @deprecated Use something better than this.\n" +
                 "      */\n" +
                 "    interface Inner {\n" +
                 "    }\n" +
                 "}\n",
                 new Violation("Tag not valid for interface", 6, 9, 6, 15));
    }

    public void testValidityMethodTags() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @see Spot#run() \n" +
                 "      * @since whenever\n" +
                 "      * @deprecated Use something better than this.\n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n");

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @author me\n" +
                 "      * @version 1.10\n" +
                 "      * @see Spot#run() \n" +
                 "      * @since whenever\n" +
                 "      * @deprecated Use something better than this.\n" +
                 "      */\n" +
                 "    void f() {}\n" +
                 "}\n",
                 new Violation("Tag not valid for method", 4, 9, 4, 15),
                 new Violation("Tag not valid for method", 5, 9, 5, 16));
    }

    public void testValidityCtorTags() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @see Spot#run() \n" +
                 "      * @since whenever\n" +
                 "      * @deprecated Use something better than this.\n" +
                 "      */\n" +
                 "    Test() {}\n" +
                 "}\n");

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @author me\n" +
                 "      * @version 1.10\n" +
                 "      * @see Spot#run() \n" +
                 "      * @since whenever\n" +
                 "      * @deprecated Use something better than this.\n" +
                 "      */\n" +
                 "    Test() {}\n" +
                 "}\n",
                 new Violation("Tag not valid for constructor", 4, 9, 4, 15),
                 new Violation("Tag not valid for constructor", 5, 9, 5, 16));

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @author me\n" +
                 "      * @version 1.10\n" +
                 "      * @return Something\n" +
                 "      * @see Spot#run() \n" +
                 "      * @since whenever\n" +
                 "      * @deprecated Use something better than this.\n" +
                 "      */\n" +
                 "    Test() {}\n" +
                 "}\n",
                 new Violation("Tag not valid for constructor", 4, 9, 4, 15),
                 new Violation("Tag not valid for constructor", 5, 9, 5, 16),
                 new Violation("Tag not valid for constructor", 6, 9, 6, 15));
    }

    public void testValidityFieldTags() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @see Spot#run() \n" +
                 "      * @since whenever\n" +
                 "      * @deprecated Use something better than this.\n" +
                 "      */\n" +
                 "    int f;\n" +
                 "}\n");

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @author me\n" +
                 "      * @version 1.10\n" +
                 "      * @see Spot#run() \n" +
                 "      * @since whenever\n" +
                 "      * @deprecated Use something better than this.\n" +
                 "      */\n" +
                 "    int f;\n" +
                 "}\n",
                 new Violation("Tag not valid for field", 4, 9, 4, 15),
                 new Violation("Tag not valid for field", 5, 9, 5, 16));
    }

    // summary sentence length

    public void testSummarySufficientLength() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n");
    }
    
    public void testSummaryNoSummarySentence() {
        evaluate("/** \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_NO_SUMMARY_SENTENCE, 1, 1, 2, 4));
    }
    
    public void testSummaryNoEndingPeriod() {
        evaluate("/** \n" +
                 "  * This doesn't have an ending period\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SUMMARY_SENTENCE_DOES_NOT_END_WITH_PERIOD, 2, 5, 2, 38));
    }
    
    public void testSummaryShortSentence() {
        evaluate("/** \n" +
                 "  * Too short.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SUMMARY_SENTENCE_TOO_SHORT, 2, 5, 2, 14));

        evaluate("/** \n" +
                 "  * This one too.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SUMMARY_SENTENCE_TOO_SHORT, 2, 5, 2, 17));

        evaluate("/** \n" +
                 "  * This one too. And it has more text that follows,\n" +
                 "  * but should not be marked in the offending sentence.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(ItemDocAnalyzer.MSG_SUMMARY_SENTENCE_TOO_SHORT, 2, 5, 2, 17));
    }

    // spelling

    public void xtestSpellingOK() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n");
    }

    public void xtestSpellingOneMistake() {
        tr.Ace.setVerbose(true);
        
        final String msg = ("Word 'descriptino' appears to be misspelled. " +
                            "Closest matches: description, descriptions, descriptor, description's, descriptive, descriptors");
        evaluate("/** This is a descriptino.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(msg, 1, 15, 1, 25));
    }

    public void xtestSpellingTwoMistakes() {
        final String msg0 = ("Word 'exampel' appears to be misspelled. " +
                             "Closest matches: example, expel, enamel, exam, examen, example");

        final String msg1 = ("Word 'badd' appears to be misspelled. " +
                             "Closest matches: bad, baddy, ba, ba, badder, baddie");

        evaluate("/** This is an exampel of\n" +
                 "  * badd spelling.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation(msg0, 1, 16, 1, 22),
                 new Violation(msg1, 2,  5, 2,  8));
    }

    public void xtestSpellingEgregiousMistake() {
        evaluate("/** This is an egreejish misspelling.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation("Word 'egreejish' appears to be misspelled. No close matches", 1, 16, 1, 24));
    }

}
