package ch.ethz.jcd.application.commands;

import ch.ethz.jcd.application.AbstractVFSApplication;
import ch.ethz.jcd.main.exceptions.command.CommandException;

import java.io.IOException;

/**
 * This command provides functionality to the VFS similar to the unix command pwd.
 */
public class VFSpwd extends AbstractVFSCommand
{
    public static final String COMMAND = "pwd";

    public VFSpwd(String[] args)
    {
        super(args);
    }

    /**
     * NAME
     * pwd - print the name of current/working directory
     * SYNOPSIS
     * pwd [OPTION]...
     * DESCRIPTION
     * Print the full filename of the current working directory.
     * <p>
     * -h, --help
     * prints information about usage
     *
     * @param application
     */
    @Override
    public void execute(AbstractVFSApplication application)
            throws CommandException
    {
        switch (args.length)
        {
            case 1:
            case 2:
            {
                int expr = args.length - 1;

                for (int i = 1; i < args.length; i++)
                {
                    if (args[i].equals(AbstractVFSCommand.OPTION_H) || args[i].equals(AbstractVFSCommand.OPTION_HELP))
                    {
                        help();
                        break;
                    } else
                    {
                        expr = Math.min(i, expr);
                    }
                }
                try
                {
                    System.out.println(application.getCurrent().getPath());
                } catch (IOException ignored)
                {
                }
                break;
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
        System.out.println("\tpwd");
    }

    /**
     * @return the command in text form
     */
    protected String command()
    {
        return COMMAND;
    }
}
