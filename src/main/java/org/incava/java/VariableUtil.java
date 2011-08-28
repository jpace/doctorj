package org.incava.java;

import java.util.*;
import net.sourceforge.pmd.ast.*;


/**
 * Miscellaneous routines for variables (declarators).
 */
public class VariableUtil extends SimpleNodeUtil
{
    public static Token getName(ASTVariableDeclarator vd)
    {
        ASTVariableDeclaratorId vid = (ASTVariableDeclaratorId)findChild(vd, ASTVariableDeclaratorId.class);
        Token nameTk = vid.getFirstToken();
        return nameTk;
    }

    public static Token[] getVariableNames(ASTVariableDeclarator[] vds)
    {
        List names = new ArrayList();
        for (int vi = 0; vi < vds.length; ++vi) {
            ASTVariableDeclarator vd = vds[vi];
            names.add(getName(vd));
        }
        return (Token[])names.toArray(new Token[0]);
    }

}
