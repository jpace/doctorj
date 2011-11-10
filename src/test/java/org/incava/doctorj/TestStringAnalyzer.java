package org.incava.doctorj;

import java.util.Arrays;
import org.incava.analysis.Violation;
import org.incava.ijdk.lang.StringExt;
import org.incava.text.Lines;

public class TestStringAnalyzer extends AbstractDoctorJTestCase {
    static {
        SpellingAnalyzer.getInstance().addDictionary("/home/jpace/proj/doctorj/etc/words.en_US");
    }
    
    public TestStringAnalyzer(String name) {
        super(name);
    }

    public Violation violation(int line, int col, String word, String ... matches) {
        StringBuilder sb = new StringBuilder("Word '" + word + "' appears to be misspelled. ");
        if (matches.length > 0) {
            sb.append("Closest matches: ").append(StringExt.join(matches, ", "));
        }
        else {
            sb.append("No close matches");
        }
        return new Violation(sb.toString(), locrange(loc(line, col), loc(line, col + word.length() - 1)));
    }

    public void xtestIncorrectString() {
        String comment = "/** Documentation that is sufficiently lengthy. */";
        evaluate(new Lines(comment,
                           "class Test {",
                           comment,
                           "    String s = \"wafle freis\";",
                           comment,
                           "    int i = 4;",
                           comment,
                           "    String t = \"supperman mxyzptlk\";",
                           "}"),
                 violation(4, 17, "wafle", "waffle", "wale", "ale", "wafer", "waffled", "waffler"),
                 violation(4, 23, "freis", "reis", "fares", "fires", "fores", "fredi", "fredi's"),
                 violation(8, 17, "supperman", "superman", "superhuman", "superman's", "supermen", "superwoman", "supper"),
                 violation(8, 27, "mxyzptlk", "atches"));
    }

    public Options createOptions(int minWords) {
        Options opts = new Options();
        opts.process(Arrays.asList(new String[] { "--minwords=" + minWords }));
        return opts;
    }

    public void testMinimumWordLimit() {
        String comment = "/** Documentation that is sufficiently lengthy. */";
        Lines lines = new Lines(comment,
                                "class Test {",
                                "    " + comment,
                                "    String sOne = \"rhodyn\";",
                                "    " + comment,
                                "    String sTwo = \"gloomy. drysmian\";",
                                "    " + comment,
                                "    String sThree = \"moody forhognis, contempt.\";",
                                "    " + comment,
                                "    String sFour = \"plunder, rypan. rage, grymetian\";",
                                "}");
        Violation rhodyn    = violation(4,  20, "rhodyn",    "rhody, brody, hod, hon, howdy, hoy");
        Violation drysmian  = violation(6,  28, "drysmian",  "adrian, dairyman, damian, dian, dorian, drying");
        Violation forhognis = violation(8,  28, "forhognis", "frogs");
        Violation rypan     = violation(10, 30, "rypan",     "ryan", "aryan", "bryan", "ran", "ryann", "arpa");
        Violation grymetian = violation(10, 43, "grymetian", "gentian", "grecian", "greta");

        evaluate(lines, new Options(),    drysmian, forhognis, rypan, grymetian);
        evaluate(lines, createOptions(0), rhodyn, drysmian, forhognis, rypan, grymetian);
        evaluate(lines, createOptions(1), rhodyn, drysmian, forhognis, rypan, grymetian);
        evaluate(lines, createOptions(2), drysmian, forhognis, rypan, grymetian);
        evaluate(lines, createOptions(3), forhognis, rypan, grymetian);
        evaluate(lines, createOptions(4), rypan, grymetian);
    }
}
