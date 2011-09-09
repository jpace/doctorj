package org.incava.doctorj;

import junit.framework.TestCase;
import org.incava.analysis.Violation;


public class TestFieldDocAnalyzer extends AbstractDoctorJTestCase {

    public TestFieldDocAnalyzer(String name) {
        super(name);
    } 

    public void testSerialFieldOK() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @serialField one int What one is." +
                 "      */\n" +
                 "    ObjectStreamField[] serialPersistentFields = { \n" +
                 "        new ObjectStreamField(\"one\",  Integer.TYPE) \n" +
                 "    };\n" +
                 "}\n",
                 new Violation[] { 
                 });
    }

    public void testSerialFieldNoDoc() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @serialField" +
                 "      */\n" +
                 "    ObjectStreamField[] serialPersistentFields = { \n" +
                 "        new ObjectStreamField(\"one\",  Integer.TYPE) \n" +
                 "    };\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(FieldDocAnalyzer.MSG_SERIALFIELD_WITHOUT_NAME_TYPE_AND_DESCRIPTION, 4, 9, 4, 20)
                 });

        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @serialField  " +
                 "      */\n" +
                 "    ObjectStreamField[] serialPersistentFields = { \n" +
                 "        new ObjectStreamField(\"one\",  Integer.TYPE) \n" +
                 "    };\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(FieldDocAnalyzer.MSG_SERIALFIELD_WITHOUT_NAME_TYPE_AND_DESCRIPTION, 4, 9, 4, 20)
                 });
    }

    public void testSerialFieldNoDescription() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @serialField one int " +
                 "      */\n" +
                 "    ObjectStreamField[] serialPersistentFields = { \n" +
                 "        new ObjectStreamField(\"one\",  Integer.TYPE) \n" +
                 "    };\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(FieldDocAnalyzer.MSG_SERIALFIELD_WITHOUT_DESCRIPTION, 4, 22, 4, 29)
                 });
    }

    public void testSerialFieldNoTypeNorDescription() {
        evaluate("/** This is a description. */\n" +
                 "class Test {\n" +
                 "    /** This is a description.\n" +
                 "      * @serialField one " +
                 "      */\n" +
                 "    ObjectStreamField[] serialPersistentFields = { \n" +
                 "        new ObjectStreamField(\"one\",  Integer.TYPE) \n" +
                 "    };\n" +
                 "}\n",
                 new Violation[] { 
                     new Violation(FieldDocAnalyzer.MSG_SERIALFIELD_WITHOUT_TYPE_AND_DESCRIPTION, 4, 22, 4, 25)
                 });
    }
    
}
