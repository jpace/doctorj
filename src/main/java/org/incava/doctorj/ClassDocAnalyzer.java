package org.incava.doctorj;

import java.util.List;
import org.incava.analysis.Report;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;

/**
 * Analyzes Javadoc and code for a class, either concrete or abstract.
 */
public class ClassDocAnalyzer extends TypeDocAnalyzer {    
    public ClassDocAnalyzer(Report r, ASTClassOrInterfaceDeclaration node, int warningLevel) {
        super(r, node, warningLevel);
    }

    public String getItemType() {
        return "class";
    }

    /**
     * Returns the valid tags, as strings, for classes.
     */
    protected List<String> getValidTags() {
        return JavadocTags.getValidClassTags();
    }
}
