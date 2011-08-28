package org.incava.java;

import java.util.*;
import net.sourceforge.pmd.ast.*;


/**
 * Miscellaneous routines for method declarations.
 */
public class MethodUtil extends FunctionUtil
{
    private static Map methodCriterias = new HashMap();

    public static ASTMethodDeclarator getDeclarator(ASTMethodDeclaration method)
    {
        return (ASTMethodDeclarator)SimpleNodeUtil.findChild(method, ASTMethodDeclarator.class);
    }

    public static Token getName(ASTMethodDeclaration method)
    {
        ASTMethodDeclarator decl = getDeclarator(method);
        return decl.getFirstToken();
    }

    public static ASTFormalParameters getParameters(ASTMethodDeclaration method)
    {
        ASTMethodDeclarator decl = getDeclarator(method);
        return (ASTFormalParameters)SimpleNodeUtil.findChild(decl, ASTFormalParameters.class);
    }

    public static String getFullName(ASTMethodDeclaration method)
    {
        Token nameTk = getName(method);
        ASTFormalParameters params = getParameters(method);
        String fullName = toFullName(nameTk, params);
        return fullName;
    }

    public static double getMatchScore(ASTMethodDeclaration a, ASTMethodDeclaration b) 
    {
        // caching the criteria (instead of extracting it every time) is around 25% faster.
        MethodMatchCriteria aCriteria = getCriteria(a);
        MethodMatchCriteria bCriteria = getCriteria(b);

        return aCriteria.compare(bCriteria);
    }

    protected static MethodMatchCriteria getCriteria(ASTMethodDeclaration method)
    {
        MethodMatchCriteria crit = (MethodMatchCriteria)methodCriterias.get(method);
        if (crit == null) {
            crit = new MethodMatchCriteria(method);
            methodCriterias.put(method, crit);
        }
        return crit;
    }

}
