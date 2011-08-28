package org.incava.doctorj;

import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.ParameterUtil;
import org.incava.java.SimpleNodeUtil;
import org.incava.javadoc.*;
import org.incava.text.Location;
import org.incava.text.SpellChecker;


/**
 * Checks the validity of Javadoc for a list of parameters.
 */
public class ParameterDocAnalyzer extends DocAnalyzer
{
    /**
     * The message for documented parameters for a function without any in the
     * code.
     */
    public final static String MSG_PARAMETERS_DOCUMENTED_BUT_NO_CODE_PARAMETERS = "Parameters documented but no code parameters";
    
    /**
     * The message for a parameter field without a name.
     */
    public final static String MSG_PARAMETER_WITHOUT_NAME = "Parameter tag without name";

    /**
     * The message for a parameter field without a description.
     */
    public final static String MSG_PARAMETER_WITHOUT_DESCRIPTION = "Parameter without description";

    /**
     * The message for parameters being misordered with regard to the code.
     */
    public final static String MSG_PARAMETER_NOT_IN_CODE_ORDER = "Parameter not in code order";

    /**
     * The message for a parameter being apparently misspelled from that in the
     * code.
     */
    public final static String MSG_PARAMETER_MISSPELLED = "Parameter misspelled";

    /**
     * The message for a parameter referring to argument type, not name.
     */
    public final static String MSG_PARAMETER_TYPE_USED = "Parameter refers to type, not name";

    /**
     * The message for an undocumented code parameter.
     */
    public final static String MSG_PARAMETER_NOT_DOCUMENTED = "Parameter not documented";

    /**
     * The message for a documented parameter not found in the code.
     */
    public final static String MSG_PARAMETER_NOT_IN_CODE = "Parameter not in code";

    /**
     * The message for a repeated documented parameter.
     */
    public final static String MSG_PARAMETER_REPEATED = "Parameter repeated";

    /**
     * The warning level for checking for the existence of Javadoc for
     * parameters.
     */
    protected final static int CHKLVL_PARAM_DOC_EXISTS = 2;

    /**
     * The Javadoc block applicable to this parameter list.
     */
    private JavadocNode _javadoc;

    /**
     * The function containing the list of parameters.
     */
    private SimpleNode _function;

    /**
     * The list of parameters in the code.
     */
    private ASTFormalParameters _parameterList;

    /**
     * The list of parameters from the code, that were documented.
     */
    private List _documentedParameters = new ArrayList();

    private int _nodeLevel;

    /**
     * Creates and runs the parameter documentation analyzer.
     *
     * @param report   The report to which to send violations.
     * @param javadoc  The javadoc for the function. Should not be null.
     * @param function The constructor or method.
     */
    public ParameterDocAnalyzer(Report report, JavadocNode javadoc, SimpleNode function, ASTFormalParameters parameterList, int nodeLevel)
    {
        super(report);

        _javadoc = javadoc;
        _function = function;
        _parameterList = parameterList;
        _nodeLevel = nodeLevel;
    }

    /**
     * Analyzes the Javadoc for the parameter list.
     */
    public void run()
    {
        tr.Ace.log("function", _function);

        // foreach @throws / @parameter tag:
        //  - check for target
        //  - check for description
        //  - in order as in code

        boolean misorderedReported = false;
        int     parameterIndex     = 0;
            
        JavadocTaggedNode[] taggedComments = _javadoc.getTaggedComments();
        for (int ti = 0; ti < taggedComments.length; ++ti) {
            JavadocTaggedNode jtn = taggedComments[ti];
            tr.Ace.bold("jtn", jtn);
            JavadocTag tag = jtn.getTag();
            
            tr.Ace.yellow("tag", tag);
            if (tag.text.equals(JavadocTags.PARAM)) {
                tr.Ace.magenta("parameter");
                
                JavadocElement tgt = jtn.getTarget();
                tr.Ace.magenta("tgt", tgt);

                tr.Ace.yellow("parameter index: " + parameterIndex);

                if (!SimpleNodeUtil.hasChildren(_parameterList)) {
                    addViolation(MSG_PARAMETERS_DOCUMENTED_BUT_NO_CODE_PARAMETERS, tag.start, tag.end);
                    break;
                }
                else if (tgt == null) {
                    if (Options.warningLevel >= CHKLVL_TAG_CONTENT + _nodeLevel) {
                        addViolation(MSG_PARAMETER_WITHOUT_NAME, tag.start, tag.end);
                    }
                    else {
                        tr.Ace.log("function", _function);
                    }
                }
                else {
                    JavadocElement desc = jtn.getDescriptionNonTarget();
                    if (jtn.getDescriptionNonTarget() == null) {
                        if (Options.warningLevel >= CHKLVL_TAG_CONTENT + _nodeLevel) {
                            addViolation(MSG_PARAMETER_WITHOUT_DESCRIPTION, tgt.start, tgt.end);
                        }
                        else {
                            tr.Ace.log("function", _function);
                        }
                    }

                    String tgtStr = tgt.text;
                    int    index  = getMatchingParameter(tgtStr);
                    
                    tr.Ace.log("matching parameter: " + index);

                    if (index == -1) {
                        index = getClosestMatchingParameter(tgtStr);
                        tr.Ace.log("closest matching parameter: " + index);
                        tr.Ace.log("parameter index: " + parameterIndex);

                        SimpleNodeUtil.dump(_parameterList, "pppp");

                        if (index == -1) {
                            String paramType = ParameterUtil.getParameterType(_parameterList, parameterIndex);
                            tr.Ace.log("paramType: " + paramType + "; tgtStr: " + tgtStr);

                            if (tgtStr.equals(paramType)) {
                                addViolation(MSG_PARAMETER_TYPE_USED, tgt.start, tgt.end);
                                addDocumentedParameter(parameterIndex, tgt.start, tgt.end);
                            }
                            else {
                                addViolation(MSG_PARAMETER_NOT_IN_CODE, tgt.start, tgt.end);
                            }
                        }
                        else {
                            addViolation(MSG_PARAMETER_MISSPELLED, tgt.start, tgt.end);
                            addDocumentedParameter(index, tgt.start, tgt.end);
                        }
                    }
                    else {
                        addDocumentedParameter(index, tgt.start, tgt.end);
                    }   
                }
                ++parameterIndex;
            }
        }

        tr.Ace.log("documentedParameters", _documentedParameters);

        if (_parameterList == null) {
            tr.Ace.log("no parameters");
        }
        else if (isCheckable(_function, CHKLVL_PARAM_DOC_EXISTS)) {
            reportUndocumentedParameters();
        }
        else {
            tr.Ace.log("function", _function);
        }
    }

    protected void addDocumentedParameter(int index, Location start, Location end)
    {
        if (_documentedParameters.size() > 0 && ((Integer)_documentedParameters.get(_documentedParameters.size() - 1)).intValue() > index) {
            addViolation(MSG_PARAMETER_NOT_IN_CODE_ORDER, start, end);
        }
        
        Integer i = new Integer(index);
        if (_documentedParameters.contains(i)) {
            addViolation(MSG_PARAMETER_REPEATED, start, end);
        }
        else {
            _documentedParameters.add(i);
        }
    }
    
    protected void reportUndocumentedParameters()
    {
        ASTFormalParameter[] params = ParameterUtil.getParameters(_parameterList);
        
        for (int ni = 0; ni < params.length; ++ni) {
            tr.Ace.log("parameter", params[ni]);
            ASTFormalParameter param = params[ni];
            if (!_documentedParameters.contains(new Integer(ni))) {
                Token nameTk = ParameterUtil.getParameterName(param);
                addViolation(MSG_PARAMETER_NOT_DOCUMENTED, nameTk);
            }
        }
    }

    /**
     * Returns the first param in the list whose name matches the given string.
     */
    protected int getMatchingParameter(String str)
    {
        ASTFormalParameter[] params = ParameterUtil.getParameters(_parameterList);
        
        for (int ni = 0; ni < params.length; ++ni) {
            tr.Ace.log("parameter", params[ni]);
            ASTFormalParameter param = params[ni];
            Token nameTk = ParameterUtil.getParameterName(param);
            if (nameTk.image.equals(str)) {
                return ni;
            }
        }

        return -1;
    }

    /**
     * Returns the first param in the list whose name most closely matches the
     * given string.
     */
    protected int getClosestMatchingParameter(String str)
    {
        if (_parameterList == null) {
            return -1;
        }
        else {
            SpellChecker         spellChecker = new SpellChecker();
            int                  bestDistance = -1;
            int                  bestIndex    = -1;
            ASTFormalParameter[] params       = ParameterUtil.getParameters(_parameterList);
        
            for (int ni = 0; ni < params.length; ++ni) {
                tr.Ace.log("parameter", params[ni]);
                ASTFormalParameter param  = params[ni];
                Token              nameTk = ParameterUtil.getParameterName(param);
                int                dist   = spellChecker.editDistance(nameTk.image, str);

                // tr.Ace.log("edit distance(param: '" + paramTkn.image + "', str: '" + str + "'): " + dist);
            
                if (dist >= 0 && dist <= SpellChecker.DEFAULT_MAX_DISTANCE && (bestDistance == -1 || dist < bestDistance)) {
                    bestDistance = dist;
                    bestIndex    = ni;
                }
            }

            return bestIndex;
        }
    }

}
