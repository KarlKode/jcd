package ch.ethz.jcd.main.exceptions;

/**
 * Created by leo on 10.05.14.
 */
public class ImportException extends Exception {
    public ImportException(Exception e) {
        this.initCause(e);
    }
}
