package org.incava.jagol;

import java.util.List;


/**
 * Represents an option that is an boolean.
 */
public class BooleanOption extends Option<Boolean> {
    public BooleanOption(String longName, String description) {
        this(longName, description, null, null);
    }

    public BooleanOption(String longName, String description, Boolean value) {
        this(longName, description, null, value);
    }

    public BooleanOption(String longName, String description, Character shortName) {
        this(longName, description, shortName, null);
    }

    public BooleanOption(String longName, String description, Character shortName, Boolean value) {
        super(longName, description, shortName, value);
    }

    /**
     * Sets the value from the string, for a boolean type.
     */
    public void setValueFromString(String value) throws InvalidTypeException {
        String lcvalue = value.toLowerCase();
        if (lcvalue.equals("yes") || lcvalue.equals("true")) {
            setValue(Boolean.TRUE);
        }
        else if (lcvalue.equals("no") || lcvalue.equals("false")) {
            setValue(Boolean.FALSE);
        }
        else {
            throw new InvalidTypeException(longName + " expects boolean argument (yes/no/true/false), not '" + value + "'");
        }
    }

    /**
     * Sets from a list of command-line arguments. Returns whether this option
     * could be set from the current head of the list.
     */
    public boolean set(String arg, List<String> args) throws OptionException {
        if (arg.equals("--" + longName)) {
            setValue(Boolean.TRUE);
        }
        else if (arg.equals("--no-" + longName) || arg.equals("--no" + longName)) {
            setValue(Boolean.FALSE);
        }
        else if (shortName != null && arg.equals("-" + shortName)) {
            setValue(Boolean.TRUE);
        }
        else {
            return false;
        }
        return true;
    }
}
