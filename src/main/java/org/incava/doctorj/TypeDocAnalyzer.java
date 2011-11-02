package org.incava.doctorj;

import java.util.List;
import java.util.Map;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.Report;
import org.incava.javadoc.*;
import org.incava.pmdx.ClassUtil;
import org.incava.pmdx.SimpleNodeUtil;


/**
 * Analyzes Javadoc and code for a type, which is a class (concrete or abstract)
 * or an interface.
 */
public abstract class TypeDocAnalyzer extends ItemDocAnalyzer {

    /**
     * The message for an author without a name.
     */
    public final static String MSG_AUTHOR_WITHOUT_NAME = "@author without name text";

    /**
     * The message for a version without associated text.
     */
    public final static String MSG_VERSION_WITHOUT_TEXT = "@version without text";

    /**
     * The message for a serial field without a description.
     */
    public final static String MSG_SERIAL_WITHOUT_TEXT = "@serial without field description";

    /**
     * The node to which this type applies.
     */
    private final ASTClassOrInterfaceDeclaration decl;
    
    /**
     * Creates an analyzer, but does not yet run.
     */
    public TypeDocAnalyzer(Report r, ASTClassOrInterfaceDeclaration decl, int warningLevel) {
        super(r, decl, warningLevel);
        
        this.decl = decl;
    }

    /**
     * Checks the Javadoc against that expected by a type.
     */
    protected void checkJavadoc(JavadocNode javadoc) {
        super.checkJavadoc(javadoc);

        if (javadoc != null && isCheckable(getEnclosingNode(), CHKLVL_TAG_CONTENT)) {
            JavadocTaggedNode[] taggedComments = javadoc.getTaggedComments();
            for (int ti = 0; ti < taggedComments.length; ++ti) {
                JavadocTaggedNode jtn = taggedComments[ti];
                JavadocTag        tag = jtn.getTag();
                
                if (tag.text.equals(JavadocTags.AUTHOR)) {
                    checkForTagDescription(jtn, MSG_AUTHOR_WITHOUT_NAME);
                }
                else if (tag.text.equals(JavadocTags.VERSION)) {
                    checkForTagDescription(jtn, MSG_VERSION_WITHOUT_TEXT);
                }
                else if (tag.text.equals(JavadocTags.SERIAL)) {
                    checkForTagDescription(jtn, MSG_SERIAL_WITHOUT_TEXT);
                }
            }
        }
    }

    /**
     * Adds a violation, for something that is not documented.
     */
    protected void addUndocumentedViolation(String desc) {
        Token nameTk = ClassUtil.getName(this.decl);
        addViolation(desc, nameTk);
    }

    protected SimpleNode getEnclosingNode() {
        return SimpleNodeUtil.getParent(this.decl);
    }

}
