package org.incava.analysis;

import java.io.StringWriter;
import junit.framework.TestCase;
import org.incava.qualog.ANSIColor;

public class TestContextReport extends TestCase {
    private final static String EOLN = System.getProperty("line.separator");

    public TestContextReport(String name) {
        super(name);
    }

    public void testReportOrder() {
        String content = ("As fast as thou shalt wane, so fast thou grow'st\n" +
                          "In one of thine, from that which thou departest;\n" +
                          "And that fresh blood which youngly thou bestow'st,\r\n" +
                          "Thou mayst call thine when thou from youth convertest.\n" +
                          "Herein lives wisdom, beauty, and increase;\n" +
                          "Without this folly, age, and cold decay:\r" +
                          "If all were minded so, the times should cease\r" +
                          "And threescore year would make the world away.\n" +
                          "Let those whom nature hath not made for store,\n" +
                          "Harsh, featureless, and rude, barrenly perish:\n" +
                          "Look whom she best endow'd, she gave the more;\n" +
                          "Which bounteous gift thou shouldst in bounty cherish:\n" +
                          "She carv'd thee for her seal, and meant thereby,\n" +
                          "Thou shouldst print more, not let that copy die.\n");
        StringWriter sw = new StringWriter();
        Report r  = new ContextReport(sw, content);
        
        r.addViolation(new Violation("msg",  3, 5, 4, 6));
        r.addViolation(new Violation("msg2", 5, 3, 6, 4));
        assertEquals(2, r.getViolations().size());
        r.flush();
        
        String str0 = sw.toString();

        sw = new StringWriter();
        r  = new ContextReport(sw, content);
        r.addViolation(new Violation("msg2", 5, 3, 6, 4));
        r.addViolation(new Violation("msg",  3, 5, 4, 6));
        assertEquals(2, r.getViolations().size());
        r.flush();
        
        String str1 = sw.toString();

        assertEquals("order of reported violations", str0, str1);
    }

    public void testSinglePointOutput() {
        String content = ("As fast as thou shalt wane, so fast thou grow'st\n" +
                          "In one of thine, from that which thou departest;\n" +
                          "And that fresh blood which youngly thou bestow'st,\r\n" +
                          "Thou mayst call thine when thou from youth convertest.\n" +
                          "Herein lives wisdom, beauty, and increase;\n" +
                          "Without this folly, age, and cold decay:\r" +
                          "If all were minded so, the times should cease\r" +
                          "And threescore year would make the world away.\n" +
                          "Let those whom nature hath not made for store,\n" +
                          "Harsh, featureless, and rude, barrenly perish:\n" +
                          "Look whom she best endow'd, she gave the more;\n" +
                          "Which bounteous gift thou shouldst in bounty cherish:\n" +
                          "She carv'd thee for her seal, and meant thereby,\n" +
                          "Thou shouldst print more, not let that copy die.\n");
        StringWriter sw = new StringWriter();
        Report r = new ContextReport(sw, content);
        r.addViolation(new Violation("msg",  5, 9, 5, 9));
        assertEquals(1, r.getViolations().size());
        r.flush();
        String str = sw.toString();
        
        // end of line gets "fixed" to whatever is appropriate for this system.
        assertEquals("In " + ANSIColor.BOLD + ANSIColor.REVERSE + "-" + ANSIColor.RESET + ":" + EOLN + EOLN +
                     "     5. Herein lives wisdom, beauty, and increase;" + EOLN +
                     "                ^" + EOLN +
                     "*** msg" + EOLN +
                     EOLN,
                     str);
    }

    public void testSingleLineOutput() {
        String content = ("How heavy do I journey on the way,\n" +
                          "When what I seek, my weary travel's end,\n" +
                          "Doth teach that ease and that repose to say,\r" +
                          "'Thus far the miles are measured from thy friend!'\n" +
                          "The beast that bears me, tired with my woe,\r" +
                          "Plods dully on, to bear that weight in me,\r\n" +
                          "As if by some instinct the wretch did know\r" +
                          "His rider lov'd not speed being made from thee.\n" +
                          "The bloody spur cannot provoke him on,\n" +
                          "That sometimes anger thrusts into his hide,\n" +
                          "Which heavily he answers with a groan,\n" +
                          "More sharp to me than spurring to his side;\n" +
                          "For that same groan doth put this in my mind,\n" +
                          "My grief lies onward, and my joy behind.\n");
        
        StringWriter sw = new StringWriter();
        Report r = new ContextReport(sw, content);
        r.addViolation(new Violation("msg",  10, 16, 10, 20));
        assertEquals(1, r.getViolations().size());
        r.flush();
        String str = sw.toString();
        
        // end of line gets "fixed" to whatever is appropriate for this system.
        assertEquals("In " + ANSIColor.BOLD + ANSIColor.REVERSE + "-" + ANSIColor.RESET + ":" + EOLN + EOLN +
                     "    10. That sometimes anger thrusts into his hide," + EOLN +
                     "                       <--->" + EOLN +
                     "*** msg" + EOLN +
                     EOLN,
                     str);
    }

    public void testMultiLineOutput() {
        String content = ("Against that time, if ever that time come,\n" +
                          "When I shall see thee frown on my defects,\r\n" +
                          "When as thy love hath cast his utmost sum,\n" +
                          "Called to that audit by advis'd respects;\r" +
                          "Against that time when thou shalt strangely pass,\n" +
                          "And scarcely greet me with that sun, thine eye,\n" +
                          "When love, converted from the thing it was,\n" +
                          "Shall reasons find of settled gravity;\n" +
                          "Against that time do I ensconce me here,\n" +
                          "Within the knowledge of mine own desert,\n" +
                          "And this my hand, against my self uprear,\n" +
                          "To guard the lawful reasons on thy part: \n" +
                          "To leave poor me thou hast the strength of laws,\n" +
                          "Since why to love I can allege no cause.\n");
        
        StringWriter sw = new StringWriter();
        Report r = new ContextReport(sw, content);
        r.addViolation(new Violation("msg",  2, 23, 8, 13));
        assertEquals(1, r.getViolations().size());
        r.flush();
        String str = sw.toString();
        
        // end of line gets "fixed" to whatever is appropriate for this system.
        assertEquals("In " + ANSIColor.BOLD + ANSIColor.REVERSE + "-" + ANSIColor.RESET + ":" + EOLN + EOLN +
                     "                              <-------------------" + EOLN +
                     "     2. When I shall see thee frown on my defects," + EOLN +
                     "     3. When as thy love hath cast his utmost sum," + EOLN +
                     "     4. Called to that audit by advis'd respects;" + EOLN +
                     "     5. Against that time when thou shalt strangely pass," + EOLN +
                     "     6. And scarcely greet me with that sun, thine eye," + EOLN +
                     "     7. When love, converted from the thing it was," + EOLN +
                     "     8. Shall reasons find of settled gravity;" + EOLN +
                     "        ------------>" + EOLN +
                     "*** msg" + EOLN +
                     EOLN,
                     str);
    }

    public void testSingleLineWithTabs() {
        String content = ("\tMine eye hath play'd the painter and hath steel'd,\n" +
                          "\tThy beauty's form in table of my heart;\n" +
                          "\tMy body is the frame wherein 'tis held,\n" +
                          "\tAnd perspective it is best painter's art.\r" +
                          "\tFor through the painter must you see his skill,\n" +
                          "\tTo find where your true image pictur'd lies,\n" +
                          "\tWhich in my bosom's shop is hanging still,\r" +
                          "\tThat hath his windows glazed with thine eyes.\n" +
                          "\tNow see what good turns eyes for eyes have done:\n" +
                          "\tMine eyes have drawn thy shape, and thine for me\r\n" +
                          "\tAre windows to my breast, where-through the sun\n" +
                          "\tDelights to peep, to gaze therein on thee; \n" +
                          "\tYet eyes this cunning want to grace their art,\n" +
                          "\tThey draw but what they see, know not the heart.\n");
        StringWriter sw = new StringWriter();
        Report r = new ContextReport(sw, content);
        r.addViolation(new Violation("msg",  2, 23, 2, 25));
        assertEquals(1, r.getViolations().size());
        r.flush();
        String str = sw.toString();
        
        assertEquals("In " + ANSIColor.BOLD + ANSIColor.REVERSE + "-" + ANSIColor.RESET + ":" + EOLN + EOLN +
                     "     2. \tThy beauty's form in table of my heart;" + EOLN +
                     "                              <->" + EOLN +
                     "*** msg" + EOLN +
                     EOLN,
                     str);
    }
}
