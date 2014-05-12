package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.AbstractVFSApplication;
import ch.ethz.jcd.console.VFSConsole;
import ch.ethz.jcd.main.exceptions.command.CommandException;

/**
 * This command provides functionality to the VFS similar to the unix command help.
 */
public class VFShelp extends AbstractVFSCommand
{
    public static final String COMMAND = "help";

    /**
     * NAME
     * help - print usage
     * SYNOPSIS
     * help
     * DESCRIPTION
     * print how to use VFS and what commands are provided
     *  @param console that executes the command
     * @param args    passed with the command
     */
    @Override
    public void execute(AbstractVFSApplication console, String[] args)
            throws CommandException
    {
        System.out.println("Commands:");
        for (AbstractVFSCommand cmd : VFSConsole.VFS_COMMANDS.values())
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
        System.out.println("\thelp");
    }

    /**
     * @return the command in text form
     */
    protected String command()
    {
        return COMMAND;
    }
}
