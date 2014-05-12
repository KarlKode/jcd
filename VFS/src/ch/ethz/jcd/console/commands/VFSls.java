package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.AbstractVFSApplication;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

import java.util.HashMap;

/**
 * This command provides functionality to the VFS similar to the unix command ls.
 */
public class VFSls extends AbstractVFSCommand
{
    public static final String COMMAND = "ls";

    /**
     * NAME
     * ls - list directory contents
     * SYNOPSIS
     * ls [OPTION]... [FILE]
     * DESCRIPTION
     * Outputs the objects that the directory at given path contains. If no path
     * is given the current directory is used as destination.
     * <p>
     * -h, --help
     * prints information about usage
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
            case 1:
            {
                out(vDisk.list(console.getCurrent()));
                break;
            }
            case 2:
            {
                int expr = args.length - 1;

                for (int i = 1; i < args.length; i++)
                {
                    if (args[i].equals(AbstractVFSCommand.OPTION_H) || args[i].equals(AbstractVFSCommand.OPTION_HELP))
                    {
                        help();
                        break;
                    }
                    else
                    {
                        expr = Math.min(i, expr);
                    }
                }

                VObject destination = resolve(console, args[expr]);

                if (destination instanceof VDirectory)
                {
                    out(vDisk.list((VDirectory) destination));
                    break;
                }
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
        System.out.println("\tls [DEST]");
    }

    /**
     * Prints a list of the given objects on the console.
     *
     * @param list to print
     */
    private void out(HashMap<String, VObject> list)
    {
        for (String key : list.keySet())
        {
            System.out.println(key);
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
