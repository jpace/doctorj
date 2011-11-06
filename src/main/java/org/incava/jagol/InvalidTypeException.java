package org.incava.jagol;


public class InvalidTypeException extends OptionException {
    private static final long serialVersionUID = 1L;    

    public InvalidTypeException(String msg) {
        super(msg);
    }
}
