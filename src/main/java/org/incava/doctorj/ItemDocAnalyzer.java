package org.incava.doctorj;

import java.util.*;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.Report;
import org.incava.javadoc.*;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.text.LineMapping;
import org.incava.text.Location;


/**
 * Analyzes Javadoc and code.
 */
public abstract class ItemDocAnalyzer extends DocAnalyzer {

    public final static String MSG_NO_SUMMARY_SENTENCE = "No summary sentence";

    public final static String MSG_SUMMARY_SENTENCE_DOES_NOT_END_WITH_PERIOD = "Summary sentence does not end with period";

    public final static String MSG_SUMMARY_SENTENCE_TOO_SHORT = "Summary sentence too short";

    public final static String MSG_SEE_WITHOUT_REFERENCE = "@see without reference";

    public final static String MSG_SINCE_WITHOUT_TEXT = "@since without text";

    public final static String MSG_DEPRECATED_WITHOUT_TEXT = "@deprecated without text";
    
    public final static String MSG_TAG_IMPROPER_ORDER = "Tag in improper order";

    protected final static int CHKLVL_SUMMARY_SENTENCE = 1;

    protected final static int CHKLVL_MISORDERED_TAGS = 0;

    protected final static int CHKLVL_VALID_TAGS = 0;

    private static SpellingAnalyzer spellingAnalzyer = new SpellingAnalyzer();

    private final SimpleNode node;
    
    public ItemDocAnalyzer(Report r, SimpleNode node) {
        super(r);
        
        this.node = node;
    }

    public static void addDictionary(String dictName) {
        spellingAnalzyer.addDictionary(dictName);
    }

    /**
     * Returns the Javadoc for the given node. By default, this is parses from
     * the whitespace immediately preceding this node. Other nodes might be
     * nested under the node with the preceding Javadoc.
     */
    protected JavadocNode getJavadoc() {
        SimpleNode  sn      = getEnclosingNode();
        JavadocNode javadoc = null;
        Token       first   = sn.getFirstToken();
        Token       st      = first.specialToken;
        
        while (javadoc == null && st != null) {
            javadoc = JavadocParser.parseJavadocNode(st.image, st.beginLine, st.beginColumn);
            st = st.specialToken;
        }
        
        return javadoc;
    }

    /**
     * Returns the node that contains the access and modifier tokens.
     */
    protected abstract SimpleNode getEnclosingNode();

    /**
     * Runs the analysis. Should be invoked by either the constructors of
     * concrete, final subclasses, or by the client.
     */
    public void run() {
        SimpleNode  encNode = getEnclosingNode();
        JavadocNode javadoc = getJavadoc();
        
        if (javadoc == null) {
            if (isCheckable(encNode, CHKLVL_DOC_EXISTS)) {
                tr.Ace.log("no Javadoc");
                StringBuilder desc = new StringBuilder("Undocumented ");
                if (SimpleNodeUtil.hasLeadingToken(encNode, JavaParserConstants.PUBLIC)) {
                    desc.append("public ");
                }
                else if (SimpleNodeUtil.hasLeadingToken(encNode, JavaParserConstants.PROTECTED)) {
                    desc.append("protected ");
                }
                else if (SimpleNodeUtil.hasLeadingToken(encNode, JavaParserConstants.PRIVATE)) {
                    desc.append("private ");
                }

                if (SimpleNodeUtil.hasLeadingToken(encNode, JavaParserConstants.ABSTRACT)) {
                    desc.append("abstract ");
                }
                
                desc.append(getItemType());

                addUndocumentedViolation(desc.toString());
            }
            else {
                tr.Ace.log("not checkable");
            }
        }
        else {
            checkJavadoc(javadoc);
        }
    }

    /**
     * Adds a violation for this type of item, with the violation pointing to
     * the name for this item.
     */
    protected abstract void addUndocumentedViolation(String desc);

    protected void checkSummarySentence(JavadocNode javadoc) {
        JavadocElement desc = javadoc.getDescription();
        if (desc == null) {
            addViolation(MSG_NO_SUMMARY_SENTENCE, javadoc.getStartLine(), javadoc.getStartColumn(), javadoc.getEndLine(), javadoc.getEndColumn());
        }
        else {
            int dotPos = desc.text.indexOf('.');
            int len = desc.text.length();
            LineMapping lines = new LineMapping(desc.text, desc.start.line, desc.start.column);

            // skip sequences like '127.0.0.1', i.e., whitespace must follow the dot:
            while (dotPos != -1 && dotPos + 1 < len && !Character.isWhitespace(desc.text.charAt(dotPos + 1))) {
                dotPos = desc.text.indexOf('.', dotPos + 1);
            }

            if (dotPos == -1) {
                Location end = lines.getLocation(desc.text.length() - 1);
                addViolation(MSG_SUMMARY_SENTENCE_DOES_NOT_END_WITH_PERIOD, desc.start, end);
            }
            else {
                String summarySentence = desc.text.substring(0, dotPos + 1);
                int nSpaces = 0;
                int spacePos = -1;
                while ((spacePos = summarySentence.indexOf(' ', spacePos + 1)) != -1) {
                    ++nSpaces;
                }
                if (nSpaces < 3) {
                    Location end = lines.getLocation(dotPos);
                    addViolation(MSG_SUMMARY_SENTENCE_TOO_SHORT, desc.start, end);
                }
            }

            spellingAnalzyer.check(this, lines, desc.text);
        }
    }

    protected void checkMisorderedTag(JavadocNode javadoc) {
        tr.Ace.log("checking for misordered tags");
        int previousOrderIndex = -1;
        JavadocTaggedNode[] taggedComments = javadoc.getTaggedComments();
        for (int ti = 0; ti < taggedComments.length; ++ti) {
            JavadocTag tag = taggedComments[ti].getTag();
            int        index = JavadocTags.getIndex(tag.text);
            tr.Ace.log("index of '" + tag.text + "': " + index);
            if (index < previousOrderIndex) {
                addViolation(MSG_TAG_IMPROPER_ORDER, tag.start, tag.end);
                break;
            }
            previousOrderIndex = index;
        }
    }

    protected void checkTagValidity(JavadocNode javadoc) {
        tr.Ace.log("checking for valid tags");
        List<String>        validTags = getValidTags();
        JavadocTaggedNode[] taggedComments = javadoc.getTaggedComments();
        for (int ti = 0; ti < taggedComments.length; ++ti) {
            JavadocTag tag = taggedComments[ti].getTag();
            if (!validTags.contains(tag.text)) {
                addViolation("Tag not valid for " + getItemType(), tag.start, tag.end);
            }
        }
    }

    protected void checkTagContent(JavadocNode javadoc) {
        JavadocTaggedNode[] taggedComments = javadoc.getTaggedComments();
        for (int ti = 0; ti < taggedComments.length; ++ti) {
            JavadocTag tag = taggedComments[ti].getTag();
            tr.Ace.log("checking tag: " + tag);
            if (tag.text.equals(JavadocTags.SEE)) {
                checkForTagDescription(taggedComments[ti], MSG_SEE_WITHOUT_REFERENCE);
            }
            else if (tag.text.equals(JavadocTags.SINCE)) {
                checkForTagDescription(taggedComments[ti], MSG_SINCE_WITHOUT_TEXT);
            }
            else if (tag.text.equals(JavadocTags.DEPRECATED)) {
                checkForTagDescription(taggedComments[ti], MSG_DEPRECATED_WITHOUT_TEXT);
            }
        }
    }

    protected void checkJavadoc(JavadocNode javadoc) {
        // check for short summary sentence and spell check the description.

        SimpleNode encNode = getEnclosingNode();

        if (isCheckable(encNode, CHKLVL_SUMMARY_SENTENCE)) {
            checkSummarySentence(javadoc);
        }

        if (isCheckable(encNode, CHKLVL_MISORDERED_TAGS)) {
            checkMisorderedTag(javadoc);
        }

        if (isCheckable(encNode, CHKLVL_VALID_TAGS)) {
            checkTagValidity(javadoc);
        }

        if (isCheckable(encNode, CHKLVL_TAG_CONTENT)) {
            checkTagContent(javadoc);
        }
    }

    protected void checkForTagDescription(JavadocTaggedNode taggedNode, String msg) {
        JavadocElement desc = taggedNode.getDescription();
        if (desc == null) {
            JavadocTag tag = taggedNode.getTag();
            addViolation(msg, tag.start, tag.end);
        }
    }

    protected SimpleNode getNode() {
        return this.node;
    }

    /**
     * Returns the type of item this analyzer is operating on.
     */
    protected abstract String getItemType();

    /**
     * Returns the valid tags, as strings, for this type of item.
     */
    protected abstract List<String> getValidTags();

}
