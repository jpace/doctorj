package org.incava.doctorj;

import java.io.*;
import java.util.*;
import org.incava.analysis.ContextReport;
import org.incava.ijdk.io.FileExt;
import org.incava.ijdk.lang.StringExt;
import org.incava.jagol.*;
import static org.incava.ijdk.util.IUtil.*;

/**
 * Options for processing Javadoc and strings.
 */
public class Options extends OptionSet {
    
    public static int MAXIMUM_WARNING_LEVEL = 100;

    public static int MINIMUM_WARNING_LEVEL = -1;

    public static int DEFAULT_WARNING_LEVEL = 4;

    public final static String VERSION = "5.2.0";

    /**
     * The strictness level, for warnings.
     */
    private int warningLevel = DEFAULT_WARNING_LEVEL;

    /**
     * Whether to use single-line (emacs) or multi-line (non-emacs) output
     * format. The emacs form is most suitable for IDEs, which expect
     * single-line output.
     */
    private boolean emacsOutput = false;

    private boolean checkComments = true;

    private boolean checkStrings = true;

    private int minWords = 2;

    private String source = "1.5";

    private final IntegerOption levelOpt;

    private final IntegerOption warningOpt;

    private final BooleanOption emacsOpt;

    private final BooleanOption commentsOpt;

    private final BooleanOption stringsOpt;

    private final IntegerOption tabWidthOpt;

    private final BooleanOption verboseOpt;

    /**
     * The list of word-list (dictionary) files.
     */
    private final ListOption dictOpt;

    private final BooleanOption versionOpt;

    private final IntegerOption minWordsOpt;

    private final StringOption sourceOpt;

    public Options() {
        super("doctorj", "Analyzes and validates Java and Javadoc");

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
        
        if (dictProperty != null) {
            for (String wl : StringExt.toList(dictProperty)) {
                wordLists.add(wl);
            }
        }
        
        this.emacsOpt    = addOption("emacs",     "whether to list violations in Emacs form (single line)",                 emacs);
        this.levelOpt    = addOption("level",     "the level of warnings to be output, with 0 being to check only errors",  lvl);
        this.warningOpt  = addOption("warning",   "same as --level",                                                        lvl);
        
        this.tabWidthOpt = addOption("tabwidth",  "the number of spaces to treat tabs equal to",                            tabWidth);
        this.dictOpt     = addOption("dictionaries", "the list of dictionary (word list) files",                            wordLists);
        this.verboseOpt  = addOption("verbose",   "whether to run in verbose mode (for debugging)",                         verbose);
        this.versionOpt  = addOption(new BooleanOption("version",   "Displays the version", 'v'));
        this.sourceOpt   = addOption("source",     "the Java source version; either 1.3, 1.4, or 1.5 (the default)",        this.source);
        
        this.commentsOpt = addOption("comments",  "whether to analyze comments; default is true",                           this.checkComments);
        this.stringsOpt  = addOption("strings",   "whether to analyze strings; default is true",                            this.checkStrings);
        this.minWordsOpt = addOption("minwords",  "the minimum number of words in a string for it to be spell-checked; default is 2", this.minWords);
        
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

    public int getMinimumWords() {
        return this.minWords;
    }

    /**
     * Processes the run control files and command line arguments, and sets the
     * static variables. Returns the arguments that were not consumed by option
     * processing.
     */
    public List<String> process(List<String> args) {
        List<String> unprocessed = super.process(args);

        Integer tabWidthInt = this.tabWidthOpt.getValue();
        if (tabWidthInt != null) {
            ContextReport.tabWidth = tabWidthInt;
        }
    
        Integer levelInt = this.levelOpt.getValue();
        if (levelInt != null) {
            this.warningLevel = levelInt;
        }
        
        Boolean emacsBool = this.emacsOpt.getValue();
        if (emacsBool != null) {
            this.emacsOutput = emacsBool;
        }
        
        Boolean verboseBool = this.verboseOpt.getValue();
        if (verboseBool != null) {
            tr.Ace.setVerbose(verboseBool);
        }

        Boolean versionBool = this.versionOpt.getValue();
        if (versionBool != null) {
            System.out.println("doctorj, version " + VERSION);
            System.out.println("Written by Jeff Pace (jeugenepace [at] gmail [dot] com)");
            System.out.println("Released under the Lesser GNU Public License");
            System.exit(0);
        }

        SpellingAnalyzer speller = SpellingAnalyzer.getInstance();
        
        List<String> dictList = this.dictOpt.getValue();
        for (String dict : dictList) {
            speller.addDictionary(dict);
        }

        String sourceStr = this.sourceOpt.getValue();
        if (sourceStr != null) {
            source = sourceStr;
        }

        Boolean commentsBool = this.commentsOpt.getValue();
        if (commentsBool != null) {
            this.checkComments = commentsBool;
        }

        Boolean stringsBool = this.stringsOpt.getValue();
        if (stringsBool != null) {
            this.checkStrings = stringsBool;
        }

        Integer minWords = this.minWordsOpt.getValue();
        if (minWords != null) {
            this.minWords = minWords;
        }

        return unprocessed;
    }
}
