package org.incava.doctorj;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.Analyzer;
import org.incava.ijdk.lang.StringExt;
import org.incava.ijdk.util.MultiMap;
import org.incava.javadoc.*;
import org.incava.text.LineMapping;
import org.incava.text.Location;
import org.incava.text.spell.*;


public class SpellingAnalyzer extends ParsingSpellChecker {

    private static SpellingAnalyzer instance = new SpellingAnalyzer();

    public static SpellingAnalyzer getInstance() {
        return instance;
    }
    
    public final static int NUM_CLOSEST_MATCHES = 6;

    private Analyzer analyzer;

    private JavadocElement desc;

    private LineMapping lines;

    public SpellingAnalyzer() {
        super(new SpellChecker(SpellChecker.CaseType.CASE_INSENSITIVE));
    }

    public void check(Analyzer analyzer, JavadocElement desc) {
        this.analyzer = analyzer;
        this.desc = desc;
        this.lines = null;
        
        super.check(this.desc.text);
    }
    
    protected String makeMessage(String word, MultiMap<Integer, String> nearMatches) {
        StringBuilder sb = new StringBuilder("Word '" + word + "' appears to be misspelled. ");
        if (nearMatches.size() == 0) {
            sb.append("No close matches");
        }
        else {
            sb.append("Closest matches: ");

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

            sb.append(StringExt.join(msgWords, ", "));
        }

        return sb.toString();
    }

    protected void wordMisspelled(String word, int position, MultiMap<Integer, String> nearMatches) {
        tr.Ace.log("word", word);

        if (this.desc == null) {
            tr.Ace.stack(tr.Ace.RED, "this.desc", this.desc);
            return;
        }

        if (this.lines == null) {
            this.lines = new LineMapping(this.desc.text, this.desc.start.line, this.desc.start.column);
        }
        
        Location start = this.lines.getLocation(position);
        Location end = this.lines.getLocation(position + word.length() - 1);
        String   msg = makeMessage(word, nearMatches);
        
        this.analyzer.addViolation(msg, start, end);
    }
}
