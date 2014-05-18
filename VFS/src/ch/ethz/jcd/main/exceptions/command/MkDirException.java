package ch.ethz.jcd.main.exceptions.command;

/**
 * Created by leo on 10.05.14.
 */
public class MkDirException extends CommandException
{
    public MkDirException(Exception ex)
    {
        this.initCause(ex);
    }
}
