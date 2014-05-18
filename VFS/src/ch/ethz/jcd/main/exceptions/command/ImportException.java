package ch.ethz.jcd.main.exceptions.command;

/**
 * Created by leo on 10.05.14.
 */
public class ImportException extends CommandException
{
    public ImportException(Exception e)
    {
        this.initCause(e);
    }
}
