package ch.ethz.jcd.application.commands;

import ch.ethz.jcd.application.AbstractVFSApplication;
import ch.ethz.jcd.main.exceptions.command.CommandException;

public class VFSNull extends AbstractVFSCommand
{
    public final String COMMAND = "null";

    public VFSNull(String[] args)
    {
        super(args);
    }

    @Override
    public void execute(AbstractVFSApplication application)
            throws CommandException
    {
        this.error(args[0] + ": command not found");
    }

    /**
     * Prints the help of the concrete command.
     */
    @Override
    public void help()
    {
        // TODO
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
