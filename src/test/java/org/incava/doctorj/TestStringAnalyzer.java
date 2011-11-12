package org.incava.doctorj;

import java.util.Arrays;
import org.incava.analysis.Violation;
import org.incava.text.Lines;

public class TestStringAnalyzer extends AbstractDoctorJTestCase {
    public TestStringAnalyzer(String name) {
        super(name);
    }

    public void testBasic() {
        String comment = "/** Documentation that is sufficiently lengthy. */";
        evaluate(new Lines(comment,
                           "class Test {",
                           comment,
                           "    String s = \"wafle freis\";",
                           "}"),
                 spellingViolation(4, 17, "wafle", "waffle", "wale", "ale", "wafer", "waffled", "waffler"),
                 spellingViolation(4, 23, "freis", "reis", "fares", "fires", "fores", "fredi", "fredi's"));
    }

    public void testIncorrectString() {
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
                 spellingViolation(4, 17, "wafle", "waffle", "wale", "ale", "wafer", "waffled", "waffler"),
                 spellingViolation(4, 23, "freis", "reis", "fares", "fires", "fores", "fredi", "fredi's"),
                 spellingViolation(8, 17, "supperman", "superman", "superhuman", "superman's", "supermen", "superwoman", "supper"),
                 spellingViolation(8, 27, "mxyzptlk"));
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
        Violation rhodyn    = spellingViolation(4,  20, "rhodyn",    "rhody, brody, hod, hon, howdy, hoy");
        Violation drysmian  = spellingViolation(6,  28, "drysmian",  "adrian, dairyman, damian, dian, dorian, drying");
        Violation forhognis = spellingViolation(8,  28, "forhognis", "frogs");
        Violation rypan     = spellingViolation(10, 30, "rypan",     "ryan", "aryan", "bryan", "ran", "ryann", "arpa");
        Violation grymetian = spellingViolation(10, 43, "grymetian", "gentian", "grecian", "greta");

        evaluate(lines, new Options(),    drysmian, forhognis, rypan, grymetian);
        evaluate(lines, createOptions(0), rhodyn, drysmian, forhognis, rypan, grymetian);
        evaluate(lines, createOptions(1), rhodyn, drysmian, forhognis, rypan, grymetian);
        evaluate(lines, createOptions(2), drysmian, forhognis, rypan, grymetian);
        evaluate(lines, createOptions(3), forhognis, rypan, grymetian);
        evaluate(lines, createOptions(4), rypan, grymetian);
    }
}
