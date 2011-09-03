package org.incava.doctorj;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import org.incava.analysis.Violation;


public class TestExceptionDocAnalyzer extends Tester
{
    public TestExceptionDocAnalyzer(String name) {
        super(name);
    }

    public void xtestMethodExceptionWithoutTag() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception\n" +
                 "      */\n" +
                 "    public void method() throws IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_WITHOUT_CLASS_NAME, 4,  9, 4, 18),
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_NOT_DOCUMENTED,     6, 33, 6, 43)
                 });
    }

    public void xtestMethodExceptionUndocumented() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception IOException\n" +
                 "      */\n" +
                 "    public void method() throws IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_WITHOUT_DESCRIPTION, 4, 20, 4, 30)
                 });
    }

    public void xtestMethodExceptionMisspelled() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception IOExceptoin thrown on I/O error.\n" +
                 "      */\n" +
                 "    public void method() throws IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_MISSPELLED, 4, 20, 4, 30)
                 });
    }
    
    public void xtestMethodExceptionAsNameMisspelled() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception IOExceptoin thrown on I/O error.\n" +
                 "      */\n" +
                 "    public void method() throws java.io.IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_MISSPELLED, 4, 20, 4, 30)
                 });
    }
    
    public void xtestMethodExceptionDocumentedButNotInCode() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception IOException thrown on I/O error.\n" +
                 "      */\n" +
                 "    public void method() {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_NOT_IN_THROWS_LIST, 4, 20, 4, 30)
                 });

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception FileNotFoundException thrown when there is no file\n" +
                 "      * @exception IOException thrown on I/O error.\n" +
                 "      */\n" +
                 "    public void method() throws FileNotFoundException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_NOT_IN_THROWS_LIST, 5, 20, 5, 30)
                 });
    }

    public void xtestMethodExceptionNotDocumented() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      */\n" +
                 "    public void method() throws IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_NOT_DOCUMENTED, 5, 33, 5, 43)
                 });

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception IOException thrown on I/O error.\n" +
                 "      */\n" +
                 "    public void method() throws java.io.IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                 });

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception java.io.IOException thrown on I/O error.\n" +
                 "      */\n" +
                 "    public void method() throws java.io.IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                 });

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception java.io.IOException thrown on I/O error.\n" +
                 "      */\n" +
                 "    public void method() throws IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void xtestMethodExceptionNotAlphabetical() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception NullPointerException thrown when there is no file\n" +
                 "      * @exception IOException thrown on I/O error.\n" +
                 "      */\n" +
                 "    public void method() throws NullPointerException, IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTIONS_NOT_ALPHABETICAL, 5, 20, 5, 30)
                 });

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception ArrayIndexOutOfBoundsException thrown when index >= array.length\n" +
                 "      * @exception NullPointerException thrown when there is no file\n" +
                 "      * @exception IOException thrown on I/O error.\n" +
                 "      */\n" +
                 "    public void method() throws IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTIONS_NOT_ALPHABETICAL, 6, 20, 6, 30)
                 });
    }
    
    public void xtestCtorExceptionWithoutTag() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception\n" +
                 "      */\n" +
                 "    public Test() throws IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_WITHOUT_CLASS_NAME, 4,  9, 4, 18),
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_NOT_DOCUMENTED,     6, 26, 6, 36)
                 });
    }

    public void xtestCtorExceptionUndocumented() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception IOException\n" +
                 "      */\n" +
                 "    public Test() throws IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_WITHOUT_DESCRIPTION, 4, 20, 4, 30)
                 });
    }

    public void xtestCtorExceptionMisspelled() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception IOExceptoin thrown on I/O error.\n" +
                 "      */\n" +
                 "    public Test() throws IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_MISSPELLED, 4, 20, 4, 30)
                 });
    }
    
    public void xtestCtorExceptionAsNameMisspelled() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception IOExceptoin thrown on I/O error.\n" +
                 "      */\n" +
                 "    public Test() throws java.io.IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_MISSPELLED, 4, 20, 4, 30)
                 });
    }
    
    public void xtestCtorExceptionDocumentedButNotInCode() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception IOException thrown on I/O error.\n" +
                 "      */\n" +
                 "    public Test() {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_NOT_IN_THROWS_LIST, 4, 20, 4, 30)
                 });

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception FileNotFoundException thrown when there is no file\n" +
                 "      * @exception IOException thrown on I/O error.\n" +
                 "      */\n" +
                 "    public Test() throws FileNotFoundException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_NOT_IN_THROWS_LIST, 5, 20, 5, 30)
                 });
    }

    public void xtestCtorExceptionNotDocumented() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      */\n" +
                 "    public Test() throws IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_NOT_DOCUMENTED, 5, 26, 5, 36)
                 });

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception IOException thrown on I/O error.\n" +
                 "      */\n" +
                 "    public Test() throws java.io.IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                 });

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception java.io.IOException thrown on I/O error.\n" +
                 "      */\n" +
                 "    public Test() throws java.io.IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                 });

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception java.io.IOException thrown on I/O error.\n" +
                 "      */\n" +
                 "    public Test() throws IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void xtestCtorExceptionNotAlphabetical() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception NullPointerException thrown when there is no file\n" +
                 "      * @exception IOException thrown on I/O error.\n" +
                 "      */\n" +
                 "    public Test() throws NullPointerException, IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTIONS_NOT_ALPHABETICAL, 5, 20, 5, 30)
                 });

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception ArrayIndexOutOfBoundsException thrown when index >= array.length\n" +
                 "      * @exception NullPointerException thrown when there is no file\n" +
                 "      * @exception IOException thrown on I/O error.\n" +
                 "      */\n" +
                 "    public Test() throws IOException {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTIONS_NOT_ALPHABETICAL, 6, 20, 6, 30)
                 });
    }

    public void xtestCtorExceptionRuntimeExceptionNotDeclared() {
        List<String> runtimeExceptions = new ArrayList<String>();
        runtimeExceptions.addAll(Arrays.asList(new String[] {
                                                   "java.awt.color.CMMException",
                                                   "java.awt.color.ProfileDataException",

                                                   "java.awt.geom.IllegalPathStateException",
        
                                                   "java.awt.image.ImagingOpException",
                                                   "java.awt.image.RasterFormatException",

                                                   "java.lang.ArithmeticException",
                                                   "java.lang.ArrayStoreException",
                                                   "java.lang.ClassCastException",
                                                   "java.lang.IllegalArgumentException",
                                                   "java.lang.IllegalMonitorStateException",
                                                   "java.lang.IllegalStateException",
                                                   "java.lang.IndexOutOfBoundsException",
                                                   "java.lang.NegativeArraySizeException",
                                                   "java.lang.NullPointerException",
                                                   "java.lang.SecurityException",
                                                   // "java.lang.TypeNotPresentException", 1.5 only
                                                   "java.lang.UnsupportedOperationException",

                                                   // "java.lang.annotation.AnnotationTypeMismatchException", 1.5 only
                                                   // "java.lang.annotation.IncompleteAnnotationException",   1.5 only
        
                                                   // "java.lang.reflect.MalformedParameterizedTypeException", 1.5 only
                                                   "java.lang.reflect.UndeclaredThrowableException",

                                                   "java.nio.BufferOverflowException",
                                                   "java.nio.BufferUnderflowException",

                                                   "java.security.ProviderException",

                                                   "java.util.ConcurrentModificationException",
                                                   "java.util.EmptyStackException",
                                                   "java.util.MissingResourceException",
                                                   "java.util.NoSuchElementException",
        
                                                   // "java.util.concurrent.RejectedExecutionException", 1.5

                                                   // "javax.management.JMRuntimeException",
        
                                                   "javax.print.attribute.UnmodifiableSetException",
        
                                                   "javax.swing.undo.CannotRedoException",
                                                   "javax.swing.undo.CannotUndoException",
        
                                                   "org.omg.CORBA.SystemException",
        
                                                   "org.w3c.dom.DOMException",
        
                                                   "org.w3c.dom.events.EventException",
                                                   
                                                   //"org.w3c.dom.ls.LSException", 1.5
                                               }));

        for (String rtexc : runtimeExceptions) {
            evaluate("/** This is a description.\n" +
                     "  */\n" +
                     "class Test {\n" +
                     "    /** This is a description.\n" +
                     "      * @exception " + rtexc + " This is a description.\n" +
                     "      */\n" +
                     "    public Test() {}\n" +
                     "}\n",
                     new Violation[] { 
                     });
        }
    }

    public void xtestFullNameNotImported() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception java.util.NoSuchElementException This is a description.\n" +
                 "      */\n" +
                 "    public Test() {}\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void xtestShortNameImportedSingle() {
        evaluate("import java.util.NoSuchElementException;\n" +
                 "/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception NoSuchElementException This is a description.\n" +
                 "      */\n" +
                 "    public Test() {}\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void xtestShortNameImportedOnDemand() {
        evaluate("import java.util.*;\n" +
                 "/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception NoSuchElementException This is a description.\n" +
                 "      */\n" +
                 "    public Test() {}\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void xtestShortNameImportedOnDemandJava1415() {
        evaluate("import org.apache.xml.utils.*;\n" +                  // 1.4
                 "import com.sun.org.apache.xml.internal.utils.*;\n" + // 1.5
                 "/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception WrappedRuntimeException This is a description.\n" +
                 "      */\n" +
                 "    public Test() {}\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void testUnknownExceptionReportOnce() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception org.incava.util.NoPointersInJavaException This is a description.\n" +
                 "      */\n" +
                 "    public void foo() {}\n" +
                 "\n" +
                 "    /** This is a description.\n" +
                 "      * @exception org.incava.util.NoPointersInJavaException This is a description.\n" +
                 "      */\n" +
                 "    public void bar() {}\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(ExceptionDocAnalyzer.MSG_EXCEPTION_NOT_DOCUMENTED, 5, 20, 5, 60)
                 });
    }

    public void testErrorShortName() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception LinkageError This is a description.\n" +
                 "      */\n" +
                 "    public void foo() {}\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void testErrorLongName() {
        evaluate("/** This is a description.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @exception java.lang.ExceptionInInitializerError This is a description.\n" +
                 "      */\n" +
                 "    public void foo() {}\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

}