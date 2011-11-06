package org.incava.jagol;

import java.util.List;


/**
 * Represents an option that is an boolean.
 */
public class BooleanOption extends Option {
    private Boolean value;
    
    public BooleanOption(String longName, String description) {
        this(longName, description, null);
    }

    public BooleanOption(String longName, String description, Boolean value) {
        super(longName, description);
        this.value = value;
    }

    // public BooleanOption(String longName, String description, char shortName, Boolean value) {
    //     super(longName, description, shortName);
    //     this.value = value;
    // }

    /**
     * Returns the value. This is null if it has not been set.
     */
    public Boolean getValue() {
        return value;
    }

    /**
     * Sets the value.
     */
    public void setValue(Boolean value) {
        this.value = value;
    }

    /**
     * Sets the value from the string, for a boolean type.
     */
    public void setValue(String value) throws InvalidTypeException {
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
     * Sets from a list of command - line arguments. Returns whether this option
     * could be set from the current head of the list.
     */
    public boolean set(String arg, List<? extends Object> args) throws OptionException {
        if (arg.equals("--" + longName)) {
            setValue(Boolean.TRUE);
        }
        else if (arg.equals("--no-" + longName) || arg.equals("--no" + longName)) {
            setValue(Boolean.FALSE);
        }
        else if (shortName != 0 && arg.equals("-" + shortName)) {
            setValue(Boolean.TRUE);
        }
        else {
            return false;
        }
        return true;
    }

    public String toString() {
        return value == null ? "" : value.toString();
    }
}
