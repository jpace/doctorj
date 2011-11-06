package org.incava.jagol;


/**
 * Represents an option that is a float.
 */
public class FloatOption extends NonBooleanOption<Float> {    
    private Float value;
    
    public FloatOption(String longName, String description) {
        this(longName, description, null, null);
    }

    public FloatOption(String longName, String description, Float value) {
        this(longName, description, null, value);
    }

    public FloatOption(String longName, String description, Character shortName) {
        super(longName, description, shortName, null);
    }

    public FloatOption(String longName, String description, Character shortName, Float value) {
        super(longName, description, shortName, value);
    }

    /**
     * Sets the value from the string, for a float type.
     */
    public void setValueFromString(String value) throws InvalidTypeException {
        try {
            setValue(new Float(value));
        }
        catch (NumberFormatException nfe) {
            throw new InvalidTypeException(longName + " expects float argument, not '" + value + "'");
        }
    }

    protected String getType() {
        return "float";
    }
}
