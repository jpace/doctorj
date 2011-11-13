package org.incava.doctorj;

import java.util.*;
import org.incava.analysis.Analyzer;
import org.incava.analysis.Report;
import org.incava.ijdk.util.Collect;

/**
 * Javadoc tags and the order in which they should appear for each Java type.
 */
public class JavadocTags {
    public enum TagValidType { TYPE, CTOR, METHOD, FIELD };
    
    public static class TagDescription {
        String tag;
        int index;
        boolean isTypeTag;
        boolean isCtorTag;
        boolean isMethodTag;
        boolean isFieldTag;

        public final Set<TagValidType> valids;

        public TagDescription(String tag, int index, boolean isTypeTag, boolean isCtorTag, boolean isMethodTag, boolean isFieldTag, EnumSet<TagValidType> valids) {
            this.tag = tag;
            this.index = index;
            this.isTypeTag = isTypeTag;
            this.isCtorTag = isCtorTag;
            this.isMethodTag = isMethodTag;
            this.isFieldTag = isFieldTag;
            this.valids = valids;
        }

        public TagDescription(String tag, int index, boolean isTypeTag, boolean isCtorTag, boolean isMethodTag, boolean isFieldTag) {
            this.tag = tag;
            this.index = index;
            this.isTypeTag = isTypeTag;
            this.isCtorTag = isCtorTag;
            this.isMethodTag = isMethodTag;
            this.isFieldTag = isFieldTag;
            this.valids = new TreeSet<TagValidType>();
            if (isTypeTag) {
                this.valids.add(TagValidType.TYPE);
            }
            if (isCtorTag) {
                this.valids.add(TagValidType.CTOR);
            }
            if (isMethodTag) {
                this.valids.add(TagValidType.METHOD);
            } 
            if (isFieldTag) {
                this.valids.add(TagValidType.FIELD);
            }
        }

        public boolean isValid(TagValidType tvt) {
            return this.valids.contains(tvt);
        }
    }

    public final static String AUTHOR = "@author";
    public final static String VERSION = "@version";
    public final static String PARAM = "@param";
    public final static String RETURN = "@return";
    public final static String EXCEPTION = "@exception";
    public final static String THROWS = "@throws";
    public final static String SEE = "@see";
    public final static String SINCE = "@since";
    public final static String SERIAL = "@serial";
    public final static String SERIALDATA = "@serialData";
    public final static String SERIALFIELD = "@serialField";
    public final static String DEPRECATED = "@deprecated";
    public final static int CUSTOM_TAG = 999;

    private final static Map<String, TagDescription> tags = new HashMap<String, TagDescription>();
    
    // reference: http://java.sun.com/j2se/1.4.2/docs/tooldocs/solaris/javadoc.html
    static {
        //                     tag          idx  type     ctor   method field
        add(new TagDescription(AUTHOR,      0,   true,    false, false, false));
        add(new TagDescription(VERSION,     1,   true,    false, false, false));
        add(new TagDescription(PARAM,       2,   false,   true,  true,  false));
        add(new TagDescription(RETURN,      3,   false,   false, true,  false));
        add(new TagDescription(EXCEPTION,   4,   false,   true,  true,  false));
        add(new TagDescription(THROWS,      4,   false,   true,  true,  false));
        add(new TagDescription(SEE,         5,   true,    true,  true,  true));
        add(new TagDescription(SINCE,       6,   true,    true,  true,  true));
        add(new TagDescription(SERIAL,      7,   true,    false, false, true));
        add(new TagDescription(SERIALDATA,  7,   false,   true,  true,  false));
        add(new TagDescription(SERIALFIELD, 7,   false,   false, false, true));
        add(new TagDescription(DEPRECATED,  8,   true,    true,  true,  true));
    }

    public static void add(TagDescription td) {
        tags.put(td.tag, td);
    }

    public static int getIndex(String tag) {
        return tags.containsKey(tag)? tags.get(tag).index : -1;
    }

    public static List<String> getTagsAtIndex(int index) {
        Iterator<String> it = tags.keySet().iterator();
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, TagDescription> entry : tags.entrySet()) {
            String tag = entry.getKey();
            TagDescription td = entry.getValue();
            if (td.index == index) {
                list.add(tag);
            }
        }
        return list;
    }

    private static List<String> getMatchingTags(TagValidType tvt) {
        List<String> matches = new ArrayList<String>();
        for (TagDescription td : tags.values()) {
            if (td.isValid(tvt)) {
                matches.add(td.tag);
            }
        }
        return matches;
    }
    
    public static List<String> getValidConstructorTags() {
        return getMatchingTags(TagValidType.CTOR);
    }
    
    public static List<String> getValidMethodTags() {
        return getMatchingTags(TagValidType.METHOD);
    }

    public static List<String> getValidFieldTags() {
        return getMatchingTags(TagValidType.FIELD);
    }

    public static List<String> getValidInterfaceTags() {
        return getMatchingTags(TagValidType.TYPE);
    }

    public static List<String> getValidClassTags() {
        return getMatchingTags(TagValidType.TYPE);
    }
}
