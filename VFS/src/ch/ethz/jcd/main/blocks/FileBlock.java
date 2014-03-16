package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.exceptions.ToDoException;
import ch.ethz.jcd.main.visitor.BlockVisitor;

/**
 * This class represents a FileBlock. Each FileBlock contains n BlockAddresses where
 * n is in [0, (blockSize - head)/addressSize]. If the a FileBlock contains no
 * Block, the file is empty. For the user a FileBlock is visible as regular file.
 */
public class FileBlock extends InodeBlock
{
    /**
     * Instantiate a new FileBlock by cloning the given Block and adding the
     * given name.
     *
     * @param b to clone
     * @param name to set
     * @throws InvalidNameException if the given name is invalid
     */
    public FileBlock(Block b, String name) throws InvalidNameException
    {
        super(b, name);
    }

    /**
     * Instantiate a new FileBlock by cloning the given Block without naming
     * the FileBlock.
     *
     * @param b to clone
     */
    public FileBlock(Block b)
    {
        super(b);
    }

    /**
     * This method is part of the visitor pattern and is called by the visitor.
     * It tells to the visitor which sort of Block he called.
     *
     * @param visitor calling this method
     * @param arg to pass
     * @param <R> generic return type
     * @param <A> generic argument type
     * @return the visitors return value
     */
    @Override
    public <R, A> R accept(BlockVisitor<R, A> visitor, A arg)
    {
        return visitor.file(this, arg);
    }

    /**
     *
     * @return size of the file
     */
    @Override
    public int size()
    {
        throw new ToDoException( );
    }
}

