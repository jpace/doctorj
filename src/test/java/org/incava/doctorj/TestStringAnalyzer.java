package org.incava.doctorj;

import org.incava.analysis.Violation;


public class TestStringAnalyzer extends AbstractDoctorJTestCase {
    
    static {
        SpellingAnalyzer.getInstance().addDictionary("/home/jpace/proj/doctorj/etc/words.en_US");
    }
    
    public TestStringAnalyzer(String name) {
        super(name);

        tr.Ace.setVerbose(true);
    }

    public void testIncorrectString() {
        //$$$ test with one close match, none, many 

        evaluate(new Lines("/** Class documentation that is sufficiently lengthy. */",
                           "class Test {",
                           "    /** Field documentation that is sufficiently lengthy. */",
                           "    String s = \"wafle freis\";",
                           "    /** Field documentation that is sufficiently lengthy. */",
                           "    int i = 4;",
                           "    /** Field documentation that is sufficiently lengthy. */",
                           "    String t = \"supperman mxyzptlk\";",
                           "}"),
                 new Violation("Word 'wafle' appears to be misspelled. Closest matches: waffle, wale, ale, wafer, waffled, waffler", 4, 17, 4, 21),
                 new Violation("Word 'freis' appears to be misspelled. Closest matches: reis, fares, fires, fores, fredi, fredi's", 4, 23, 4, 27),
                 new Violation("Word 'supperman' appears to be misspelled. Closest matches: superman, superhuman, superman's, supermen, superwoman, supper", 8, 17, 8, 25),
                 new Violation("Word 'mxyzptlk' appears to be misspelled. No close matches", 8, 27, 8, 34));        
    }

}
