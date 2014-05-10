package ch.ethz.jcd.main.exceptions;

/**
 * Created by leo on 10.05.14.
 */
public class ResolveException extends Exception {
    public ResolveException(Exception e) {
        this.initCause(e);
    }
}
