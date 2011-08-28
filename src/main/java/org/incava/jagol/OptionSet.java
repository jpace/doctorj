package org.incava.jagol;

import java.io.*;
import java.util.*;


/**
 * A group of options.
 */
public class OptionSet
{
    private List options = new ArrayList();

    private List rcFiles = new ArrayList();

    private String appName;
    
    private String description;
    
    public OptionSet(String appName, String description)
    {
        this.appName = appName;
        this.description = description;
    }
    
    /**
     * Returns the application name.
     */
    public String getAppName()
    {
        return appName;
    }

    /**
     * Returns the description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Adds an options to this set.
     */
    public void add(Option opt)
    {
        options.add(opt);
    }

    /**
     * Adds a run control file to be processed.
     */
    public void addRunControlFile(String name)
    {
        tr.Ace.log("adding rc file: " + name);
        rcFiles.add(name);
    }

    /**
     * Processes the run control files and command line arguments. Returns the
     * arguments that were not consumed by option processing.
     */
    public String[] process(String[] args)
    {
        tr.Ace.log("args: " + args);

        processRunControlFiles();

        return processCommandLine(args);
    }

    /**
     * Processes the run control files, if any.
     */
    protected void processRunControlFiles()
    {
        tr.Ace.log("");
        Iterator it = rcFiles.iterator();
        while (it.hasNext()) {
            String rcFileName = (String)it.next();
            tr.Ace.log("processing: " + rcFileName);
            try {
                Properties props = new Properties();

                int tildePos = rcFileName.indexOf('~');
                if (tildePos != -1) {
                    rcFileName = rcFileName.substring(0, tildePos) + System.getProperty("user.home") + rcFileName.substring(tildePos + 1);
                }

                props.load(new FileInputStream(rcFileName));

                tr.Ace.log("properties: " + props);
                Iterator pit = props.keySet().iterator();
                while (pit.hasNext()) {
                    String key   = (String)pit.next();
                    String value = (String)props.get(key);
                    tr.Ace.log(key + " => " + value);
                    Iterator oit = options.iterator();
                    boolean processed = false;
                    while (!processed && oit.hasNext()) {
                        Option opt = (Option)oit.next();
                        tr.Ace.log("option: " + opt.getLongName());
                        if (opt.getLongName().equals(key)) {
                            tr.Ace.log("option matches: " + opt);
                            processed = true;
                            try {
                                opt.setValue(value);
                            }
                            catch (OptionException oe) {
                                tr.Ace.log("option exception: " + oe);
                                System.err.println("error: " + oe.getMessage());
                            }
                        }
                    }
                }
            }
            catch (IOException ioe) {
                tr.Ace.log("exception: " + ioe);
                // ioe.printStackTrace();
            }
        }
    }

    /**
     * Processes the command line arguments. Returns the arguments that were not
     * consumed by option processing.
     */
    protected String[] processCommandLine(String[] args)
    {
        tr.Ace.log("args: " + args);
        
        List argList = new ArrayList();
        for (int i = 0; i < args.length; ++i) {
            argList.add(args[i]);
        }

        tr.Ace.log("arg list: " + argList);

        while (argList.size() > 0) {
            String arg = (String)argList.get(0);
            
            tr.Ace.log("arg: " + arg);
            
            if (arg.equals("--")) {
                argList.remove(0);
                break;
            }
            else if (arg.charAt(0) == '-') {
                tr.Ace.log("got leading dash");
                argList.remove(0);
                
                Iterator oit = options.iterator();
                boolean processed = false;
                while (!processed && oit.hasNext()) {
                    Option opt = (Option)oit.next();
                    tr.Ace.log("option: " + opt);
                    try {
                        processed = opt.set(arg, argList);
                        tr.Ace.log("processed: " + processed);
                    }
                    catch (OptionException oe) {
                        tr.Ace.log("option exception: " + oe);
                        System.err.println("error: " + oe.getMessage());
                    }
                }

                if (!processed) {
                    tr.Ace.log("argument not processed: '" + arg + "'");
                    if (arg.equals("--help") || arg.equals("-h")) {
                        showUsage();
                    }
                    else if (rcFiles.size() > 0 && arg.equals("--help-config")) {
                        showConfig();
                    }
                    else {
                        System.err.println("invalid option: " + arg + " (-h will show valid options)");
                    }
                    
                    break;
                }
            }
            else {
                break;
            }
        }

        String[] unprocessed = (String[])argList.toArray(new String[0]);
        
        tr.Ace.log("args", args);
        tr.Ace.log("unprocessed", unprocessed);

        return unprocessed;
    }

    protected void showUsage()
    {
        tr.Ace.log("generating help");

        System.out.println("Usage: " + appName + " [options] file...");
        System.out.println(description);
        System.out.println();
        System.out.println("Options:");

        tr.Ace.log("options: " + options);

        List tags = new ArrayList();

        Iterator it = options.iterator();
        while (it.hasNext()) {
            Option opt = (Option)it.next();
            tr.Ace.log("opt: " + opt);
            StringBuffer buf = new StringBuffer("  ");

            if (opt.getShortName() == 0) {
                buf.append("    ");
            }
            else {
                buf.append("-" + opt.getShortName() + ", ");
            }
                            
            buf.append("--" + opt.getLongName());
                            
            tags.add(buf.toString());
        }
                        
        int widest = -1;
        Iterator tit = tags.iterator();
        while (tit.hasNext()) {
            String tag = (String)tit.next();
            widest = Math.max(tag.length(), widest);
        }

        it = options.iterator();
        tit = tags.iterator();
        while (it.hasNext()) {
            Option opt = (Option)it.next();
            String tag = (String)tit.next();
            tr.Ace.log("opt: " + opt);

            System.out.print(tag);
            for (int i = tag.length(); i < widest + 2; ++i) {
                System.out.print(" ");
            }

            System.out.println(opt.getDescription());
        }

        if (rcFiles.size() > 0) {
            System.out.println("For an example configure file, run --help-config");
            System.out.println();
            System.out.println("Configuration File" + (rcFiles.size() > 1 ? "s" : "") + ":");
            Iterator rit = rcFiles.iterator();
            while (rit.hasNext()) {
                String rcFileName = (String)rit.next();
                System.out.println("    " + rcFileName);
            }
        }
    }

    protected void showConfig()
    {
        tr.Ace.log("generating config");

        Iterator it = options.iterator();
        while (it.hasNext()) {
            Option opt = (Option)it.next();
            System.out.println("# " + opt.getDescription());
            System.out.println(opt.getLongName() + " = " + opt.toString());
            System.out.println();
        }
    }
}
