package org.incava.doctorj;

import java.util.List;
import org.incava.analysis.Report;
import net.sourceforge.pmd.ast.*;
import org.incava.javadoc.*;


/**
 * Analyzes Javadoc and code for a class, either concrete or abstract.
 */
public class ClassDocAnalyzer extends TypeDocAnalyzer
{
    public ClassDocAnalyzer(Report r, ASTClassOrInterfaceDeclaration node)
    {
        super(r, node);
    }

    public String getItemType() 
    {
        return "class";
    }

    /**
     * Returns the valid tags, as strings, for classes.
     */
    protected List getValidTags()
    {
        return JavadocTags.getValidClassTags();
    }

}
