package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

/**
 * This command provides functionality to the VFS similar to the unix command rm.
 */
public class VFSrm extends AbstractVFSCommand
{
    protected static final String OPTION_R = "-r";
    protected static final String OPTION_RECURSIVE = "--recursive";

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
     *
     * @param console that executes the command
     * @param args    passed with the command
     */
    @Override
    public void execute(VFSConsole console, String[] args)
    {
        VDisk vDisk = console.getVDisk();

        switch (args.length)
        {
            case 2:
            case 3:
            {
                boolean recursive = false;
                int expr = 3;

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

                String path = args[expr].startsWith(VDisk.PATH_SEPARATOR) ? args[expr] : console.getCurrent() + args[expr];
                VObject destination = resolve(console, path);

                if(destination instanceof VDirectory && !recursive)
                {
                    System.out.println("rm: cannot remove '"+ path +"': Is a directory");
                    break;
                }

                if (destination != null)
                {
                    vDisk.delete(destination);
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
        System.out.println("\trm FILE");
    }
}
