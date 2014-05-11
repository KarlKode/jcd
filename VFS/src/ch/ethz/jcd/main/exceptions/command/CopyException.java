package ch.ethz.jcd.main.exceptions.command;

/**
 * Created by leo on 10.05.14.
 */
public class CopyException extends CommandException
{
    public CopyException(Exception e)
    {
        this.initCause(e);
    }
}
