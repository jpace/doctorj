package org.incava.jagol;


/**
 * Represents an option that is an integer.
 */
public class IntegerOption extends NonBooleanOption<Integer> {
    public IntegerOption(String longName, String description) {
        this(longName, description, null, null);
    }

    public IntegerOption(String longName, String description, Integer value) {
        this(longName, description, null, value);
    }

    public IntegerOption(String longName, String description, Character shortName) {
        this(longName, description, shortName, null);
    }

    public IntegerOption(String longName, String description, Character shortName, Integer value) {
        super(longName, description, shortName, value);
    }

    /**
     * Sets the value from the string, for an integer type.
     */
    public void setValueFromString(String value) throws InvalidTypeException {
        try {
            setValue(new Integer(value));
        }
        catch (NumberFormatException nfe) {
            throw new InvalidTypeException(getLongName() + " expects integer argument, not '" + value + "'");
        }
    }    

    protected String getType() {
        return "integer";
    }
}
