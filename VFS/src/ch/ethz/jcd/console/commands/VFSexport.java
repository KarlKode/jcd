package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.utils.VDisk;

import java.io.File;

/**
 * This command provides functionality to the VFS to export from the VFS to the
 * host file system.
 */
public class VFSexport extends AbstractVFSCommand
{
    /**
     * NAME
     *      export - exports a file on VFS to the host file system
     * SYNOPSIS
     *      export [OPTION]... PATH_TO_HOST_FILE... DEST
     * DESCRIPTION
     *      Exports the given file form the virtual file system into the given
     *      location at the host file system.
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
                File file = new File(args[1]);
                VFile source = resolveFile(console, args[2]);

                if(source != null)
                {
                    vDisk.exportToHost(source, file);
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
        System.out.println("\texport PATH_TO_HOST_FILE SOURCE");
    }
}
