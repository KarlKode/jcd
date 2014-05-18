package ch.ethz.jcd.application.commands;

import ch.ethz.jcd.application.AbstractVFSApplication;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.exceptions.command.ResolveException;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

import java.io.IOException;
import java.io.Serializable;

/**
 * The AbstractVFSCommand is the abstract super class of the strategy pattern.
 * It describes what functionality a command has to provide to be used by the
 * console application
 */
public abstract class AbstractVFSCommand implements Serializable
{
    protected static final String COMMAND = "cmd";
    protected static final String OPTION_H = "-h";
    protected static final String OPTION_HELP = "--help";

    protected String[] args = null;

    public AbstractVFSCommand(String[] args)
    {
        this.setArgs(args);
    }

    public void setArgs(String[] args)
    {
        this.args = args;
    }

    /**
     * Executes the concrete command according to the passed arguments.
     *
     * @param application to execute on
     */
    public abstract void execute(AbstractVFSApplication application)
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
     * @param path        to resolve
     * @param application to execute on
     * @return the object if found, otherwise null
     */
    protected VObject resolve(AbstractVFSApplication application, String path)
            throws ResolveException
    {
        try
        {
            if (!path.startsWith(VDisk.PATH_SEPARATOR))
            {
                String pwd = application.getCurrent().getPath();
                path = pwd.endsWith(VDisk.PATH_SEPARATOR) ? pwd + path : pwd + VDisk.PATH_SEPARATOR + path;
            }
        } catch (IOException ignored)
        {

        }
        return application.getVDisk().resolve(path);
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

    public String toString()
    {
        String s = command();
        for (int i = 1; i < args.length; i++)
        {
            s += " " + args[i];
        }
        return s;
    }
}
