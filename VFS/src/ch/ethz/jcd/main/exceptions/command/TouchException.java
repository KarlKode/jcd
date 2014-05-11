package ch.ethz.jcd.main.exceptions.command;

/**
 * Created by leo on 10.05.14.
 */
public class TouchException extends CommandException {
    public TouchException(Exception ex) {
        this.initCause(ex);
    }
}
