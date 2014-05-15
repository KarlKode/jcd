package ch.ethz.jcd.application.commands;

import ch.ethz.jcd.application.AbstractVFSApplication;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

/**
 * This command provides functionality to the VFS similar to the unix command cd.
 */
public class VFScd extends AbstractVFSCommand
{
    public final String COMMAND = "cd";

    /**
     * NAME
     * cd - change the working directory
     * SYNOPSIS
     * cd [OPTION]... DEST
     * DESCRIPTION
     * change the working directory to the given DEST
     * <p>
     * -h, --help
     * prints information about usage
     * @param application to execute on
     */
    @Override
    public void execute(AbstractVFSApplication application)
            throws CommandException
    {
        switch (args.length)
        {
            case 1:
            {
                application.setCurrent((VDirectory) resolve(application, VDisk.PATH_SEPARATOR));
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

                VObject destination = resolve(application, args[expr]);
                if (destination != null && destination instanceof VDirectory)
                {
                    application.setCurrent((VDirectory) destination);
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
        System.out.println("\tcd [DEST]");
    }

    /**
     * @return the command in text form
     */
    protected String command()
    {
        return COMMAND;
    }
}
