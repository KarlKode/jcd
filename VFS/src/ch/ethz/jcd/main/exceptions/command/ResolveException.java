package ch.ethz.jcd.main.exceptions.command;

/**
 * Created by leo on 10.05.14.
 */
public class ResolveException extends CommandException
{
    public ResolveException(Exception e)
    {
        this.initCause(e);
    }
}