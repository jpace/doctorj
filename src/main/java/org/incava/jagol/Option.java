package org.incava.jagol;

import java.util.List;


/**
 * Base class of all options.
 */
public abstract class Option<VarType> {
    protected final String longName;

    protected Character shortName;

    private final String description;

    protected VarType value;
    
    public Option(String longName, String description) {
        this(longName, description, null);
    }

    public Option(String longName, String description, Character shortName) {
        this(longName, description, shortName, null);
    }

    public Option(String longName, String description, Character shortName, VarType value) {
        this.longName = longName;
        this.description = description;
        this.shortName = shortName;
        this.value = value;
    }

    public void setShortName(Character shortName) {
        this.shortName = shortName;
    }
    
    /**
     * Returns the long option name.
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Returns the short option name.
     */
    public Character getShortName() {
        return shortName;
    }

    /**
     * Returns the description.
     */
    public String getDescription() {
        return description;
    }

    public VarType getValue() {
        return value;
    }

    public void setValue(VarType value) {
        this.value = value;
    }

    /**
     * Sets from a list of command-line arguments. Returns whether this option
     * could be set from the current head of the list.
     */
    public abstract boolean set(String arg, List<String> args) throws OptionException;

    /**
     * Sets the value from the string, for this option type.
     */
    public abstract void setValueFromString(String value) throws InvalidTypeException;

    public String toString() {
        return value == null ? "" : value.toString();
    }

}
