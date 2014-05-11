package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

/**
 * This command provides functionality to the VFS similar to the unix command cp.
 */
public class VFScp extends AbstractVFSCommand
{
    /**
     * NAME
     *      cp - copy files and directories
     * SYNOPSIS
     *      cp [OPTION]... SOURCE... DEST
     * DESCRIPTION
     *      Copy SOURCE to DEST.
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
            }
            case 3:
            {
                String name = args[1];
                VFile file = (VFile) vDisk.resolve(args[1]);
                VObject destination = console.getCurrent();
                if(args[1].split(VDisk.PATH_SEPARATOR).length > 1)
                {
                    name = args[1].substring(args[1].lastIndexOf(VDisk.PATH_SEPARATOR) + 1);
                    destination = resolve(console, args[1].substring(0, args[1].lastIndexOf(VDisk.PATH_SEPARATOR)));
                }
                if(destination != null && destination instanceof VDirectory && file != null)
                {
                    vDisk.copy(file, (VDirectory) destination, name);
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
        System.out.println("\tcp SOURCE DEST");
    }
}
