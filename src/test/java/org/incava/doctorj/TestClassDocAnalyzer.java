package org.incava.doctorj;

import junit.framework.TestCase;
import org.incava.analysis.Violation;


public class TestClassDocAnalyzer extends TestTypeDocAnalyzer
{
    public TestClassDocAnalyzer(String name) {
        super(name);
    }

    public void testClassAuthorWithText() {
        evaluate("/** This is a description.\n" +
                 "  * @author e. e. cummings\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] { 
                 });

        evaluate("/** This is a description.\n" +
                 "  * @author I\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] { 
                 });

        evaluate("/** This is a description.\n" +
                 "  * @author fred\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void testClassAuthorWithoutText() {
        evaluate("/** This is a description.\n" +
                 "  * @author\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] {
                     new Violation(TypeDocAnalyzer.MSG_AUTHOR_WITHOUT_NAME, 2, 5, 2, 11)
                 });
    }
    
    public void testClassAuthorWithoutTextSpaces() {
        evaluate("/** This is a description.\n" +
                 "  * @author   \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_AUTHOR_WITHOUT_NAME, 2, 5, 2, 11)
                 });

        evaluate("/** This is a description.\n" +
                 "  * @author \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_AUTHOR_WITHOUT_NAME, 2, 5, 2, 11)
                 });
    }

    public void testClassVersionWithText() {
        evaluate("/** This is a description.\n" +
                 "  * @version 1.1.2\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] { 
                 });

        evaluate("/** This is a description.\n" +
                 "  * @version 1\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void testClassVersionWithoutText() {
        evaluate("/** This is a description.\n" +
                 "  * @version\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_VERSION_WITHOUT_TEXT, 2, 5, 2, 12)
                 });
    }
    
    public void testClassVersionWithoutTextSpaces() {
        evaluate("/** This is a description.\n" +
                 "  * @version   \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_VERSION_WITHOUT_TEXT, 2, 5, 2, 12)
                 });

        evaluate("/** This is a description.\n" +
                 "  * @version \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_VERSION_WITHOUT_TEXT, 2, 5, 2, 12)
                 });
    }

    public void testClassSerialWithText() {
        evaluate("/** This is a description.\n" +
                 "  * @serial This describes the serial field.\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] { 
                 });

        evaluate("/** This is a description.\n" +
                 "  * @serial description\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void testClassSerialWithoutText() {
        evaluate("/** This is a description.\n" +
                 "  * @serial\n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_SERIAL_WITHOUT_TEXT, 2, 5, 2, 11)
                 });
    }
    
    public void testClassSerialWithoutTextSpaces() {
        evaluate("/** This is a description.\n" +
                 "  * @serial   \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_SERIAL_WITHOUT_TEXT, 2, 5, 2, 11)
                 });

        evaluate("/** This is a description.\n" +
                 "  * @serial \n" +
                 "  */\n" +
                 "class Test {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_SERIAL_WITHOUT_TEXT, 2, 5, 2, 11)
                 });
    }

}
