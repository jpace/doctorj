package org.incava.doctorj;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.Report;
import org.incava.javadoc.*;
import org.incava.pmdx.*;
import org.incava.text.spell.SpellChecker;


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
        for (int ki = 0; ki < KNOWN_RUNTIME_EXCEPTIONS.length; ++ki) {
            String ke = KNOWN_RUNTIME_EXCEPTIONS[ki];
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
     */
    public ExceptionDocAnalyzer(Report report, JavadocNode javadoc, SimpleNode function, int nodeLevel) {
        super(report);

         this.javadoc = javadoc;
         this.throwsList = FunctionUtil.getThrowsList(function);
         this.function = function;
         this.nodeLevel = nodeLevel;
         this.importMap = null;
         this.documentedExceptions = new ArrayList<String>();
         this.reportedExceptions = new ArrayList<String>();
    }
    
    public void run() {
        // foreach @throws / @exception tag:
        //  - check for target
        //  - check for description
        //  - check that target is declared, or is subclass of RuntimeException
        //  - in alphabetical order

        boolean alphabeticalReported = false;
        String  previousException = null;
        
        SimpleNode node = this.function;
        while (node != null && !(node instanceof ASTCompilationUnit)) {
            node = SimpleNodeUtil.getParent(node);
        }
        
        ASTCompilationUnit     cu      = (ASTCompilationUnit)node;
        ASTImportDeclaration[] imports = CompilationUnitUtil.getImports(cu);
        this.importMap = makeImportMap(imports);

        JavadocTaggedNode[] taggedComments = this.javadoc.getTaggedComments();
        for (int ti = 0; ti < taggedComments.length; ++ti) {
            JavadocTaggedNode jtn = taggedComments[ti];            
            tr.Ace.log("jtn", jtn);
            JavadocTag        tag = jtn.getTag();
            tr.Ace.log("tag", tag);

            if (tag.text.equals(JavadocTags.EXCEPTION) || tag.text.equals(JavadocTags.THROWS)) {
                JavadocElement tgt = jtn.getTarget();
                tr.Ace.log("tgt", tgt);

                if (tgt == null) {
                    if (Options.warningLevel >= CHKLVL_TAG_CONTENT + this.nodeLevel) {
                        addViolation(MSG_EXCEPTION_WITHOUT_CLASS_NAME, tag.start, tag.end);
                    }
                }
                else {
                    if (jtn.getDescriptionNonTarget() == null && Options.warningLevel >= CHKLVL_TAG_CONTENT + this.nodeLevel) {
                        addViolation(MSG_EXCEPTION_WITHOUT_DESCRIPTION, tgt.start, tgt.end);
                    }
                    
                    String shortName = getShortName(tgt.text);
                    String fullName = tgt.text;
                    Class  cls = null;
                    
                    if (fullName.indexOf('.') >= 0) {
                        cls = loadClass(fullName);
                    }
                    else {
                        fullName = getExactMatch(fullName);
                        
                        if (fullName == null) {
                            Iterator<String> iit = this.importMap.keySet().iterator();
                            while (cls == null && iit.hasNext()) {
                                String impName = iit.next();
                                String shImpName = getShortName(impName);
                                if (shImpName.equals("*")) {
                                    // try to load pkg.name
                                    fullName = impName.substring(0, impName.indexOf("*")) + shortName;
                                    cls = loadClass(fullName);
                                }
                            }
                            
                            if (cls == null) {
                                // maybe java.lang....
                                fullName = "java.lang." + shortName;
                                cls = loadClass(fullName);
                            }
                        }
                        else {
                            cls = loadClass(fullName);
                        }
                    }
                    
                    checkAgainstCode(tag, tgt, shortName, fullName, cls);

                    if (!alphabeticalReported && 
                        Options.warningLevel >= CHKLVL_EXCEPTIONS_ALPHABETICAL + this.nodeLevel && 
                        previousException != null && previousException.compareTo(shortName) > 0) {
                        
                        addViolation(MSG_EXCEPTIONS_NOT_ALPHABETICAL, tgt.start, tgt.end);
                        alphabeticalReported = true;
                    }
                        
                    previousException = shortName;
                }
            }
        }

        if (this.throwsList != null && Options.warningLevel >= CHKLVL_EXCEPTION_DOC_EXISTS + this.nodeLevel) {
            reportUndocumentedExceptions();
        }            
    }

    protected Map<String, ASTImportDeclaration> makeImportMap(ASTImportDeclaration[] imports) {
        Map<String, ASTImportDeclaration> namesToImp = new HashMap<String, ASTImportDeclaration>();

        for (int ii = 0; ii < imports.length; ++ii) {
            ASTImportDeclaration imp = imports[ii];
            StringBuffer         buf = new StringBuffer();   
            Token                tk = imp.getFirstToken().next;
            
            while (tk != null) {
                if (tk == imp.getLastToken()) {
                    break;
                }
                else {
                    buf.append(tk.image);
                    tk = tk.next;
                }
            }

            namesToImp.put(buf.toString(), imp);
        }
        
        return namesToImp;
    }

    /**
     * Returns the short name of the class, e.g., Integer instead of
     * java.lang.Integer.
     */
    protected String getShortName(String name) {
        int    lastDot = name.lastIndexOf('.');
        String shName = lastDot == -1 ? name : name.substring(lastDot + 1);
        return shName;
    }

    protected String getExactMatch(String name) {
        Iterator<String> iit = this.importMap.keySet().iterator();
        while (iit.hasNext()) {
            String impName = iit.next();
            String shImpName = getShortName(impName);
            if (!shImpName.equals("*") && shImpName.equals(name)) {
                return impName;
            }
        }

        return null;
    }
    
    protected Class loadClass(String clsName) {
        try {
            Class cls = Class.forName(clsName);
            return cls;
        }
        catch (Exception e) {
            // e.printStackTrace();
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
                val = new Boolean(RuntimeException.class.isAssignableFrom(excClass) || 
                                  Error.class.isAssignableFrom(excClass));
                excToRuntime.put(excName, val);
            }
            return val.booleanValue();
        }
    }

    protected void checkAgainstCode(JavadocTag tag, JavadocElement tgt, String shortExcName, String fullExcName, Class excClass) {
        ASTName name = getMatchingException(shortExcName);
        tr.Ace.log("name", name);
        if (name == null) {
            name = getClosestMatchingException(shortExcName);
            tr.Ace.log("name", name);
            if (name == null) {
                tr.Ace.log("name", name);
                if (isRuntimeException(excClass)) {
                    tr.Ace.log("excClass", excClass);
                    // don't report it.
                }
                else if (excClass != null || !reportedExceptions.contains(fullExcName)) {
                    tr.Ace.log("reportedExceptions", reportedExceptions);

                    tr.Ace.onRed("fullExcName", fullExcName);
                    // this violation is an error, not a warning:
                    addViolation(MSG_EXCEPTION_NOT_IN_THROWS_LIST, tgt.start, tgt.end);
                    
                    // report it only once.
                    reportedExceptions.add(fullExcName);
                }
                else {
                    tr.Ace.onRed("fullExcName", fullExcName);
                    tr.Ace.onRed("shortExcName", shortExcName);

                    // we don't report exceptions when we don't know if they're
                    // run-time exceptions, or when they've already been
                    // reported.
                }
            }
            else {
                // this violation is an error:
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
            
            tr.Ace.log("considering name: " + name + " (" + nameToken + ")");
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
            
            for (int ni = 0; ni < names.length; ++ni) {
                ASTName name = names[ni];
                // (again) by using the last token, we disregard the package:
                Token   nameToken = name.getLastToken();
                tr.Ace.log("considering name: " + name + " (" + nameToken + ")");
                if (nameToken.image.equals(str)) {
                    return name;
                }
            }
            tr.Ace.log("no exact match for '" + str + "'");
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
            SpellChecker spellChecker = new SpellChecker();
            int          bestDistance = -1;
            ASTName      bestName = null;
            ASTName[]    names = ThrowsUtil.getNames(this.throwsList);
            
            for (int ni = 0; ni < names.length; ++ni) {
                ASTName name = names[ni];
                Token   nameToken = name.getLastToken();
                int     dist = spellChecker.editDistance(nameToken.image, str);
            
                if (dist >= 0 && dist <= SpellChecker.DEFAULT_MAX_DISTANCE && (bestDistance == -1 || dist < bestDistance)) {
                    bestDistance = dist;
                    bestName = name;
                }
            }

            return bestName;
        }
    }

}
