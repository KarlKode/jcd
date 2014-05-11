package ch.ethz.jcd.main.exceptions;

public class NoSuchFileOrDirectoryException extends Exception
{
    public NoSuchFileOrDirectoryException()
    {
        super("No such file or directory");
    }
}
