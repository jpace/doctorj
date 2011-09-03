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
    
    private ASTFieldDeclaration _field;
    
    public FieldDocAnalyzer(Report r, ASTFieldDeclaration field) {
        super(r, field);
        
        _field = field;
    }

    public String getItemType() {
        return "field";
    }
    
    protected void checkJavadoc(JavadocNode javadoc) {
        super.checkJavadoc(javadoc);

        SimpleNode encNode = getEnclosingNode();

        if (javadoc != null && isCheckable(encNode, CHKLVL_TAG_CONTENT)) {
            JavadocTaggedNode[] taggedComments = javadoc.getTaggedComments();
            for (int ti = 0; ti < taggedComments.length; ++ti) {
                JavadocTaggedNode jtn = taggedComments[ti];                
                JavadocTag        tag = jtn.getTag();

                tr.Ace.log("checking tag: " + tag);
                if (tag.text.equals(JavadocTags.SERIALFIELD)) {

                    // expecting: field - name field - type field - description
                    JavadocElement desc = jtn.getDescription();
                    if (desc == null) {
                        addViolation(MSG_SERIALFIELD_WITHOUT_NAME_TYPE_AND_DESCRIPTION, tag.start, tag.end);
                    }
                    else {
                        JavadocElement nontgt = jtn.getDescriptionNonTarget();

                        if (nontgt == null) {
                            addViolation(MSG_SERIALFIELD_WITHOUT_TYPE_AND_DESCRIPTION, desc.start, desc.end);
                        }
                        else {
                            String text = nontgt.text;
                            
                            int pos = 0;
                            int len = text.length();

                            boolean gotAnotherWord = false;
                            while (!gotAnotherWord && pos < len) {
                                if (Character.isWhitespace(text.charAt(pos))) {
                                    gotAnotherWord = true;
                                }
                                ++pos;
                            }

                            if (!gotAnotherWord) {
                                addViolation(MSG_SERIALFIELD_WITHOUT_DESCRIPTION, desc.start, desc.end);
                            }
                        }
                    }
                }
            }
        }        
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

        ASTVariableDeclarator[] vds = FieldUtil.getVariableDeclarators(_field);

        Token begin = vds[0].getFirstToken();
        Token end = vds[vds.length - 1].getFirstToken();

        addViolation(desc, begin, end);
    }

    protected SimpleNode getEnclosingNode() {
        return SimpleNodeUtil.getParent(_field);
    }

}
