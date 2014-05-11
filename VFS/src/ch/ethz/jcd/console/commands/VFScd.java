package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

/**
 * This command provides functionality to the VFS similar to the unix command cd.
 */
public class VFScd extends AbstractVFSCommand
{
    /**
     * NAME
     *      cd - change the working directory
     * SYNOPSIS
     *      cd [OPTION]... DEST
     * DESCRIPTION
     *      change the working directory to the given DEST
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
            case 1:
            {
                console.setCurrent((VDirectory) vDisk.resolve(VDisk.PATH_SEPARATOR));
                break;
            }
            case 2:
            {
                if(args[1].equals(AbstractVFSCommand.OPTION_H) || args[1].equals(AbstractVFSCommand.OPTION_HELP))
                {
                    help();
                    break;
                }

                VObject destination = resolve(console, args[1]);
                if (destination != null && destination instanceof VDirectory)
                {
                    console.setCurrent((VDirectory) destination);
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
        System.out.println("\tcd [DEST]");
    }
}
