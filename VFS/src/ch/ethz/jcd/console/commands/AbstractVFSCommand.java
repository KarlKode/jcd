package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

import java.io.IOException;

/**
 * The AbstractVFSCommand is the abstract super class of the strategy pattern.
 * It describes what functionality a command has to provide to be used by the
 * console application
 */
public abstract class AbstractVFSCommand
{
    protected static final String OPTION_H = "-h";
    protected static final String OPTION_HELP = "--help";

    /**
     * Executes the concrete command according to the passed arguments.
     *
     * @param console that executes the command
     * @param args passed with the command
     */
    public abstract void execute(VFSConsole console, String[] args);

    /**
     * Prints how to use the concrete command.
     */
    public void usage( )
    {
        System.out.print("Usage: ");
        help();
    }

    /**
     * Prints the help of the concrete command.
     */
    public abstract void help( );

    /**
     * Resolves the given path of a directory.
     *
     * @param console that executes the command
     * @param path to resolve
     * @return the directory if found, otherwise null
     */
    protected VDirectory resolveDirectory(VFSConsole console, String path)
    {
        if(!path.startsWith(VDisk.PATH_SEPARATOR))
        {
            try
            {
                String pwd = console.getCurrent().getPath();
                path = pwd.endsWith(VDisk.PATH_SEPARATOR) ?  pwd + path : pwd + VDisk.PATH_SEPARATOR + path;
            }
            catch (IOException e)
            {
                return null;
            }
        }

        VObject destination = console.getVDisk().resolve(path);

        return (destination instanceof VDirectory) ? (VDirectory) destination : null;
    }

    /**
     * Resolves the given path of a file.
     *
     * @param console that executes the command
     * @param path to resolve
     * @return the file if found, otherwise null
     */
    protected VFile resolveFile(VFSConsole console, String path)
    {
        if(!path.startsWith(VDisk.PATH_SEPARATOR))
        {
            try
            {
                String pwd = console.getCurrent().getPath();
                path = pwd.endsWith(VDisk.PATH_SEPARATOR) ?  pwd + path : pwd + VDisk.PATH_SEPARATOR + path;
            }
            catch (IOException e)
            {
                return null;
            }
        }

        VObject destination = console.getVDisk().resolve(path);

        return (destination instanceof VFile) ? (VFile) destination : null;
    }
}
