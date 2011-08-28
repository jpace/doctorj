package org.incava.java;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.lang.StringExt;


/**
 * Miscellaneous routines for functions (ctors and methods).
 */
public class FunctionUtil extends SimpleNodeUtil
{
    /**
     * Returns the throws token, or null if none.
     */
    public static Token getThrows(SimpleNode function)
    {
        Token tk = function.getFirstToken();
        while (true) {
            if (tk.kind == JavaParserConstants.THROWS) {
                return tk;
            }
            else if (tk == function.getLastToken()) {
                break;
            }
            else {
                tk = tk.next;
            }
        }
        return null;
    }

    /**
     * Returns the throws list, or null if none.
     */
    public static ASTNameList getThrowsList(SimpleNode function)
    {
        List children = getChildren(function);
        Iterator it = children.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof Token && ((Token)obj).kind == JavaParserConstants.THROWS && it.hasNext()) {
                ASTNameList throwsList = (ASTNameList)it.next();
                return throwsList;
            }
        }
        return null;
    }

    protected static String toFullName(Token tk, ASTFormalParameters params)
    {
        List   types = ParameterUtil.getParameterTypes(params);
        String args  = StringExt.join(types, ", ");
        return tk.image + "(" + args + ")";
    }
    
}
