package org.incava.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.incava.ijdk.lang.StringExt;

public class Lines extends ArrayList<String> {
    public static final long serialVersionUID = 0L;
    
    public static List<String> collectLines(String eoln, String ... lines) {
        List<String> list = new ArrayList<String>();
        for (String line : lines) {
            list.add(line + eoln);
        }
        return list;
    }
    
    public static List<String> collectLinesNoEOLN(String ... lines) {
        List<String> list = new ArrayList<String>();
        for (String line : lines) {
            list.add(line);
        }
        return list;
    }

    public static List<String> collectLines(String ... lines) {
        return collectLines("\n", lines);
    }
    
    public Lines(String ... lines) {
        super(collectLines(lines));
    }    
    
    public Lines(Collection<String> lines) {
        super(lines);
    }    

    public String[] get() {
        return toArray(new String[size()]);
    }

    public String toString() {
        return StringExt.join(get(), "");
    }
}
