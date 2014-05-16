package ch.ethz.jcd.application.commands;

import ch.ethz.jcd.application.AbstractVFSApplication;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.exceptions.command.ResolveException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

/**
 * This command provides functionality to the VFS similar to the unix command cp.
 */
public class VFScp extends AbstractVFSCommand
{
    public static final String COMMAND = "cp";

    public VFScp(String[] args)
    {
        super(args);
    }

    /**
     * NAME
     * cp - copy files and directories
     * SYNOPSIS
     * cp [OPTION]... SOURCE... DEST
     * DESCRIPTION
     * Copy SOURCE to DEST.
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
            case 2:
            case 3:
            {
                int src = args.length - 1;
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
                        src = Math.min(i, src);
                        dest = Math.max(i, dest);
                    }
                }

                if (!(src == dest))
                {
                    String name = args[dest];
                    VObject source = vDisk.resolve(args[src]);
                    VObject destination;

                    try
                    {
                        /**
                         * copying into directory w/o renaming the object
                         *
                         * eg. cp tmp/bla.txt /usr/local/
                         */
                        destination = resolve(application, args[dest]);

                        if (destination instanceof VDirectory)
                        {
                            name = args[src];

                            if (args[src].split(VDisk.PATH_SEPARATOR).length > 1)
                            {
                                name = args[src].substring(args[dest].lastIndexOf(VDisk.PATH_SEPARATOR) + 1);
                            }
                        }
                    }
                    catch (ResolveException ignored)
                    {
                        /**
                         * copying into directory doing the renaming of the object
                         *
                         * eg. cp tmp/bla.txt /usr/local/foo.txt
                         */
                        destination = application.getCurrent();

                        if (args[dest].split(VDisk.PATH_SEPARATOR).length > 1)
                        {
                            name = args[dest].substring(args[dest].lastIndexOf(VDisk.PATH_SEPARATOR) + 1);
                            destination = resolve(application, args[dest].substring(0, args[dest].lastIndexOf(VDisk.PATH_SEPARATOR)));
                        }
                    }

                    if (destination instanceof VDirectory)
                    {
                        vDisk.copy(source, (VDirectory) destination, name);
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
        System.out.println("\tcp SOURCE DEST");
    }

    /**
     * @return the command in text form
     */
    protected String command()
    {
        return COMMAND;
    }
}
