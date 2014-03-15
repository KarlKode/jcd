package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.exceptions.ToDoException;

public class DirectoryBlock extends InodeBlock
{
    public static final String ROOT_DIRECTORY_BLOCK_NAME = "";

    public DirectoryBlock(Block block, String name) throws InvalidNameException
    {
        super(block, name);
    }

    public DirectoryBlock(Block block)
    {
        super(block);
    }

    @Override
    public int size()
    {
        throw new ToDoException( );
    }
}
