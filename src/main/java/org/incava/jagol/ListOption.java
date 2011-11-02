package org.incava.jagol;

import java.util.*;
import org.incava.ijdk.lang.StringExt;


/**
 * Represents a list of objects that comprise this option.
 */
public class ListOption extends Option {
    
    private List<String> value;
    
    /**
     * Creates the option.
     */
    public ListOption(String longName, String description) {
        this(longName, description, new ArrayList<String>());
    }

    /**
     * Creates the option, with a default list.
     */
    public ListOption(String longName, String description, List<String> value) {
        super(longName, description);
        this.value = value;
    }

    /**
     * Returns the value. This is empty by default.
     */
    public List<String> getValue() {
        return value;
    }

    /**
     * Sets the value.
     */
    public void setValue(List<String> value) {
        this.value = value;
    }

    /**
     * Sets the value from the string, for a list type. Assumes whitespace or
     * comma delimiter
     */
    public void setValue(String value) throws InvalidTypeException {
        parse(value);
    }

    /**
     * Sets from a list of command - line arguments. Returns whether this option
     * could be set from the current head of the list. Assumes whitespace or
     * comma delimiter.
     */
    public boolean set(String arg, List<? extends Object> args) throws OptionException {
        if (arg.equals("--" + longName)) {
            if (args.isEmpty()) {
                throw new InvalidTypeException(longName + " expects following argument");
            }
            else {
                Object value = args.remove(0);
                setValue(value.toString());
            }
        }
        else if (arg.startsWith("--" + longName + "=")) {
            int pos = ("--" + longName + "=").length();
            if (pos >= arg.length()) {
                throw new InvalidTypeException(longName + " expects argument");
            }
            else {
                String value = arg.substring(pos);
                setValue(value);
            }
        }
        else if (shortName != 0 && arg.equals("-" + shortName)) {
            if (args.isEmpty()) {
                throw new InvalidTypeException(shortName + " expects following argument");
            }
            else {
                String value = args.remove(0).toString();
                setValue(value);
            }
        }
        else {
            return false;
        }
        return true;
    }

    /**
     * Parses the value into the value list. If subclasses want to convert the
     * string to their own data type, override the < code > convert</code > method.
     *
     * @see ListOption#convert(String)
     */
    protected void parse(String str) throws InvalidTypeException {
        List<String> list = StringExt.toList(str);
        for (String s : list) {
            if (!s.equals("+=")) {
                value.add(convert(s));
            }
        }
    }

    /**
     * Returns the string, possibly converted to a different Object type. 
     * Subclasses can convert the string to their own data type.
     */
    protected String convert(String str) throws InvalidTypeException {
        return str;
    }

    public String toString() {
        return StringExt.join(value, ", ");
    }
}
