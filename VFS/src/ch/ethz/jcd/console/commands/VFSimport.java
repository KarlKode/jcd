package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;
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
    /**
     * NAME
     *      import - imports a file into VFS
     * SYNOPSIS
     *      import [OPTION]... PATH_TO_HOST_FILE... [DEST]
     * DESCRIPTION
     *      Imports the given file form the host file system into the given
     *      location at the virtual file system.
     *
     *      -h, --help
     *          prints information about usage
     *
     * @param console that executes the command
     * @param args passed with the command
     */
    @Override
    public void execute(VFSConsole console, String[] args)
    {
        VDisk vDisk = console.getVDisk();

        switch (args.length)
        {
            case 2:
            {
                if(args[1].equals(AbstractVFSCommand.OPTION_H) || args[1].equals(AbstractVFSCommand.OPTION_HELP))
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
                File file = new File(args[1]);
                VObject destination = resolve(console, args[2]);

                if((destination != null && destination instanceof VDirectory) || !file.exists())
                {
                    vDisk.importFromHost(file, (VDirectory) destination);
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
        System.out.println("\timport PATH_TO_HOST_FILE DEST");
    }
}
