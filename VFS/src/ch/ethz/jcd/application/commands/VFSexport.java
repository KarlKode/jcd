package ch.ethz.jcd.application.commands;

import ch.ethz.jcd.application.AbstractVFSApplication;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

import java.io.File;

/**
 * This command provides functionality to the VFS to export from the VFS to the
 * host file system.
 */
public class VFSexport extends AbstractVFSCommand
{
    public static final String COMMAND = "export";

    public VFSexport(String[] args)
    {
        super(args);
    }

    /**
     * NAME
     * export - exports a file on VFS to the host file system
     * SYNOPSIS
     * export [OPTION]... PATH_TO_HOST_FILE... DEST
     * DESCRIPTION
     * Exports the given file form the virtual file system into the given
     * location at the host file system.
     * <p>
     * -h, --help
     * prints information about usage
     * @param application
     */
    @Override
    public void execute(AbstractVFSApplication application)
            throws CommandException
    {
        VDisk vDisk = application.getVDisk();

        switch (args.length)
        {
            case 3:
            {
                int host = args.length - 1;
                int dest = 0;

                for (int i = 1; i < args.length; i++)
                {
                    if (args[i].equals(AbstractVFSCommand.OPTION_H) || args[i].equals(AbstractVFSCommand.OPTION_HELP))
                    {
                        help();
                        break;
                    }
                    else
                    {
                        host = Math.min(i, host);
                        dest = Math.max(i, dest);
                    }
                }

                if (!(host == dest))
                {
                    File file = new File(args[host]);
                    VObject source = resolve(application, args[dest]);

                    if (source instanceof VFile)
                    {
                        vDisk.exportToHost((VFile) source, file);
                        break;
                    }
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
        System.out.println("\texport PATH_TO_HOST_FILE SOURCE");
    }

    /**
     * @return the command in text form
     */
    protected String command()
    {
        return COMMAND;
    }
}
