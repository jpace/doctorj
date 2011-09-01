package org.incava.doctorj;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.ijdk.lang.StringExt;
import org.incava.javadoc.*;
import org.incava.text.LineMapping;
import org.incava.text.Location;
import org.incava.ijdk.util.MultiMap;


public class ItemCommentSpellCheck extends CommentSpellCheck {

    public final static int NUM_CLOSEST_MATCHES = 6;

    private ItemDocAnalyzer analyzer;

    private JavadocElement desc;

    private LineMapping lines;

    public void check(ItemDocAnalyzer analyzer, JavadocElement desc) {
        this.analyzer = analyzer;
        this.desc = desc;
        this.lines = null;
        
        super.check(this.desc.text);
    }
    
    protected String makeMessage(String word, MultiMap<Integer, String> nearMatches) {
        StringBuffer buf = new StringBuffer("Word '" + word + "' appears to be misspelled. ");
        if (nearMatches.size() == 0) {
            buf.append("No close matches");
        }
        else {
            buf.append("Closest matches: ");
            
            Iterator<Collection<String>> it = nearMatches.values().iterator();
            List<String> msgWords = new ArrayList<String>();
            
            while (it.hasNext() && msgWords.size() < NUM_CLOSEST_MATCHES) {
                Collection<String> matches = it.next();
                Iterator<String> mit = matches.iterator();
                
                while (mit.hasNext() && msgWords.size() < NUM_CLOSEST_MATCHES) {
                    String w = mit.next();
                    msgWords.add(w);
                }
            }

            buf.append(StringExt.join(msgWords, ", "));
        }
        return buf.toString();
    }

    protected void wordMisspelled(String word, int position, MultiMap<Integer, String> nearMatches) {
        tr.Ace.log("word", word);

        if (this.lines == null) {
            this.lines = new LineMapping(this.desc.text, this.desc.start.line, this.desc.start.column);
        }
        
        Location start = this.lines.getLocation(position);
        Location end   = this.lines.getLocation(position + word.length() - 1);
        String   msg = makeMessage(word, nearMatches);
        
        this.analyzer.addViolation(msg, start, end);
    }
}
