package org.incava.jagol;


/**
 * Represents an option that is a String.
 */
public class StringOption extends NonBooleanOption<String> {
    public StringOption(String longName, String description) {
        this(longName, description, null, null);
    }

    public StringOption(String longName, String description, String value) {
        this(longName, description, null, value);
    }

    public StringOption(String longName, String description, Character shortName) {
        super(longName, description, shortName, null);
    }

    public StringOption(String longName, String description, Character shortName, String value) {
        super(longName, description, shortName, value);
    }

    protected String getType() {
        return "string";
    }

    public void setValueFromString(String value) throws InvalidTypeException {
        setValue(value);
    }
}
