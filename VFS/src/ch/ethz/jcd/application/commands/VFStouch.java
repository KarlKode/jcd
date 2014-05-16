package ch.ethz.jcd.application.commands;

import ch.ethz.jcd.application.AbstractVFSApplication;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

/**
 * This command provides functionality to the VFS similar to the unix command touch.
 */
public class VFStouch extends AbstractVFSCommand
{
    public static final String COMMAND = "touch";

    public VFStouch(String[] args)
    {
        super(args);
    }

    /**
     * NAME
     * touch - creates a new empty file
     * SYNOPSIS
     * touch [OPTION]... FILE
     * DESCRIPTION
     * Creates a new FILE resolving the path before the FILE name.
     * <p>
     * -h, --help
     * prints information about usage
     * @param application to execute on
     */
    @Override
    public void execute(AbstractVFSApplication application)
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
                    destination = resolve(application, args[expr].substring(0, args[expr].lastIndexOf(VDisk.PATH_SEPARATOR)));
                }

                if (destination instanceof VDirectory)
                {
                    vDisk.touch((VDirectory) destination, name);
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
        System.out.println("\ttouch FILE");
    }

    /**
     * @return the command in text form
     */
    protected String command()
    {
        return COMMAND;
    }
}
