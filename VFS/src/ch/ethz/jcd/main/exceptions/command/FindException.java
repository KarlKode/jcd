package ch.ethz.jcd.main.exceptions.command;

/**
 * Created by leo on 10.05.14.
 */
public class FindException extends CommandException
{
    public FindException(Exception e)
    {
        this.initCause(e);
    }
}
