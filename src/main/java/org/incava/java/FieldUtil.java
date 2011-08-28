package org.incava.java;

import java.util.*;
import net.sourceforge.pmd.ast.*;


/**
 * Miscellaneous routines for fields.
 */
public class FieldUtil extends SimpleNodeUtil
{
    public static Token getName(ASTVariableDeclarator vd)
    {
        ASTVariableDeclaratorId vid = (ASTVariableDeclaratorId)findChild(vd, ASTVariableDeclaratorId.class);
        Token nameTk = vid.getFirstToken();
        return nameTk;
    }

    public static ASTVariableDeclarator[] getVariableDeclarators(ASTFieldDeclaration fld)
    {
        return (ASTVariableDeclarator[])findChildren(fld, ASTVariableDeclarator.class);
    }

    /**
     * Returns a string in the form "a, b, c", for the variables declared in
     * this field.
     */
    public static String getNames(ASTFieldDeclaration fld)
    {
        ASTVariableDeclarator[] avds = getVariableDeclarators(fld);
        StringBuffer buf = new StringBuffer();
        for (int ai = 0; ai < avds.length; ++ai) {
            ASTVariableDeclarator avd = avds[ai];
            if (ai > 0) {
                buf.append(", ");
            }
            buf.append(VariableUtil.getName(avd).image);
        }
        return buf.toString();
    }

    public static double getMatchScore(ASTFieldDeclaration a, ASTFieldDeclaration b) 
    {
        ASTVariableDeclarator[] avds = getVariableDeclarators(a);
        ASTVariableDeclarator[] bvds = getVariableDeclarators(b);

        Token[] aNames = VariableUtil.getVariableNames(avds);
        Token[] bNames = VariableUtil.getVariableNames(bvds);

        int matched = 0;
        int i = 0;
        while (i < aNames.length && i < bNames.length) {
            if (aNames[i].image.equals(bNames[i].image)) {
                ++matched;
            }
            ++i;
        }

        int count = Math.max(aNames.length, bNames.length);

        double score = 0.5 * matched / count;

        ASTType aType = (ASTType)findChild(a, ASTType.class);
        ASTType bType = (ASTType)findChild(b, ASTType.class);

        if (toString(aType).equals(toString(bType))) {
            score += 0.5;
        }
        
        return score;
    }

}
