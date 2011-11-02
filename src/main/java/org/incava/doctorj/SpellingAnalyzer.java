package org.incava.doctorj;

import java.text.MessageFormat;
import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.Analyzer;
import org.incava.ijdk.lang.StringExt;
import org.incava.ijdk.util.MultiMap;
import org.incava.text.LineMapping;
import org.incava.text.Location;
import org.incava.text.spell.*;


public class SpellingAnalyzer extends ParsingSpellChecker {

    public final static String MSG_MISSPELLED_NO_MATCHES = "Word ''{0}'' appears to be misspelled. No close matches";
    
    public final static String MSG_MISSPELLED_MATCHES = "Word ''{0}'' appears to be misspelled. Closest matches: {1}";
    
    private static SpellingAnalyzer instance = new SpellingAnalyzer();

    public static SpellingAnalyzer getInstance() {
        return instance;
    }
    
    public final static int NUM_CLOSEST_MATCHES = 6;

    private Analyzer analyzer;

    private LineMapping lines;

    public SpellingAnalyzer() {
        super(new SpellChecker(SpellChecker.CaseType.CASE_INSENSITIVE));
    }

    public void check(Analyzer analyzer, LineMapping lines, String text) {
        this.analyzer = analyzer;
        this.lines = lines;
        
        tr.Ace.log("lines", lines);
        tr.Ace.log("text", text);
        
        super.check(text);
    }
    
    protected String makeMessage(String word, MultiMap<Integer, String> nearMatches) {
        if (nearMatches.size() == 0) {
            return MessageFormat.format(MSG_MISSPELLED_NO_MATCHES, word);
        }
        else {
            List<String> msgWords = new ArrayList<String>();
            Set<Integer> eds = new TreeSet<Integer>(nearMatches.keySet());
            for (Integer ed : eds) {
                List<String> matches = new ArrayList<String>(new TreeSet<String>(nearMatches.get(ed)));
                int maxNewWords = NUM_CLOSEST_MATCHES - msgWords.size();
                if (maxNewWords <= 0) {
                    break;
                }

                int nNew = Math.min(maxNewWords, matches.size());
                msgWords.addAll(matches.subList(0, nNew));
            }

            return MessageFormat.format(MSG_MISSPELLED_MATCHES, word, StringExt.join(msgWords, ", "));
        }
    }

    protected void wordMisspelled(String word, int position, MultiMap<Integer, String> nearMatches) {
        tr.Ace.red("word", word);
        tr.Ace.red("nearMatches", nearMatches);
        
        Location start = this.lines.getLocation(position);
        Location end = this.lines.getLocation(position + word.length() - 1);
        String   msg = makeMessage(word, nearMatches);
        
        this.analyzer.addViolation(msg, start, end);
    }
}
