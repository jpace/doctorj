package org.incava.doctorj;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.javadoc.*;
import org.incava.lang.StringExt;
import org.incava.text.LineMapping;
import org.incava.text.Location;


public class ItemCommentSpellCheck extends CommentSpellCheck
{
    public final static int NUM_CLOSEST_MATCHES = 6;

    private ItemDocAnalyzer _analyzer;

    private JavadocElement _desc;

    private LineMapping _lines = null;

    public void check(ItemDocAnalyzer analyzer, JavadocElement desc)
    {
        _analyzer = analyzer;
        _desc = desc;
        _lines = null;
        
        super.check(_desc.text);
    }
    
    protected String makeMessage(String word, Map nearMatches)
    {
        StringBuffer buf = new StringBuffer("Word '" + word + "' appears to be misspelled. ");
        if (nearMatches.size() == 0) {
            buf.append("No close matches");
        }
        else {
            buf.append("Closest matches: ");
            
            Iterator it = nearMatches.values().iterator();
            List msgWords = new ArrayList();
            
            while (it.hasNext() && msgWords.size() < NUM_CLOSEST_MATCHES) {
                List     matches = (List)it.next();
                Iterator mit     = matches.iterator();
                
                while (mit.hasNext() && msgWords.size() < NUM_CLOSEST_MATCHES) {
                    String w = (String)mit.next();
                    msgWords.add(w);
                }
            }

            buf.append(StringExt.join(msgWords, ", "));
        }
        return buf.toString();
    }

    protected void wordMisspelled(String word, int position, Map nearMatches)
    {
        tr.Ace.log("word", word);

        if (_lines == null) {
            _lines = new LineMapping(_desc.text, _desc.start.line, _desc.start.column);
        }
        
        Location start = _lines.getLocation(position);
        Location end   = _lines.getLocation(position + word.length() - 1);
        String   msg   = makeMessage(word, nearMatches);
        
        _analyzer.addViolation(msg, start, end);
    }
}
