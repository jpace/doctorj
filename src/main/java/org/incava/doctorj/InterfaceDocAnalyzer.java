package org.incava.doctorj;

import java.util.List;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import org.incava.analysis.Report;
import org.incava.javadoc.*;


/**
 * Analyzes Javadoc and code for an interface.
 */
public class InterfaceDocAnalyzer extends TypeDocAnalyzer {

    public InterfaceDocAnalyzer(Report r, ASTClassOrInterfaceDeclaration node, int warningLevel) {
        super(r, node, warningLevel);
    }

    public String getItemType() {
        return "interface";
    }

    /**
     * Returns the valid tags, as strings, for interfaces.
     */
    protected List<String> getValidTags() {
        return JavadocTags.getValidInterfaceTags();
    }

}
