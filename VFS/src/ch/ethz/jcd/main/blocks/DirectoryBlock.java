package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.exceptions.ToDoException;
import ch.ethz.jcd.main.visitor.BlockVisitor;

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

    /**
     * TODO describe
     * @param visitor
     * @param arg
     * @param <R>
     * @param <A>
     * @return
     */
    public <R, A> R accept(BlockVisitor<R, A> visitor, A arg)
    {
        return visitor.directory(this, arg);
    }

    @Override
    public int size()
    {
        throw new ToDoException( );
    }
}
