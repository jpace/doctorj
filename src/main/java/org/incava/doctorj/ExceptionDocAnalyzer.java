package org.incava.doctorj;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.Report;
import org.incava.javadoc.*;
import org.incava.pmdx.*;
import org.incava.ijdk.lang.MathExt;
import org.incava.text.spell.SpellChecker;
import org.incava.text.spell.Spelling;
import static org.incava.ijdk.util.IUtil.*;


/**
 * Checks for violations of rules applying to exceptions.
 */
public class ExceptionDocAnalyzer extends DocAnalyzer {
    
    public final static String MSG_EXCEPTION_WITHOUT_CLASS_NAME = "Exception without class name";

    public final static String MSG_EXCEPTION_WITHOUT_DESCRIPTION = "Exception without description";

    public final static String MSG_EXCEPTIONS_NOT_ALPHABETICAL = "Exceptions not alphabetical";

    public final static String MSG_EXCEPTION_NOT_IN_THROWS_LIST = "Exception not in throws list";

    public final static String MSG_EXCEPTION_MISSPELLED = "Exception misspelled";

    public final static String MSG_EXCEPTION_NOT_DOCUMENTED = "Exception not documented";

    protected final static int CHKLVL_EXCEPTIONS_ALPHABETICAL = 2;

    protected final static int CHKLVL_EXCEPTION_DOC_EXISTS = 1;
    
    private static Map<String, Boolean> excToRuntime = new HashMap<String, Boolean>();

    private final Collection<String> reportedExceptions;

    protected final static String[] KNOWN_RUNTIME_EXCEPTIONS = new String[] {
        "java.awt.color.CMMException",
        "java.awt.color.ProfileDataException",

        "java.awt.geom.IllegalPathStateException",
        
        "java.awt.image.ImagingOpException",
        "java.awt.image.RasterFormatException",

        "java.lang.ArithmeticException",
        "java.lang.ArrayStoreException",
        "java.lang.ClassCastException",
        "java.lang.EnumConstantNotPresentException",
        "java.lang.IllegalArgumentException",
        "java.lang.IllegalMonitorStateException",
        "java.lang.IllegalStateException",
        "java.lang.IndexOutOfBoundsException",
        "java.lang.NegativeArraySizeException",
        "java.lang.NullPointerException",
        "java.lang.SecurityException",
        "java.lang.TypeNotPresentException",
        "java.lang.UnsupportedOperationException",

        "java.lang.annotation.AnnotationTypeMismatchException",
        "java.lang.annotation.IncompleteAnnotationException",
        
        "java.lang.reflect.MalformedParameterizedTypeException",
        "java.lang.reflect.UndeclaredThrowableException",

        "java.nio.BufferOverflowException",
        "java.nio.BufferUnderflowException",

        "java.security.ProviderException",

        "java.util.ConcurrentModificationException",
        "java.util.EmptyStackException",
        "java.util.MissingResourceException",
        "java.util.NoSuchElementException",
        
        "java.util.concurrent.RejectedExecutionException",

        "javax.management.JMRuntimeException",
        
        "javax.print.attribute.UnmodifiableSetException",
        
        "javax.swing.undo.CannotRedoException",
        "javax.swing.undo.CannotUndoException",
        
        "org.omg.CORBA.SystemException",
        
        "org.w3c.dom.DOMException",
        
        "org.w3c.dom.events.EventException",

        "org.w3c.dom.ls.LSException",
    };

    static {
        for (String ke : KNOWN_RUNTIME_EXCEPTIONS) {
            excToRuntime.put(ke, Boolean.TRUE);
        }
    }

    private final JavadocNode javadoc;
    private final ASTNameList throwsList;
    private final SimpleNode function;
    private final List<String> documentedExceptions;
    private final int nodeLevel;

    private Map<String, ASTImportDeclaration> importMap;

    /**
     * Creates and runs the exception documentation analyzer.
     *
     * @param report   The report to which to send violations.
     * @param javadoc  The javadoc for the function. Should not be null.
     * @param function The constructor or method.
     * @param nodeLevel The level of the javadoc node with regard to reporting errors.
     * @param warningLevel The current level for reporting errors.
     */
    public ExceptionDocAnalyzer(Report report, JavadocNode javadoc, SimpleNode function, int nodeLevel, int warningLevel) {
        super(report, warningLevel);

         this.javadoc = javadoc;
         this.throwsList = FunctionUtil.getThrowsList(function);
         this.function = function;
         this.nodeLevel = nodeLevel;
         this.importMap = null;
         this.documentedExceptions = new ArrayList<String>();
         this.reportedExceptions = new HashSet<String>();
    }
    
    public void run() {
        // foreach @throws / @exception tag:
        //  - check for target
        //  - check for description
        //  - check that target is declared, or is subclass of RuntimeException
        //  - in alphabetical order

        int warningLevel = getWarningLevel();

        boolean alphabeticalReported = false;
        String  previousException = null;
        
        SimpleNode node = this.function;
        while (node != null && !(node instanceof ASTCompilationUnit)) {
            node = SimpleNodeUtil.getParent(node);
        }
        
        ASTCompilationUnit     cu      = (ASTCompilationUnit)node;
        ASTImportDeclaration[] imports = CompilationUnitUtil.getImports(cu);
        this.importMap = makeImportMap(imports);

        for (JavadocTaggedNode jtn : this.javadoc.getTaggedComments()) {
            JavadocTag tag = jtn.getTag();

            if (!tag.text.equals(JavadocTags.EXCEPTION) && !tag.text.equals(JavadocTags.THROWS)) {
                continue;
            }

            JavadocElement tgt = jtn.getTarget();

            if (tgt == null) {
                handleViolation(CHKLVL_TAG_CONTENT, MSG_EXCEPTION_WITHOUT_CLASS_NAME, tag);
            }
            else {
                if (jtn.getDescriptionNonTarget() == null) {
                    handleViolation(CHKLVL_TAG_CONTENT, MSG_EXCEPTION_WITHOUT_DESCRIPTION, tgt);
                }
                    
                String shortName = getShortName(tgt.text);
                String fullName = tgt.text;
                Class  cls = resolveClassByName(fullName, shortName);
                
                checkAgainstCode(tag, tgt, shortName, fullName, cls);

                if (!alphabeticalReported && isReported(CHKLVL_EXCEPTIONS_ALPHABETICAL) && previousException != null && previousException.compareTo(shortName) > 0) {
                    handleViolation(CHKLVL_EXCEPTIONS_ALPHABETICAL, MSG_EXCEPTIONS_NOT_ALPHABETICAL, tgt);
                    alphabeticalReported = true;
                }
                        
                previousException = shortName;
            }
        }

        if (this.throwsList != null && isReported(CHKLVL_EXCEPTION_DOC_EXISTS)) {
            reportUndocumentedExceptions();
        }            
    }

    private boolean isReported(int level) {
        return getWarningLevel() >= level + this.nodeLevel;
    }
    
    private boolean handleViolation(int level, String msg, JavadocElement elmt) {
        return isReported(level) ? addViolation(msg, elmt.start, elmt.end) : false;
    }

    protected Class resolveClassByName(String fullName, String shortName) {
        if (fullName.indexOf('.') >= 0) {
            return loadClass(fullName);
        }
        else {
            String flNm = getExactMatch(fullName);
            if (isNotNull(flNm)) {
                return loadClass(flNm);
            }
                        
            for (String impName : this.importMap.keySet()) {
                String shImpName = getShortName(impName);
                if (shImpName.equals("*")) {
                    // try to load pkg.name
                    Class cls = loadClass(impName.substring(0, impName.indexOf("*")) + shortName);
                    if (cls != null) {
                        return cls;
                    }
                }
            }
                            
            // maybe java.lang....
            return loadClass("java.lang." + shortName);
        }
    }          
    
    protected Map<String, ASTImportDeclaration> makeImportMap(ASTImportDeclaration[] imports) {
        Map<String, ASTImportDeclaration> namesToImp = new HashMap<String, ASTImportDeclaration>();

        for (ASTImportDeclaration imp : imports) {
            StringBuilder sb = new StringBuilder();   
            Token         tk = imp.getFirstToken().next;
            
            while (tk != null) {
                if (tk == imp.getLastToken()) {
                    break;
                }
                else {
                    sb.append(tk.image);
                    tk = tk.next;
                }
            }

            namesToImp.put(sb.toString(), imp);
        }
        
        return namesToImp;
    }

    /**
     * Returns the short name of the class, e.g., Integer instead of
     * java.lang.Integer.
     */
    protected String getShortName(String name) {
        int lastDot = name.lastIndexOf('.');
        return lastDot == -1 ? name : name.substring(lastDot + 1);
    }

    protected String getExactMatch(String name) {
        for (String impName : this.importMap.keySet()) {
            String shImpName = getShortName(impName);
            if (!shImpName.equals("*") && shImpName.equals(name)) {
                return impName;
            }
        }

        return null;
    }
    
    protected Class loadClass(String clsName) {
        try {
            return Class.forName(clsName);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns whether the given class is derived from Runtimeexception or
     * Error.
     */
    protected boolean isRuntimeException(Class excClass) {
        if (excClass == null) {
            return false;
        }
        else {
            String  excName = excClass.getName();
            Boolean val = excToRuntime.get(excName);
            if (val == null) {
                val = RuntimeException.class.isAssignableFrom(excClass) || Error.class.isAssignableFrom(excClass);
                excToRuntime.put(excName, val);
            }
            return val;
        }
    }

    protected void checkAgainstCode(JavadocTag tag, JavadocElement tgt, String shortExcName, String fullExcName, Class excClass) {
        ASTName name = getMatchingException(shortExcName);
        if (name == null) {
            name = getClosestMatchingException(shortExcName);
            if (name == null) {
                if (!isRuntimeException(excClass) && (isNotNull(excClass) || !reportedExceptions.contains(fullExcName))) {
                    addViolation(MSG_EXCEPTION_NOT_IN_THROWS_LIST, tgt.start, tgt.end);
                    
                    // report it only once.
                    reportedExceptions.add(fullExcName);
                }
            }
            else {
                addViolation(MSG_EXCEPTION_MISSPELLED, tgt.start, tgt.end);
                this.documentedExceptions.add(name.getLastToken().image);
            }
        }
        else {
            this.documentedExceptions.add(shortExcName);
        }
    }
    
    protected void reportUndocumentedExceptions() {
        ASTName[] names = ThrowsUtil.getNames(this.throwsList);
        
        for (int ni = 0; ni < names.length; ++ni) {
            ASTName name = names[ni];

            // by using the last token, we disregard the package:
            Token   nameToken = name.getLastToken();
            
            if (!this.documentedExceptions.contains(nameToken.image)) {
                addViolation(MSG_EXCEPTION_NOT_DOCUMENTED, 
                             nameToken.beginLine, nameToken.beginColumn, 
                             nameToken.beginLine, nameToken.beginColumn + nameToken.image.length() - 1);
            }
        }
    }

    /**
     * Returns the first name in the list that matches the given string.
     */
    protected ASTName getMatchingException(String str) {
        if (this.throwsList == null) {
            return null;
        }
        else {
            ASTName[] names = ThrowsUtil.getNames(this.throwsList);
            
            for (ASTName name : names) {
                // (again) by using the last token, we disregard the package:
                Token nameToken = name.getLastToken();
                if (nameToken.image.equals(str)) {
                    return name;
                }
            }
            return null;
        }
    }

    /**
     * Returns the name in the list that most closely matches the given string.
     */
    protected ASTName getClosestMatchingException(String str) {
        if (this.throwsList == null) {
            return null;
        }
        else {
            Integer   bestDistance = null;
            ASTName   bestName = null;
            ASTName[] names = ThrowsUtil.getNames(this.throwsList);
            
            for (ASTName name : names) {
                Token nameToken = name.getLastToken();
                int   dist = Spelling.getEditDistance(nameToken.image, str);
                
                if (MathExt.isWithin(dist, 0, SpellChecker.DEFAULT_MAX_DISTANCE) && (isNull(bestDistance) || dist < bestDistance)) {
                    bestDistance = dist;
                    bestName = name;
                }
            }

            return bestName;
        }
    }
}
