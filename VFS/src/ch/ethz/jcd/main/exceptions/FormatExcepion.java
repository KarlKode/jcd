package ch.ethz.jcd.main.exceptions;

import ch.ethz.jcd.main.exceptions.command.CommandException;

/**
 * Created by leo on 13.05.14.
 */
public class FormatExcepion extends CommandException
{
    public FormatExcepion(Exception e)
    {
        this.initCause(e);
    }
}
