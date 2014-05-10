package ch.ethz.jcd.main.exceptions;

/**
 * Created by leo on 10.05.14.
 */
public class RenameException extends Exception {
    public RenameException(Exception ex) {
        this.initCause(ex);
    }
}
