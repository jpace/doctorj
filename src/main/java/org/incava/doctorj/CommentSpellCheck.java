package org.incava.doctorj;

import java.io.*;
import java.util.*;
import org.incava.text.spell.*;


public class CommentSpellCheck extends ParsingSpellChecker {

    public static CommentSpellCheck instance = new CommentSpellCheck();

    public static CommentSpellCheck getInstance() {
        return instance;
    }
    
    protected CommentSpellCheck() {
        super(new NoCaseSpellChecker());
    }

}
