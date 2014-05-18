package ch.ethz.jcd.main.exceptions.command;

/**
 * Created by leo on 10.05.14.
 */
public class ListException extends CommandException
{
    public ListException(Exception e)
    {
        this.initCause(e);
    }
}
