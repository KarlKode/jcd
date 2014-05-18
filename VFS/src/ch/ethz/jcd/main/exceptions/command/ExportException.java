package ch.ethz.jcd.main.exceptions.command;

/**
 * Created by leo on 10.05.14.
 */
public class ExportException extends CommandException
{
    public ExportException(Exception e)
    {
        this.initCause(e);
    }
}
