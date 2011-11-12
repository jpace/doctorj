package org.incava.doctorj;

import java.util.List;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.Report;
import org.incava.javadoc.*;
import org.incava.pmdx.FieldUtil;
import org.incava.pmdx.SimpleNodeUtil;

/**
 * Analyzes Javadoc and code for fields.
 */
public class FieldDocAnalyzer extends ItemDocAnalyzer {
    public final static String MSG_SERIALFIELD_WITHOUT_NAME_TYPE_AND_DESCRIPTION = "@serialField without field name, type, and description.";
    public final static String MSG_SERIALFIELD_WITHOUT_TYPE_AND_DESCRIPTION = "@serialField without field type and description.";
    public final static String MSG_SERIALFIELD_WITHOUT_DESCRIPTION = "@serialField without description.";
    
    private final ASTFieldDeclaration field;
    
    public FieldDocAnalyzer(Report r, ASTFieldDeclaration field, int warningLevel) {
        super(r, field, warningLevel);
        this.field = field;
    }

    public String getItemType() {
        return "field";
    }
    
    protected void checkJavadoc(JavadocNode javadoc) {
        super.checkJavadoc(javadoc);

        SimpleNode encNode = getEnclosingNode();

        if (javadoc != null && isCheckable(encNode, CHKLVL_TAG_CONTENT)) {
            checkTagContent(javadoc);
        }        
    }

    protected void checkTagContent(JavadocNode javadoc) {
        super.checkTagContent(javadoc);
        
        for (JavadocTaggedNode jtn : javadoc.getTaggedComments()) {
            JavadocTag tag = jtn.getTag();
            if (tag.textMatches(JavadocTags.SERIALFIELD)) {
                checkSerialField(jtn);
            }
        }
    }

    protected boolean checkSerialField(JavadocTaggedNode jtn) {
        JavadocTag tag = jtn.getTag();
        
        // expecting: field-name field-type field-description
        JavadocElement desc = jtn.getDescription();
        if (desc == null) {
            return addViolation(MSG_SERIALFIELD_WITHOUT_NAME_TYPE_AND_DESCRIPTION, tag.start, tag.end);
        }
        else {
            return checkSerialFieldType(jtn) && checkSerialFieldDescription(jtn);
        }
    }

    protected boolean checkSerialFieldType(JavadocTaggedNode jtn) {
        // expecting: field-name field-type field-description
        JavadocElement desc = jtn.getDescription();
        if (jtn.getDescriptionNonTarget() == null) {
            return addViolation(MSG_SERIALFIELD_WITHOUT_TYPE_AND_DESCRIPTION, desc.start, desc.end);
        }
        else {
            return true;
        }
    }

    protected boolean checkSerialFieldDescription(JavadocTaggedNode jtn) {
        // expecting: field-name field-type field-description
        JavadocElement desc = jtn.getDescription();
        JavadocElement nontgt = jtn.getDescriptionNonTarget();

        // look for some non-whitespace in what should be the description
        String text = nontgt.text;

        for (int pos = 0; pos < text.length(); ++pos) {
            if (Character.isWhitespace(text.charAt(pos))) {
                return true;
            }
        }

        return addViolation(MSG_SERIALFIELD_WITHOUT_DESCRIPTION, desc.start, desc.end);
    }

    /**
     * Returns the valid tags, as strings, for fields.
     */
    protected List<String> getValidTags() {
        return JavadocTags.getValidFieldTags();
    }

    /**
     * Adds a violation for a field, with the violation pointing to the field
     * name.
     */
    protected void addUndocumentedViolation(String desc) {
        // reference the list of variables declared in this field.
        ASTVariableDeclarator[] vds = FieldUtil.getVariableDeclarators(this.field);

        Token begin = vds[0].getFirstToken();
        Token end = vds[vds.length - 1].getFirstToken();

        addViolation(desc, begin, end);
    }

    protected SimpleNode getEnclosingNode() {
        return SimpleNodeUtil.getParent(this.field);
    }
}
