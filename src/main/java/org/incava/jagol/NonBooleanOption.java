package org.incava.jagol;

import java.util.List;

/**
 * Base class of all options, except for booleans.
 */
public abstract class NonBooleanOption<VarType> extends Option<VarType> {    
    public NonBooleanOption(String longName, String description) {
        this(longName, description, null, null);
    }

    public NonBooleanOption(String longName, String description, VarType value) {
        this(longName, description, null, value);
    }

    public NonBooleanOption(String longName, String description, Character shortName) {
        this(longName, description, shortName, null);
    }

    public NonBooleanOption(String longName, String description, Character shortName, VarType value) {
        super(longName, description, shortName, value);
    }

    /**
     * Sets from a list of command-line arguments. Returns whether this option
     * could be set from the current head of the list.
     */
    public boolean set(String arg, List<String> argList) throws OptionException {
        return setFromLongName(arg, argList) || setFromLongNameEq(arg) || setFromShortName(arg, argList);
    }
    
    protected void checkArgList(Object name, List<String> argList) throws InvalidTypeException {
        if (argList.isEmpty()) {
            throw new InvalidTypeException(name + " expects following " + getType() + " argument");
        }
    }

    protected void checkArgString(Object name, String arg, int pos) throws InvalidTypeException {
        if (pos >= arg.length()) {
            throw new InvalidTypeException(name + " expects argument of type " + getType());
        }
    }

    protected boolean setFromLongName(String arg, List<String> argList) throws OptionException {
        String longName = getLongName();
        return arg.equals("--" + longName) ? setFromArgList(longName, argList) : false;
    }

    protected boolean setFromLongNameEq(String arg) throws OptionException {
        String longNameEq = "--" + getLongName() + "=";
        return arg.startsWith(longNameEq) ? setFromStringPosition(getLongName(), arg, longNameEq.length()) : false;
    }
    
    protected boolean setFromStringPosition(String name, String arg, int pos) throws InvalidTypeException {
        checkArgString(name, arg, pos);        
        setValueFromString(arg.substring(pos));
        return true;
    }

    protected boolean setFromShortName(String arg, List<String> argList) throws OptionException {
        Character shortName = getShortName();
        return shortName != null && arg.equals("-" + shortName) ? setFromArgList(shortName, argList) : false;
    }
    
    protected boolean setFromArgList(Object name, List<String> argList) throws OptionException {
        checkArgList(name, argList);
        setValueFromString(argList.remove(0));
        return true;
    }

    /**
     * Returns the option type.
     */
    protected abstract String getType();
}
