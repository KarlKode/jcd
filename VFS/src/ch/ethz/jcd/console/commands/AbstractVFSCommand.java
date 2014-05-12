package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.AbstractVFSApplication;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.exceptions.command.ResolveException;
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
    protected static final String COMMAND = "cmd";
    protected static final String OPTION_H = "-h";
    protected static final String OPTION_HELP = "--help";

    /**
     * Executes the concrete command according to the passed arguments.
     *  @param console that executes the command
     * @param args    passed with the command
     */
    public abstract void execute(AbstractVFSApplication console, String[] args)
            throws CommandException;

    /**
     * Prints how to use the concrete command.
     */
    public void usage()
    {
        System.out.print("Usage: ");
        help();
    }

    /**
     * Prints the help of the concrete command.
     */
    public abstract void help();

    /**
     * Resolves the given path of a object.
     *
     * @param console that executes the command
     * @param path    to resolve
     *
     * @return the object if found, otherwise null
     */
    protected VObject resolve(AbstractVFSApplication console, String path)
            throws ResolveException
    {
        try
        {
            if (!path.startsWith(VDisk.PATH_SEPARATOR))
            {
                String pwd = console.getCurrent().getPath();
                path = pwd.endsWith(VDisk.PATH_SEPARATOR) ? pwd + path : pwd + VDisk.PATH_SEPARATOR + path;
            }
        }
        catch (IOException ignored)
        {

        }
        return console.getVDisk().resolve(path);
    }


    /**
     * method to out standardized error message in the console
     *
     * @param reason to print
     */
    public void error(String reason)
    {
        System.out.println(this.command() + ": " + reason);
    }

    /**
     * @return the command in text form
     */
    protected abstract String command();
}
