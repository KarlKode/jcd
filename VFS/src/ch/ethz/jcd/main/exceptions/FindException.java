package ch.ethz.jcd.main.exceptions;

/**
 * Created by leo on 10.05.14.
 */
public class FindException extends Exception {
    public FindException(Exception e) {
        this.initCause(e);
    }
}
