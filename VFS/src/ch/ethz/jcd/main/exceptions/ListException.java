package ch.ethz.jcd.main.exceptions;

import java.io.IOException;

/**
 * Created by leo on 10.05.14.
 */
public class ListException extends Exception {
    public ListException(Exception e) {
        this.initCause(e);
    }
}
