package ch.ethz.jcd.main.exceptions.command;

/**
 * Created by leo on 10.05.14.
 */
public class RenameException extends CommandException {
    public RenameException(Exception ex) {
        this.initCause(ex);
    }
}
