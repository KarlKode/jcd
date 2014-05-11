package ch.ethz.jcd.main.exceptions;

public class BlockFullException extends Exception
{
    public BlockFullException()
    {
        // TODO put in some fancy message
        super("Block is Full");
    }
}
