package ch.ethz.jcd.application.commands;

import ch.ethz.jcd.application.AbstractVFSApplication;
import ch.ethz.jcd.main.exceptions.command.CommandException;

public class VFSquit extends AbstractVFSCommand
{
    public final String COMMAND = "quit";

    public VFSquit(String[] args)
    {
        super(args);
    }

    @Override
    public void execute(AbstractVFSApplication application)
            throws CommandException
    {
        application.quit();
    }

    /**
     * Prints the help of the concrete command.
     */
    @Override
    public void help()
    {
        System.out.println("\tquit");
    }

    /**
     * @return the command in text form
     */
    @Override
    protected String command()
    {
        return COMMAND;
    }
}
