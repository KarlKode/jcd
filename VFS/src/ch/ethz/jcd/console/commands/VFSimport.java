package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

import java.io.File;

/**
 * This command provides functionality to the VFS to import from the host file
 * system to the VFS.
 */
public class VFSimport extends AbstractVFSCommand
{
    public static final String COMMAND = "import";

    /**
     * NAME
     * import - imports a file into VFS
     * SYNOPSIS
     * import [OPTION]... PATH_TO_HOST_FILE... [DEST]
     * DESCRIPTION
     * Imports the given file form the host file system into the given
     * location at the virtual file system.
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
                if (args[1].equals(AbstractVFSCommand.OPTION_H) || args[1].equals(AbstractVFSCommand.OPTION_HELP))
                {
                    help();
                    break;
                }
                File file = new File(args[1]);
                vDisk.importFromHost(file, console.getCurrent());
                break;
            }
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
                    File file = new File(args[src]);
                    VObject destination = resolve(console, args[dest]);

                    if (destination instanceof VDirectory && file.exists())
                    {
                        vDisk.importFromHost(file, (VDirectory) destination);
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
        System.out.println("\timport PATH_TO_HOST_FILE DEST");
    }

    /**
     * @return the command in text form
     */
    protected String command()
    {
        return COMMAND;
    }
}
