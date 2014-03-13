package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.exceptions.ToDoException;

public class DirectoryBlock extends InodeBlock
{
    public DirectoryBlock(Block block, String name) throws InvalidNameException
    {
        super(block, name);
    }

    @Override
    public int size()
    {
        throw new ToDoException( );
    }
}
