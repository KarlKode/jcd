package ch.ethz.jcd.main.exceptions;

/**
 * Created by leo on 10.05.14.
 */
public class TouchException extends Exception {
    public TouchException(Exception ex) {
        this.initCause(ex);
    }
}
