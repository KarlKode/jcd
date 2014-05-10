package ch.ethz.jcd.main.exceptions;

/**
 * Created by leo on 10.05.14.
 */
public class MoveException extends Exception {
    public MoveException(Exception ex) {
        this.initCause(ex);
    }
}
