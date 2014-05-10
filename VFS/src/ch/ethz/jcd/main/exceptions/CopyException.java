package ch.ethz.jcd.main.exceptions;

/**
 * Created by leo on 10.05.14.
 */
public class CopyException extends Exception {
    public CopyException(Exception e) {
        this.initCause(e);
    }
}
