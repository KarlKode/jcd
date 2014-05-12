package ch.ethz.jcd.application.commands;

import ch.ethz.jcd.application.AbstractVFSApplication;
import ch.ethz.jcd.application.VFSConsole;
import ch.ethz.jcd.main.exceptions.command.CommandException;

/**
 * This command provides functionality to the VFS similar to the unix command help.
 */
public class VFShelp extends AbstractVFSCommand
{
    public static final String COMMAND = "help";

    public VFShelp(AbstractVFSApplication application)
    {
        super(application);
    }

    /**
     * NAME
     * help - print usage
     * SYNOPSIS
     * help
     * DESCRIPTION
     * print how to use VFS and what commands are provided
     * @param args    passed with the command
     */
    @Override
    public void execute(String[] args)
            throws CommandException
    {
        application.println("Commands:");
        for (AbstractVFSCommand cmd : application.commands.values())
        {
            cmd.help();
        }
    }

    /**
     * Prints the help of the concrete command.
     */
    @Override
    public void help()
    {
        application.println("\thelp");
    }

    /**
     * @return the command in text form
     */
    protected String command()
    {
        return COMMAND;
    }
}
