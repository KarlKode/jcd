package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.exceptions.ToDoException;

public class FileBlock extends InodeBlock
{
    public FileBlock(Block b, String name) throws InvalidNameException
    {
        super(b, name);
    }

    public FileBlock(Block b)
    {
        super(b);
    }

    @Override
    public int size()
    {
        throw new ToDoException( );
    }
}

