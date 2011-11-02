package org.incava.doctorj;

import java.io.*;
import java.util.*;
import org.incava.analysis.ContextReport;
import org.incava.ijdk.lang.StringExt;
import org.incava.jagol.*;
import static org.incava.ijdk.util.IUtil.*;


/**
 * Options for Javadoc processing.
 */
public class Options extends OptionSet {
    
    public static int MAXIMUM_WARNING_LEVEL = 100;

    public static int MINIMUM_WARNING_LEVEL = -1;

    public static int DEFAULT_WARNING_LEVEL = 4;

    /**
     * The version.
     */
    public final static String VERSION = "5.2.0";

    /**
     * The strictness level, for warnings.
     */
    private int warningLevel = DEFAULT_WARNING_LEVEL;

    /**
     * Whether to use single - line (emacs) or multi - line (non - emacs) output
     * format. The emacs form is most suitable for IDEs, which expect
     * single - line output.
     */
    private boolean emacsOutput = false;

    /**
     * Whether to check comments.
     */
    private boolean checkComments = true;

    /**
     * Whether to check strings.
     */
    private boolean checkStrings = true;

    /**
     * The Java source version.
     */
    private String source = "1.4";

    /**
     * The warning level option.
     */
    private final IntegerOption levelOpt;

    /**
     * The warning level option.
     */
    private final IntegerOption warningOpt;

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

    public Options() {
        super("doctorj", "Analyzes and validates Java and Javadoc");

        // use the doctorj.* equivalent property for each option.

        Boolean emacs = this.emacsOutput;
        String emacsProperty = System.getProperty("doctorj.emacs");
        if (emacsProperty != null) {
            emacs = new Boolean(emacsProperty);
        }

        Integer lvl = this.warningLevel;
        String warningLvlProperty = or(System.getProperty("doctorj.level"), System.getProperty("doctorj.warning"));
        
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
            for (String wl : StringExt.toList(dictProperty)) {
                wordLists.add(wl);
            }
        }
        
        add(this.emacsOpt    = new BooleanOption("emacs",     "whether to list violations in Emacs form (single line)",                 emacs));
        add(this.levelOpt    = new IntegerOption("level",     "the level of warnings to be output, with 0 being to check only errors",  lvl));
        add(this.warningOpt  = new IntegerOption("warning",   "same as --level",                                                        lvl));

        add(this.tabWidthOpt = new IntegerOption("tabwidth",  "the number of spaces to treat tabs equal to",                            tabWidth));
        add(this.dictOpt     = new ListOption("dictionaries", "the list of dictionary (word list) files",                               wordLists));
        add(this.verboseOpt  = new BooleanOption("verbose",   "whether to run in verbose mode (for debugging)",                         verbose));
        add(this.versionOpt  = new BooleanOption("version",   "Displays the version"));
        add(this.sourceOpt   = new StringOption("source",     "the Java source version; either 1.3, 1.4 (the default), or 1.5",         this.source));

        add(this.commentsOpt = new BooleanOption("comments",  "whether to analyze comments; default is true",                           this.checkComments));
        add(this.stringsOpt  = new BooleanOption("strings",   "whether to analyze strings; default is true",                            this.checkStrings));

        this.versionOpt.setShortName('v');
        
        addRunControlFile("/etc/doctorj.conf");
        addRunControlFile("~/.doctorjrc");
    }

    /**
     * Returns the strictness level.
     */
    public int getWarningLevel() {
        return this.warningLevel;
    }

    /**
     * Sets the strictness level.
     */
    public void setWarningLevel(int level) {
        this.warningLevel = level;
    }

    public boolean getEmacsOutput() {
        return this.emacsOutput;
    }

    public boolean checkComments() {
        return this.checkComments;
    }

    public boolean checkStrings() {
        return this.checkStrings;
    }

    public String getSource() {
        return source;
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
            ContextReport.tabWidth = tabWidthInt.intValue();
        }
    
        Integer levelInt = this.levelOpt.getValue();
        if (levelInt != null) {
            this.warningLevel = levelInt.intValue();
        }
        
        Boolean emacsBool = this.emacsOpt.getValue();
        if (emacsBool != null) {
            this.emacsOutput = emacsBool.booleanValue();
        }
        
        Boolean verboseBool = this.verboseOpt.getValue();
        if (verboseBool != null) {
            tr.Ace.setVerbose(verboseBool.booleanValue());
        }

        Boolean versionBool = this.versionOpt.getValue();
        if (versionBool != null) {
            System.out.println("doctorj, version " + VERSION);
            System.out.println("Written by Jeff Pace (jeugenepace [at] gmail [dot] com)");
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
            this.checkComments = commentsBool.booleanValue();
        }

        Boolean stringsBool = this.stringsOpt.getValue();
        if (stringsBool != null) {
            this.checkStrings = stringsBool.booleanValue();
        }
        
        return unprocessed;
    }

}
