package org.incava.doctorj;

import junit.framework.TestCase;
import org.incava.analysis.Violation;


public class TestInterfaceDocAnalyzer extends TestTypeDocAnalyzer
{
    public TestInterfaceDocAnalyzer(String name)
    {
        super(name);
    }

    public void testInterfaceAuthorWithText()
    {
        evaluate("/** This is a description.\n" +
                 "  * @author e. e. cummings\n" +
                 "  */\n" +
                 "interface TestInterfaceAuthorTag {\n" +
                 "}\n",
                 new Violation[] { 
                 });

        evaluate("/** This is a description.\n" +
                 "  * @author I\n" +
                 "  */\n" +
                 "interface TestInterfaceAuthorTag {\n" +
                 "}\n",
                 new Violation[] { 
                 });

        evaluate("/** This is a description.\n" +
                 "  * @author fred\n" +
                 "  */\n" +
                 "interface TestInterfaceAuthorTag {\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void testInterfaceAuthorWithoutText()
    {
        evaluate("/** This is a description.\n" +
                 "  * @author\n" +
                 "  */\n" +
                 "interface TestInterfaceAuthorTag {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_AUTHOR_WITHOUT_NAME, 2, 5, 2, 11)
                 });
    }
    
    public void testInterfaceAuthorWithoutTextSpaces()
    {
        evaluate("/** This is a description.\n" +
                 "  * @author   \n" +
                 "  */\n" +
                 "interface TestInterfaceAuthorTag {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_AUTHOR_WITHOUT_NAME, 2, 5, 2, 11)
                 });

        evaluate("/** This is a description.\n" +
                 "  * @author \n" +
                 "  */\n" +
                 "interface TestInterfaceAuthorTag {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_AUTHOR_WITHOUT_NAME, 2, 5, 2, 11)
                 });
    }
    

    public void testInterfaceVersionWithText()
    {
        evaluate("/** This is a description.\n" +
                 "  * @version 1.1.2\n" +
                 "  */\n" +
                 "interface TestInterfaceVersionTag {\n" +
                 "}\n",
                 new Violation[] { 
                 });

        evaluate("/** This is a description.\n" +
                 "  * @version 1\n" +
                 "  */\n" +
                 "interface TestInterfaceVersionTag {\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void testInterfaceVersionWithoutText()
    {
        evaluate("/** This is a description.\n" +
                 "  * @version\n" +
                 "  */\n" +
                 "interface TestInterfaceVersionTag {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_VERSION_WITHOUT_TEXT, 2, 5, 2, 12)
                 });
    }
    
    public void testInterfaceVersionWithoutTextSpaces()
    {
        evaluate("/** This is a description.\n" +
                 "  * @version   \n" +
                 "  */\n" +
                 "interface TestInterfaceVersionTag {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_VERSION_WITHOUT_TEXT, 2, 5, 2, 12)
                 });

        evaluate("/** This is a description.\n" +
                 "  * @version \n" +
                 "  */\n" +
                 "interface TestInterfaceVersionTag {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_VERSION_WITHOUT_TEXT, 2, 5, 2, 12)
                 });
    }


    public void testInterfaceSerialWithText()
    {
        evaluate("/** This is a description.\n" +
                 "  * @serial This describes the serial field.\n" +
                 "  */\n" +
                 "interface TestInterfaceSerialTag {\n" +
                 "}\n",
                 new Violation[] { 
                 });

        evaluate("/** This is a description.\n" +
                 "  * @serial description\n" +
                 "  */\n" +
                 "interface TestInterfaceSerialTag {\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void testInterfaceSerialWithoutText()
    {
        evaluate("/** This is a description.\n" +
                 "  * @serial\n" +
                 "  */\n" +
                 "interface TestInterfaceSerialTag {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_SERIAL_WITHOUT_TEXT, 2, 5, 2, 11)
                 });
    }
    
    public void testInterfaceSerialWithoutTextSpaces()
    {
        evaluate("/** This is a description.\n" +
                 "  * @serial   \n" +
                 "  */\n" +
                 "interface TestInterfaceSerialTag {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_SERIAL_WITHOUT_TEXT, 2, 5, 2, 11)
                 });

        evaluate("/** This is a description.\n" +
                 "  * @serial \n" +
                 "  */\n" +
                 "interface TestInterfaceSerialTag {\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(TypeDocAnalyzer.MSG_SERIAL_WITHOUT_TEXT, 2, 5, 2, 11)
                 });
    }

}
