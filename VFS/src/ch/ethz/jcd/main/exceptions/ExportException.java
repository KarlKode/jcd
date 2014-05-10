package ch.ethz.jcd.main.exceptions;

/**
 * Created by leo on 10.05.14.
 */
public class ExportException extends Exception {
    public ExportException(Exception e) {
        this.initCause(e);
    }
}
