package ch.ethz.jcd.main.exceptions;

/**
 * Created by leo on 10.05.14.
 */
public class DeleteException extends Exception {
    public DeleteException(Exception e) {
        this.initCause(e);
    }
}
