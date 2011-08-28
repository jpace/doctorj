package org.incava.java;

import java.util.*;
import net.sourceforge.pmd.ast.*;


/**
 * A criterion (some criteria) for matching nodes.
 */
public class MethodMatchCriteria extends MatchCriteria
{
    private ASTMethodDeclaration meth;

    private String name = null;

    private ASTFormalParameters params = null;
    
    public MethodMatchCriteria(ASTMethodDeclaration m)
    {
        meth = m;
    }

    public double compare(MatchCriteria other)
    {
        if (other instanceof MethodMatchCriteria) {
            MethodMatchCriteria mmother = (MethodMatchCriteria)other;
            
            // System.out.println("comparing " + hashCode() + " <=> " + other.hashCode());
            
            String aName = getName();
            String bName = mmother.getName();

            double score = 0.0;

            if (aName.equals(bName)) {
                ASTFormalParameters afp = getParameters();
                ASTFormalParameters bfp = mmother.getParameters();

                score = ParameterUtil.getMatchScore(afp, bfp);
            }
            else {
                // this could eventually find methods renamed, if we compare based
                // on parameters and method contents
            }

            return score;

        }
        else {
            return super.compare(other);
        }
    }

    protected String getName()
    {
        if (name == null) {
            name = MethodUtil.getName(meth).image;
        }
        return name;
    }

    protected ASTFormalParameters getParameters()
    {
        if (params == null) {
            params = MethodUtil.getParameters(meth);
        }
        return params;
    }

}
