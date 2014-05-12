package ch.ethz.jcd.application.commands;

import ch.ethz.jcd.application.AbstractVFSApplication;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.exceptions.command.ResolveException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

/**
 * This command provides functionality to the VFS similar to the unix command mv.
 */
public class VFSmv extends AbstractVFSCommand
{
    public static final String COMMAND = "mv";

    public VFSmv(AbstractVFSApplication application)
    {
        super(application);
    }

    /**
     * NAME
     * mv - move (rename) files or directory
     * SYNOPSIS
     * ls [OPTION]... SOURCE... DEST
     * DESCRIPTION
     * Rename SOURCE to DEST, or move SOURCE(s) to DIRECTORY.
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
                    VObject source = resolve(args[src]);
                    VObject destination;

                    try
                    {
                        /**
                         * moving into directory w/o renaming the object
                         *
                         * eg. mv tmp/bla.txt /usr/local/
                         */
                        destination = resolve(args[dest]);

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
                         * moving into directory doing the renaming of the object
                         *
                         * eg. mv tmp/bla.txt /usr/local/foo.txt
                         */
                        destination = application.getCurrent();

                        if (args[dest].split(VDisk.PATH_SEPARATOR).length > 1)
                        {
                            name = args[dest].substring(args[dest].lastIndexOf(VDisk.PATH_SEPARATOR) + 1);
                            destination = resolve(args[dest].substring(0, args[dest].lastIndexOf(VDisk.PATH_SEPARATOR)));
                        }
                    }

                    if (destination instanceof VDirectory && source != null)
                    {
                        vDisk.move(source, (VDirectory) destination, name);
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
        System.out.println("\tmv SOURCE DEST");
    }

    /**
     * @return the command in text form
     */
    protected String command()
    {
        return COMMAND;
    }
}