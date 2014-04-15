package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;

/**
 * This command provides functionality to the VFS similar to the unix command touch.
 */
public class VFStouch extends AbstractVFSCommand
{
    /**
     * NAME
     *      touch - creates a new empty file
     * SYNOPSIS
     *      touch [OPTION]... FILE
     * DESCRIPTION
     *      Creates a new FILE resolving the path before the FILE name.
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
                String name = args[1];
                VDirectory destination = console.getCurrent();

                if(args[1].split(VDisk.PATH_SEPARATOR).length > 1)
                {
                    name = args[1].substring(args[1].lastIndexOf(VDisk.PATH_SEPARATOR) + 1);
                    destination = resolveDirectory(console, args[1].substring(0, args[1].lastIndexOf(VDisk.PATH_SEPARATOR)));
                }
                if(destination != null)
                {
                    vDisk.touch(destination, name);
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
}
