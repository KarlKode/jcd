package ch.ethz.jcd.main.exceptions;

/**
 * Created by leo on 10.05.14.
 */
public class MkDirException extends Exception {
    public MkDirException(Exception ex) {
        this.initCause(ex);
    }
}
