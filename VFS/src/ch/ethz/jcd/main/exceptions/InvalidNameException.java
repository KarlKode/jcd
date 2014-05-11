package ch.ethz.jcd.main.exceptions;

public class InvalidNameException extends Exception
{
    public InvalidNameException(String reason)
    {
        super(reason);
    }
}
