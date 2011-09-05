package org.incava.doctorj;

import java.io.*;
import java.util.*;
import org.incava.ijdk.io.FileExt;
import org.incava.ijdk.util.MultiMap;
import org.incava.text.*;


public class CommentSpellCheck extends ParsingSpellChecker {

    public static CommentSpellCheck instance = new CommentSpellCheck();

    public static CommentSpellCheck getInstance() {
        return instance;
    }
    
    protected CommentSpellCheck() {
        super(new NoCaseSpellChecker());
    }

}
