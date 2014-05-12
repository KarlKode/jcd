package ch.ethz.jcd.application.commands;

import ch.ethz.jcd.application.AbstractVFSApplication;
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

    public VFSmkdir(AbstractVFSApplication application)
    {
        super(application);
    }

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
     * @param args    passed with the command
     */
    @Override
    public void execute(String[] args)
            throws CommandException
    {
        VDisk vDisk = application.getVDisk();

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
                VObject destination = application.getCurrent();

                if (args[expr].split(VDisk.PATH_SEPARATOR).length > 1)
                {
                    name = args[expr].substring(args[expr].lastIndexOf(VDisk.PATH_SEPARATOR) + 1);
                    destination = resolve(args[expr].substring(0, args[expr].lastIndexOf(VDisk.PATH_SEPARATOR) + 1));
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
        application.println("\tmkdir DIRECTORY");
    }

    /**
     * @return the command in text form
     */
    protected String command()
    {
        return COMMAND;
    }
}
