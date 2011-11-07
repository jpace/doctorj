package org.incava.jagol;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.incava.ijdk.io.FileExt;
import static org.incava.ijdk.util.IUtil.*;

/**
 * A group of options.
 */
public class OptionSet {
    private final List<Option<?>> options;

    private final List<String> rcFileNames;

    private final String appName;
    
    private final String description;
    
    public OptionSet(String appName, String description) {
        this.appName = appName;
        this.description = description;
        this.options = new ArrayList<Option<?>>();
        this.rcFileNames = new ArrayList<String>();
    }
    
    /**
     * Returns the application name.
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Adds an options to this set.
     */
    public <T extends Option> T addOption(T opt) {
        options.add(opt);
        return opt;
    }

    public BooleanOption addOption(String name, String desc, Boolean value) {
        return addOption(new BooleanOption(name, desc, value));
    }

    public BooleanOption addBooleanOption(String name, String desc) {
        return addOption(new BooleanOption(name, desc));
    }

    public IntegerOption addOption(String name, String desc, Integer value) {
        return addOption(new IntegerOption(name, desc, value));
    }

    public ListOption addOption(String name, String desc, List<String> value) {
        return addOption(new ListOption(name, desc, value));
    }

    public StringOption addOption(String name, String desc, String value) {
        return addOption(new StringOption(name, desc, value));
    }

    /**
     * Adds a run control file to be processed.
     */
    public void addRunControlFile(String name) {
        rcFileNames.add(name);
    }

    /**
     * Processes the run control files and command line arguments. Returns the
     * arguments that were not consumed by option processing.
     */
    public List<String> process(List<String> args) {
        processRunControlFiles();
        return processCommandLine(args);
    }

    /**
     * Processes the run control files, if any.
     */
    protected void processRunControlFiles() {
        for (String rcFileName : rcFileNames) {
            processRunControlFile(rcFileName);
        }
    }

    /**
     * Processes a run control file.
     */
    protected void processRunControlFile(String rcFileName) {
        tr.Ace.setVerbose(true);
        
        Properties props = new Properties();
        rcFileName = FileExt.resolveFileName(rcFileName);
        try {
            props.load(new FileInputStream(rcFileName));
        }
        catch (IOException ioe) {
            return;
        }

        for (Map.Entry<Object, Object> propEntry : props.entrySet()) {
            Option opt = findOptionByLongName((String)propEntry.getKey());
            if (isTrue(opt)) {
                setOption(opt, (String)propEntry.getValue());
            }
        }
    }

    protected void setOption(Option<?> opt, String value) {
        try {
            opt.setValueFromString(value);
        }
        catch (OptionException oe) {
            System.err.println("error: " + oe.getMessage());
        }
    }

    protected Option findOptionByLongName(String longName) {
        for (Option opt : options) {
            if (opt.getLongName().equals(longName)) {
                return opt;
            }
        }
        return null;
    }

    protected boolean processArgument(String arg, List<String> argList) {
        for (Option<?> opt : options) {
            try {
                if (opt.set(arg, argList)) {
                    return true;
                }
            }
            catch (OptionException oe) {
                System.err.println("error: " + oe.getMessage());
            }
        }
        return false;
    }

    /**
     * Processes the command line arguments. Returns the arguments that were not
     * consumed by option processing.
     */
    protected List<String> processCommandLine(List<String> args) {
        // copy the array, in case it's immutable (e.g. main args[])
        List<String> argList = new ArrayList<String>(args);
        while (!argList.isEmpty()) {
            String arg = argList.get(0);
            if (arg.equals("--")) {
                argList.remove(0);
                break;
            }
            if (arg.charAt(0) != '-') {
                break;
            }

            argList.remove(0);
            if (!processArgument(arg, argList)) {
                handleBadArgument(arg);
                break;
            }
        }

        return argList;
    }

    protected void handleBadArgument(String arg) {
        if (arg.equals("--help") || arg.equals("-h")) {
            showUsage();
        }
        else if (!rcFileNames.isEmpty() && arg.equals("--help-config")) {
            showConfig();
        }
        else {
            System.err.println("invalid option: " + arg + " (-h will show valid options)");
        }
    }

    protected void showUsage() {
        System.out.println("Usage: " + appName + " [options] file...");
        System.out.println(description);
        System.out.println();
        System.out.println("Options:");

        List<String> tags = new ArrayList<String>();

        for (Option opt : options) {
            StringBuffer buf = new StringBuffer("  ");

            if (opt.getShortName() == null) {
                buf.append("    ");
            }
            else {
                buf.append("-" + opt.getShortName() + ", ");
            }
                            
            buf.append("--" + opt.getLongName());
                            
            tags.add(buf.toString());
        }
                        
        int widest = -1;
        for (String tag : tags) {
            widest = Math.max(tag.length(), widest);
        }

        for (int idx = 0; idx < options.size(); ++idx) {
            Option opt = options.get(idx);
            String tag = tags.get(idx);

            System.out.print(tag);
            for (int ti = tag.length(); ti < widest + 2; ++ti) {
                System.out.print(" ");
            }

            System.out.println(opt.getDescription());
        }

        if (!rcFileNames.isEmpty()) {
            System.out.println("For an example configure file, run --help-config");
            System.out.println();
            System.out.println("Configuration File" + (rcFileNames.size() > 1 ? "s" : "") + ":");
            for (String rcFileName : rcFileNames) {
                System.out.println("    " + rcFileName);
            }
        }
    }

    protected void showConfig() {
        for (Option opt : options) {
            System.out.println("# " + opt.getDescription());
            System.out.println(opt.getLongName() + " = " + opt.toString());
            System.out.println();
        }
    }
}
