package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.AbstractVFSApplication;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.utils.VDisk;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * This command provides functionality to the VFS similar to the unix command find.
 */
public class VFSfind extends AbstractVFSCommand
{
    public static final String COMMAND = "find";

    protected static final String OPTION_R = "-r";
    protected static final String OPTION_RECURSIVE = "--recursive";

    protected static final String OPTION_I = "-i";
    protected static final String OPTION_CASE_INSENSITIVE = "--case-insensitive";

    /**
     * NAME
     * find - search for files in a directory hierarchy
     * SYNOPSIS
     * find [-r] [-i] [expression]
     * DESCRIPTION
     * find searches the directory tree rooted at each given starting-point by
     * evaluating the given expression from left to right.
     * <p>
     * -h, --help
     * prints information about usage
     * -i, --case-insensitive
     * enables case insensitive search
     * -r, --recursive
     * enables recursive search
     *  @param console that executes the command
     * @param args    passed with the command
     */
    @Override
    public void execute(AbstractVFSApplication console, String[] args)
            throws CommandException
    {
        VDisk vDisk = console.getVDisk();

        switch (args.length)
        {
            case 2:
            case 3:
            case 4:
            {
                boolean recursive = false;
                boolean insensitive = false;
                int expr = args.length - 1;

                for (int i = 1; i < args.length; i++)
                {
                    if (args[i].equals(AbstractVFSCommand.OPTION_H) || args[i].equals(AbstractVFSCommand.OPTION_HELP))
                    {
                        help();
                        break;
                    }
                    else if (args[i].equals(OPTION_R) || args[i].equals(OPTION_RECURSIVE))
                    {
                        recursive = true;
                    }
                    else if (args[i].equals(OPTION_I) || args[i].equals(OPTION_CASE_INSENSITIVE))
                    {
                        insensitive = true;
                    }
                    else
                    {
                        expr = Math.min(i, expr);
                    }
                }
                Pattern pattern = insensitive ? Pattern.compile("\\w*" + args[expr], Pattern.CASE_INSENSITIVE) : Pattern.compile("\\w*" + args[expr] + "\\w*");
                out(vDisk.find(pattern, console.getCurrent(), recursive));
                break;
            }
            default:
            {
                usage();
                break;
            }
        }
    }

    /**
     * Prints the help of the concrete command.
     */
    @Override
    public void help()
    {
        System.out.println("\tfind OPTIONS EXPRESSION");
    }

    /**
     * Prints a list of the given objects on the console.
     *
     * @param list to print
     */
    private void out(HashMap<VFile, String> list)
    {
        for (String value : list.values())
        {
            System.out.println(value);
        }
    }

    /**
     * @return the command in text form
     */
    protected String command()
    {
        return COMMAND;
    }
}
