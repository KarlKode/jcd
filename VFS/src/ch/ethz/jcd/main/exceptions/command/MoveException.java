package ch.ethz.jcd.main.exceptions.command;

/**
 * Created by leo on 10.05.14.
 */
public class MoveException extends CommandException {
    public MoveException(Exception ex) {
        this.initCause(ex);
    }
}
