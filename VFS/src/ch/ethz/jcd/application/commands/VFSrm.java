package ch.ethz.jcd.application.commands;

import ch.ethz.jcd.application.AbstractVFSApplication;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

/**
 * This command provides functionality to the VFS similar to the unix command rm.
 */
public class VFSrm extends AbstractVFSCommand
{
    protected static final String COMMAND = "rm";
    protected static final String OPTION_R = "-r";
    protected static final String OPTION_RECURSIVE = "--recursive";

    public VFSrm(AbstractVFSApplication application)
    {
        super(application);
    }

    /**
     * NAME
     * rm - remove files or directories
     * SYNOPSIS
     * rm [OPTION]... FILE...
     * DESCRIPTION
     * Recursively removes the given file or directory and its underlying structure
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
            case 3:
            {
                boolean recursive = false;
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
                    else
                    {
                        expr = Math.min(i, expr);
                    }
                }

                String path = args[expr].startsWith(VDisk.PATH_SEPARATOR) ? args[expr] : application.getCurrent() + args[expr];
                VObject destination = resolve(path);

                if (destination instanceof VDirectory && !recursive)
                {
                    this.error("cannot remove '" + path + "': Is a directory");
                    break;
                }

                vDisk.delete(destination);
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
        application.println("\trm FILE");
    }

    /**
     * @return the command in text form
     */
    protected String command()
    {
        return COMMAND;
    }
}
