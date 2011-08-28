package org.incava.java;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.util.ReverseComparator;


/**
 * Miscellaneous routines for type declarations.
 */
public class TypeDeclarationUtil extends SimpleNodeUtil
{
    public static Token getName(ASTTypeDeclaration typeDecl)
    {
        ASTClassOrInterfaceDeclaration cidecl = (ASTClassOrInterfaceDeclaration)findChild(typeDecl, ASTClassOrInterfaceDeclaration.class);
        return cidecl == null ? null : cidecl.getFirstToken().next;
    }

    public static ASTClassOrInterfaceDeclaration getType(ASTTypeDeclaration typeDecl)
    {
        return (ASTClassOrInterfaceDeclaration)SimpleNodeUtil.findChild(typeDecl, ASTClassOrInterfaceDeclaration.class);
    }

    public static ASTTypeDeclaration findTypeDeclaration(String name, ASTTypeDeclaration[] types)
    {
        for (int i = 0; i < types.length; ++i) {
            ASTTypeDeclaration type      = types[i];
            Token              otherName = getName(type);

            if ((otherName == null && name == null) ||
                (otherName != null && otherName.image.equals(name))) {
                return type;
            }
        }

        return null;
    }

    /**
     * Returns a list of all methods, fields, constructors, and inner classes
     * and interfaces.
     */
    public static ASTClassOrInterfaceBodyDeclaration[] getDeclarations(ASTTypeDeclaration tdecl)
    {
        ASTClassOrInterfaceDeclaration cidecl = (ASTClassOrInterfaceDeclaration)findChild(tdecl, ASTClassOrInterfaceDeclaration.class);
        return getDeclarations(cidecl);
    }

    /**
     * Returns a list of all methods, fields, constructors, and inner classes
     * and interfaces.
     */
    public static ASTClassOrInterfaceBodyDeclaration[] getDeclarations(ASTClassOrInterfaceDeclaration coid)
    {
        ASTClassOrInterfaceBody body = (ASTClassOrInterfaceBody)findChild(coid, ASTClassOrInterfaceBody.class);
        return (ASTClassOrInterfaceBodyDeclaration[])findChildren(body, ASTClassOrInterfaceBodyDeclaration.class);
    }

    /**
     * Returns the real declaration, which is a method, field, constructor, or
     * inner class or interface.
     */
    public static SimpleNode getDeclaration(ASTClassOrInterfaceBodyDeclaration bdecl)
    {
        return hasChildren(bdecl) ? findChild(bdecl, null) : null;
    }

    public static TreeMap matchDeclarations(ASTClassOrInterfaceBodyDeclaration[] aDecls, ASTClassOrInterfaceBodyDeclaration[] bDecls)
    {
        // keys (scores) maintained in reversed order:
        TreeMap byScore = new TreeMap(new ReverseComparator());

        // map b by declaration types

        tr.Ace.log("aDecls", aDecls);
        tr.Ace.log("bDecls", bDecls);

        for (int ai = 0; ai < aDecls.length; ++ai) {
            ASTClassOrInterfaceBodyDeclaration aNode = aDecls[ai];
            SimpleNode an     = getDeclaration(aNode);
            List       scores = new ArrayList();

            for (int bi = 0; bi < bDecls.length; ++bi) {
                ASTClassOrInterfaceBodyDeclaration bNode = bDecls[bi];

                double     score = getMatchScore(aNode, bNode);
                if (score > 0.0) {
                    Double dScore  = new Double(score);
                    List   atScore = (List)byScore.get(dScore);
                    if (atScore == null) {
                        atScore = new ArrayList();
                        byScore.put(dScore, atScore);
                    }
                    atScore.add(new Object[] { aNode, bNode });
                }
            }
        }

        List     aSeen  = new ArrayList();
        List     bSeen  = new ArrayList();
        Set      scores = byScore.keySet();
        Iterator sit    = scores.iterator();

        while (sit.hasNext()) {
            Double   dScore  = (Double)sit.next();
            List     atScore = (List)byScore.get(dScore);
            Iterator vit     = atScore.iterator();

            while (vit.hasNext()) {
                Object[]   values = (Object[])vit.next();
                SimpleNode a      = (SimpleNode)values[0];
                SimpleNode b      = (SimpleNode)values[1];

                if (aSeen.contains(a)) {
                    // a already seen
                    vit.remove();
                }
                else if (bSeen.contains(b)) {
                    // b already seen
                    vit.remove();
                }
                else {                    
                    // neither already seen
                    aSeen.add(a);
                    bSeen.add(b);
                }
            }
            
            if (atScore.size() == 0) {
                // remove the empty list
                sit.remove();
            }
        }

        return byScore;
    }

    public static double getMatchScore(ASTClassOrInterfaceBodyDeclaration aDecl, ASTClassOrInterfaceBodyDeclaration bDecl)
    {
        SimpleNode a = getDeclaration(aDecl);
        SimpleNode b = getDeclaration(bDecl);

        double score = 0.0;
        if (a == null && b == null) {
            score = 1.0;
        }
        else if (a == null || b == null) {
            // not a match.
        }
        else if (a.getClass().equals(b.getClass())) {
            if (a instanceof ASTMethodDeclaration) {
                score = MethodUtil.getMatchScore((ASTMethodDeclaration)a, (ASTMethodDeclaration)b);
            }
            else if (a instanceof ASTFieldDeclaration) {
                // compare by name
                score = FieldUtil.getMatchScore((ASTFieldDeclaration)a, (ASTFieldDeclaration)b);
            }
            else if (a instanceof ASTConstructorDeclaration) {
                score = CtorUtil.getMatchScore((ASTConstructorDeclaration)a, (ASTConstructorDeclaration)b);
            }
            else if (a instanceof ASTClassOrInterfaceDeclaration) {
                ASTClassOrInterfaceDeclaration acoid = (ASTClassOrInterfaceDeclaration)a;
                ASTClassOrInterfaceDeclaration bcoid = (ASTClassOrInterfaceDeclaration)b;
                score = ClassUtil.getMatchScore(acoid, bcoid);
            }
            else {
                // WTF?
                tr.Ace.stack(tr.Ace.RED, "a", a);
            }
        }

        return score;
    }

}
