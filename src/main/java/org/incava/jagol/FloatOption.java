package org.incava.jagol;


/**
 * Represents an option that is a float.
 */
public class FloatOption extends NonBooleanOption {
    
    private Float value;
    
    public FloatOption(String longName, String description) {
        this(longName, description, null);
    }

    public FloatOption(String longName, String description, Float value) {
        super(longName, description);
        this.value = value;
    }

    /**
     * Returns the value. This is null if not set.
     */
    public Float getValue() {
        return value;
    }

    /**
     * Sets the value.
     */
    public void setValue(Float value) {
        this.value = value;
    }

    /**
     * Sets the value from the string, for a float type.
     */
    public void setValue(String value) throws InvalidTypeException {
        try {
            setValue(new Float(value));
        }
        catch (NumberFormatException nfe) {
            throw new InvalidTypeException(longName + " expects float argument, not '" + value + "'");
        }
    }

    public String toString() {
        return value == null ? "" : value.toString();
    }

    protected String getType() {
        return "float";
    }
}
