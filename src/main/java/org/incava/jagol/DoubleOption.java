package org.incava.jagol;


/**
 * Represents an option that is a double.
 */
public class DoubleOption extends NonBooleanOption<Double> {
    public DoubleOption(String longName, String description) {
        this(longName, description, null, null);
    }

    public DoubleOption(String longName, String description, Double value) {
        this(longName, description, null, value);
    }

    public DoubleOption(String longName, String description, Character shortName) {
        this(longName, description, shortName, null);
    }

    public DoubleOption(String longName, String description, Character shortName, Double value) {
        super(longName, description, shortName, value);
    }

    /**
     * Sets the value from the string, for a double type.
     */
    public void setValueFromString(String value) throws InvalidTypeException {
        try {
            setValue(new Double(value));
        }
        catch (NumberFormatException nfe) {
            throw new InvalidTypeException(longName + " expects double argument, not '" + value + "'");
        }
    }

    protected String getType() {
        return "double";
    }
}
