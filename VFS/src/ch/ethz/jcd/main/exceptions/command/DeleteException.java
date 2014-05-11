package ch.ethz.jcd.main.exceptions.command;

/**
 * Created by leo on 10.05.14.
 */
public class DeleteException extends CommandException {
    public DeleteException(Exception e) {
        this.initCause(e);
    }
}
