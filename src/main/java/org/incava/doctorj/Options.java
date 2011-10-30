package org.incava.doctorj;

import java.io.*;
import java.util.*;
import org.incava.analysis.ContextReport;
import org.incava.ijdk.lang.StringExt;
import org.incava.jagol.*;


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
public class Options extends OptionSet {
    
    public static int MAXIMUM_WARNING_LEVEL = 100;

    public static int MINIMUM_WARNING_LEVEL = -1;

    public static int DEFAULT_WARNING_LEVEL = 4;

    /**
     * The strictness level, for warnings.
     */
    public static int warningLevel = DEFAULT_WARNING_LEVEL;

    /**
     * Whether to use single - line (emacs) or multi - line (non - emacs) output
     * format. The emacs form is most suitable for IDEs, which expect
     * single - line output.
     */
    public static boolean emacsOutput = false;

    /**
     * Whether to check comments.
     */
    public static boolean checkComments = true;

    /**
     * Whether to check strings.
     */
    public static boolean checkStrings = true;

    /**
     * The version.
     */
    public static String VERSION = "5.2.0";

    /**
     * The Java source version.
     */
    public static String source = "1.4";

    /**
     * The warning level option.
     */
    private final IntegerOption levelOpt;

    /**
     * The warning level option.
     */
    private final IntegerOption warningOpt;

    // /**
    //  * Option to set warning level to the equivalent of --level = 0.
    //  */
    // private final BooleanOption errorsOpt;

//     /**
//      * Option to set warning level to the equivalent of --level = 1.
//      */
//     private BooleanOption publicOpt;

//     /**
//      * Option to set warning level to the equivalent of --level = 2.
//      */
//     private BooleanOption protectedOpt;

//     /**
//      * Option to set warning level to the equivalent of --level = 3.
//      */
//     private BooleanOption packageOpt;

//     /**
//      * Option to set warning level to the equivalent of --level = 4.
//      */
//     private BooleanOption privateOpt;

//     /**
//      * Option to set warning level to the equivalent of --level = max.
//      */
//     private BooleanOption pedanticOpt;

    /**
     * The emacs option.
     */
    private final BooleanOption emacsOpt;

    /**
     * The comments option.
     */
    private final BooleanOption commentsOpt;

    /**
     * The strings option.
     */
    private final BooleanOption stringsOpt;

    /**
     * The tab width option.
     */
    private final IntegerOption tabWidthOpt;

    /**
     * The verbose option.
     */
    private final BooleanOption verboseOpt;

    /**
     * The list of word - list (dictionary) files.
     */
    private final ListOption dictOpt;

    /**
     * The version option.
     */
    private final BooleanOption versionOpt;

    /**
     * The source option.
     */
    private final StringOption sourceOpt;

    private static Options instance = new Options();

    public static Options getInstance() {
        return instance;
    }

    protected Options() {
        super("doctorj", "Analyzes and validates Java and Javadoc");

        // use the doctorj.* equivalent property for each option.

        Boolean emacs = new Boolean(emacsOutput);
        String emacsProperty = System.getProperty("doctorj.emacs");
        if (emacsProperty != null) {
            emacs = new Boolean(emacsProperty);
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

        List<String> wordLists = new ArrayList<String>();
        String dirProperty = System.getProperty("doctorj.dir");
        
        if (dirProperty != null) {
            Locale locale = Locale.getDefault();
            String wordListFile = dirProperty + "/words." + locale;
            
            wordLists.add(wordListFile);
        }
        
        String dictProperty = System.getProperty("doctorj.dictionaries");
        tr.Ace.log("dictProperty", dictProperty);
        
        if (dictProperty != null) {
            for (String wl : StringExt.listify(dictProperty)) {
                wordLists.add(wl);
            }
        }
        
        add(this.emacsOpt = new BooleanOption("emacs",     "whether to list violations in Emacs form (single line)",                 emacs));
        add(this.levelOpt = new IntegerOption("level",     "the level of warnings to be output, with 0 being to check only errors",  lvl));
        add(this.warningOpt = new IntegerOption("warning",   "same as --level",                                                        lvl));

//         add(this.errorsOpt = new BooleanOption("errors",    "report only errors; equivalent to warning level of 0"));
//         add(this.publicOpt = new BooleanOption("public",    "public methods; equivalent to warning level of 1"));
//         add(this.protectedOpt = new BooleanOption("protected", "protected methods; equivalent to warning level of 2"));
//         add(this.packageOpt = new BooleanOption("package",   "package methods; equivalent to warning level of 3"));
//         add(this.privateOpt = new BooleanOption("private",   "private methods; warning level of 4"));
//         add(this.pedanticOpt = new BooleanOption("pedantic",  "warning level at max"));

        add(this.tabWidthOpt = new IntegerOption("tabwidth",  "the number of spaces to treat tabs equal to",                            tabWidth));
        add(this.dictOpt = new ListOption("dictionaries", "the list of dictionary (word list) files",                               wordLists));
        add(this.verboseOpt = new BooleanOption("verbose",   "whether to run in verbose mode (for debugging)",                         verbose));
        add(this.versionOpt = new BooleanOption("version",   "Displays the version"));
        add(this.sourceOpt = new StringOption("source",     "the Java source version; either 1.3, 1.4 (the default), or 1.5",         source));

        add(this.commentsOpt = new BooleanOption("comments",     "whether to analyze comments; default is true",         checkComments));
        add(this.stringsOpt = new BooleanOption("strings",     "whether to analyze strings; default is true",         checkStrings));

        this.versionOpt.setShortName('v');
        
        addRunControlFile("/etc/doctorj.conf");
        addRunControlFile("~/.doctorjrc");
    }

    /**
     * Processes the run control files and command line arguments, and sets the
     * static variables. Returns the arguments that were not consumed by option
     * processing.
     */
    public String[] process(String[] args) {
        tr.Ace.log("args: " + args);
        String[] unprocessed = super.process(args);

        Integer tabWidthInt = this.tabWidthOpt.getValue();
        if (tabWidthInt != null) {
            tr.Ace.log("setting tab width: " + tabWidthInt);
            ContextReport.tabWidth = tabWidthInt.intValue();
        }
    
        Integer levelInt = this.levelOpt.getValue();
        if (levelInt != null) {
            tr.Ace.log("setting warning level: " + levelInt);
            warningLevel = levelInt.intValue();
        }

//         if (this.errorsOpt.getValue() != null) {
//             warningLevel = 0;
//         }
//         else if (this.publicOpt.getValue() != null) {
//             warningLevel = 1;
//         }
//         else if (this.protectedOpt.getValue() != null) {
//             warningLevel = 2;
//         }
//         else if (this.packageOpt.getValue() != null) {
//             warningLevel = 3;
//         }
//         else if (this.privateOpt.getValue() != null) {
//             warningLevel = 4;
//         }
//         else if (this.pedanticOpt.getValue() != null) {
//             warningLevel = MAXIMUM_WARNING_LEVEL;
//         }
        
        Boolean emacsBool = this.emacsOpt.getValue();
        if (emacsBool != null) {
            tr.Ace.log("setting output format: " + emacsBool);
            emacsOutput = emacsBool.booleanValue();
        }
        
        Boolean verboseBool = this.verboseOpt.getValue();
        if (verboseBool != null) {
            tr.Ace.setVerbose(verboseBool.booleanValue());
        }

        Boolean versionBool = this.versionOpt.getValue();
        if (versionBool != null) {
            System.out.println("doctorj, version " + VERSION);
            System.out.println("Written by Jeff Pace (jpace [at] incava [dot] org)");
            System.out.println("Released under the Lesser GNU Public License");
            System.exit(0);
        }

        List<String> dictList = this.dictOpt.getValue();
        if (dictList != null) {
            for (String dict : dictList) {
                SpellingAnalyzer.getInstance().addDictionary(dict);
            }
        }

        String sourceStr = this.sourceOpt.getValue();
        if (sourceStr != null) {
            tr.Ace.log("sourceStr", sourceStr);
            source = sourceStr;
        }

        Boolean commentsBool = this.commentsOpt.getValue();
        if (commentsBool != null) {
            checkComments = commentsBool.booleanValue();
        }

        Boolean stringsBool = this.stringsOpt.getValue();
        if (stringsBool != null) {
            checkStrings = stringsBool.booleanValue();
        }

        tr.Ace.blue("unprocessed", unprocessed);
        
        return unprocessed;
    }

}
