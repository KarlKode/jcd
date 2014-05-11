package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

/**
 * This command provides functionality to the VFS similar to the unix command mkdir.
 */
public class VFSmkdir extends AbstractVFSCommand
{
    public static final String COMMAND = "mkdir";

    /**
     * NAME
     * mkdir - make directories
     * SYNOPSIS
     * mkdir [OPTION]... DIRECTORY
     * DESCRIPTION
     * Create the DIRECTORY(ies), if they do not already exist.
     * <p>
     * -h, --help
     * prints information about usage
     *
     * @param console that executes the command
     * @param args    passed with the command
     */
    @Override
    public void execute(VFSConsole console, String[] args)
            throws CommandException
    {
        VDisk vDisk = console.getVDisk();

        switch (args.length)
        {
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

                String name = args[expr];
                VObject destination = console.getCurrent();

                if (args[expr].split(VDisk.PATH_SEPARATOR).length > 1)
                {
                    name = args[expr].substring(args[expr].lastIndexOf(VDisk.PATH_SEPARATOR) + 1);
                    destination = resolve(console, args[expr].substring(0, args[expr].lastIndexOf(VDisk.PATH_SEPARATOR) + 1));
                }

                if (destination instanceof VDirectory)
                {
                    vDisk.mkdir((VDirectory) destination, name);
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
        System.out.println("\tmkdir DIRECTORY");
    }

    /**
     * @return the command in text form
     */
    protected String command()
    {
        return COMMAND;
    }
}
