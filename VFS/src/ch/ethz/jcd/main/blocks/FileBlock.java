package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.exceptions.ToDoException;
import ch.ethz.jcd.main.visitor.BlockVisitor;

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
        return visitor.file(this, arg);
    }

    @Override
    public int size()
    {
        throw new ToDoException( );
    }
}

