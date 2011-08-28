package org.incava.doctorj;

import java.io.*;
import java.util.*;
import org.incava.analysis.ContextReport;
import org.incava.jagol.*;
import org.incava.lang.StringExt;


/**
 * Options for Javadoc processing, supporting:
 *
 *     doctorj             (just errors)
 *     doctorj --public    (warning level of 0; public methods)
 *     doctorj --protected (warning level of 1)
 *     doctorj --package   (warning level of 2)
 *     doctorj --private   (warning level of 3)
 *     doctorj --pedantic  (warning level at max)
 */
public class Options extends OptionSet
{
    public static int MAXIMUM_WARNING_LEVEL = 100;

    public static int MINIMUM_WARNING_LEVEL = -1;

    public static int DEFAULT_WARNING_LEVEL = 4;

    /**
     * The strictness level, for warnings.
     */
    public static int warningLevel = DEFAULT_WARNING_LEVEL;

    /**
     * Whether to use single-line (emacs) or multi-line (non-emacs) output
     * format. The emacs form is most suitable for IDEs, which expect
     * single-line output.
     */
    public static boolean emacsOutput = false;

    /**
     * The version.
     */
    public static String VERSION = "5.1.2";

    /**
     * The Java source version.
     */
    public static String source = "1.4";

    /**
     * The warning level option.
     */
    private IntegerOption _levelOpt;

    /**
     * The warning level option.
     */
    private IntegerOption _warningOpt;

    /**
     * Option to set warning level to the equivalent of --level=0.
     */
    private BooleanOption _errorsOpt;

//     /**
//      * Option to set warning level to the equivalent of --level=1.
//      */
//     private BooleanOption _publicOpt;

//     /**
//      * Option to set warning level to the equivalent of --level=2.
//      */
//     private BooleanOption _protectedOpt;

//     /**
//      * Option to set warning level to the equivalent of --level=3.
//      */
//     private BooleanOption _packageOpt;

//     /**
//      * Option to set warning level to the equivalent of --level=4.
//      */
//     private BooleanOption _privateOpt;

//     /**
//      * Option to set warning level to the equivalent of --level=max.
//      */
//     private BooleanOption _pedanticOpt;

    /**
     * The emacs option.
     */
    private BooleanOption _emacsOpt;

    /**
     * The tab width option.
     */
    private IntegerOption _tabWidthOpt;

    /**
     * The verbose option.
     */
    private BooleanOption _verboseOpt;

    /**
     * The list of word-list (dictionary) files.
     */
    private ListOption _dictOpt;

    /**
     * The version option.
     */
    private BooleanOption _versionOpt;

    /**
     * The source option.
     */
    private StringOption _sourceOpt;

    private static Options instance = null;

    public static Options get()
    {
        if (instance == null) {
            instance = new Options();
        }
        return instance;
    }

    protected Options()
    {
        super("doctorj", "Analyzes and validates Java and Javadoc");

        // use the doctorj.* equivalent property for each option.

        Boolean emacs = new Boolean(emacsOutput);
        String emacsProperty = System.getProperty("doctorj.emacs");
        tr.Ace.log("emacs property: " + emacsProperty);
        if (emacsProperty != null) {
            emacs = new Boolean(emacsProperty);
            tr.Ace.log("emacs: " + emacs);
        }

        Integer lvl = new Integer(warningLevel);
        String warningLvlProperty = System.getProperty("doctorj.level");
        if (warningLvlProperty == null) {
            warningLvlProperty = System.getProperty("doctorj.warning");
        }
        
        if (warningLvlProperty != null) {
            lvl = new Integer(warningLvlProperty);
        }
        
        Integer tabWidth = new Integer(ContextReport.tabWidth);
        String tabWidthProperty = System.getProperty("doctorj.tabwidth");
        if (tabWidthProperty != null) {
            tabWidth = new Integer(tabWidthProperty);
        }

        Boolean verbose = new Boolean(false);
        String verboseProperty = System.getProperty("doctorj.verbose");
        if (verboseProperty != null) {
            verbose = new Boolean(verboseProperty);
        }

        List   wordLists = new ArrayList();
        String dirProperty = System.getProperty("doctorj.dir");
        tr.Ace.log("dirProperty: " + dirProperty);
        
        if (dirProperty != null) {
            Locale locale = Locale.getDefault();
            tr.Ace.log("locale: " + locale);
            
            String wordListFile = dirProperty + "/words." + locale;
            tr.Ace.log("wordListFile: " + wordListFile);
            
            wordLists.add(wordListFile);
        }
        
        String dictProperty = System.getProperty("doctorj.dictionaries");
        tr.Ace.log("dictProperty", dictProperty);
        
        if (dictProperty != null) {
            List     list = StringExt.listify(dictProperty);
            Iterator it   = list.iterator();
            while (it.hasNext()) {
                String s = (String)it.next();
                wordLists.add(s);
            }
        }
        
        add(_emacsOpt     = new BooleanOption("emacs",     "whether to list violations in Emacs form (single line)",                 emacs));
        add(_levelOpt     = new IntegerOption("level",     "the level of warnings to be output, with 0 being to check only errors",  lvl));
        add(_warningOpt   = new IntegerOption("warning",   "same as --level",                                                        lvl));

//         add(_errorsOpt    = new BooleanOption("errors",    "report only errors; equivalent to warning level of 0"));
//         add(_publicOpt    = new BooleanOption("public",    "public methods; equivalent to warning level of 1"));
//         add(_protectedOpt = new BooleanOption("protected", "protected methods; equivalent to warning level of 2"));
//         add(_packageOpt   = new BooleanOption("package",   "package methods; equivalent to warning level of 3"));
//         add(_privateOpt   = new BooleanOption("private",   "private methods; warning level of 4"));
//         add(_pedanticOpt  = new BooleanOption("pedantic",  "warning level at max"));

        add(_tabWidthOpt = new IntegerOption("tabwidth",  "the number of spaces to treat tabs equal to",                            tabWidth));
        add(_dictOpt     = new ListOption("dictionaries", "the list of dictionary (word list) files",                               wordLists));
        add(_verboseOpt  = new BooleanOption("verbose",   "whether to run in verbose mode (for debugging)",                         verbose));
        add(_versionOpt  = new BooleanOption("version",   "Displays the version"));
        add(_sourceOpt   = new StringOption("source",     "the Java source version; either 1.3, 1.4 (the default), or 1.5",         source));
        _versionOpt.setShortName('v');
        
        addRunControlFile("/etc/doctorj.conf");
        addRunControlFile("~/.doctorjrc");
    }

    /**
     * Processes the run control files and command line arguments, and sets the
     * static variables. Returns the arguments that were not consumed by option
     * processing.
     */
    public String[] process(String[] args)
    {
        tr.Ace.log("args: " + args);
        String[] unprocessed = super.process(args);

        Integer tabWidthInt = _tabWidthOpt.getValue();
        if (tabWidthInt != null) {
            tr.Ace.log("setting tab width: " + tabWidthInt);
            ContextReport.tabWidth = tabWidthInt.intValue();
        }
    
        Integer levelInt = _levelOpt.getValue();
        if (levelInt != null) {
            tr.Ace.log("setting warning level: " + levelInt);
            warningLevel = levelInt.intValue();
        }

//         if (_errorsOpt.getValue() != null) {
//             warningLevel = 0;
//         }
//         else if (_publicOpt.getValue() != null) {
//             warningLevel = 1;
//         }
//         else if (_protectedOpt.getValue() != null) {
//             warningLevel = 2;
//         }
//         else if (_packageOpt.getValue() != null) {
//             warningLevel = 3;
//         }
//         else if (_privateOpt.getValue() != null) {
//             warningLevel = 4;
//         }
//         else if (_pedanticOpt.getValue() != null) {
//             warningLevel = MAXIMUM_WARNING_LEVEL;
//         }
        
        Boolean emacsBool = _emacsOpt.getValue();
        if (emacsBool != null) {
            tr.Ace.log("setting output format: " + emacsBool);
            emacsOutput = emacsBool.booleanValue();
        }
        
        Boolean verboseBool = _verboseOpt.getValue();
        if (verboseBool != null) {
            tr.Ace.setVerbose(verboseBool.booleanValue());
        }

        Boolean versionBool = _versionOpt.getValue();
        if (versionBool != null) {
            System.out.println("doctorj, version " + VERSION);
            System.out.println("Written by Jeff Pace (jpace [at] incava [dot] org)");
            System.out.println("Released under the Lesser GNU Public License");
            System.exit(0);
        }

        List dictList = _dictOpt.getValue();
        if (dictList != null) {
            Iterator it = dictList.iterator();
            while (it.hasNext()) {
                String dict = (String)it.next();
                ItemDocAnalyzer.spellChecker.addDictionary(dict);
            }
        }

        String sourceStr = _sourceOpt.getValue();
        if (sourceStr != null) {
            tr.Ace.log("sourceStr", sourceStr);
            source = sourceStr;
        }

        tr.Ace.blue("unprocessed", unprocessed);
        
        return unprocessed;
    }

}
