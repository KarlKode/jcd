package ch.ethz.jcd.main.exceptions;

import ch.ethz.jcd.main.exceptions.command.CommandException;

/**
 * Created by leo on 18.05.14.
 */
public class FilenameTooLong extends CommandException {
    private String message;

    public FilenameTooLong(String s) {
        message = s;
    }
}
