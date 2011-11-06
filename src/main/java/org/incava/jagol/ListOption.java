package org.incava.jagol;

import java.util.ArrayList;
import java.util.List;
import org.incava.ijdk.lang.StringExt;


/**
 * Represents a list of objects that comprise this option.
 */
public class ListOption extends NonBooleanOption<List<String>> {
    public ListOption(String longName, String description) {
        this(longName, description, null, new ArrayList<String>());
    }

    public ListOption(String longName, String description, List<String> value) {
        this(longName, description, null, value);
    }

    public ListOption(String longName, String description, Character shortName) {
        this(longName, description, shortName, new ArrayList<String>());
    }

    public ListOption(String longName, String description, Character shortName, List<String> value) {
        super(longName, description, shortName, value);
    }

    /**
     * Sets the value from the string, for a list type. Assumes whitespace or
     * comma delimiter.
     */
    public void setValueFromString(String value) throws InvalidTypeException {
        parse(value);
    }

    protected void checkArgList(Object name, List<String> argList) throws InvalidTypeException {
        if (argList.isEmpty()) {
            throw new InvalidTypeException(name + " expects following argument");
        }
    }

    protected void checkArgString(Object name, String arg, int pos) throws InvalidTypeException {
        if (pos >= arg.length()) {
            throw new InvalidTypeException(name + " expects argument");
        }
    }

    /**
     * Parses the value into the value list. If subclasses want to convert the
     * string to their own data type, override the < code > convert</code > method.
     *
     * @see ListOption#convert(String)
     */
    protected void parse(String str) throws InvalidTypeException {
        List<String> val = new ArrayList<String>();
        List<String> list = StringExt.toList(str);
        for (String s : list) {
            if (!s.equals("+=")) {
                value.add(convert(s));
            }
        }
        super.setValue(value);
    }

    /**
     * Returns the string, possibly converted to a different Object type. 
     * Subclasses can convert the string to their own data type.
     */
    protected String convert(String str) throws InvalidTypeException {
        return str;
    }

    public String toString() {
        return StringExt.join(getValue(), ", ");
    }

    public String getType() {
        return null;
    }
}
