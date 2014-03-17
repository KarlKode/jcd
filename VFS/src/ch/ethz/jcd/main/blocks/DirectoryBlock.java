package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.exceptions.ToDoException;
import ch.ethz.jcd.main.visitor.BlockVisitor;

/**
 * This class represents a DirectoryBlock. Each DirectoryBlock contains n BlockAddresses where
 * n is in [0, (blockSize - head)/addressSize]. If the a DirectoryBlock contains no
 * Block, the directory is empty. For the user a DirectoryBlock is visible as regular directory.
 */
public class DirectoryBlock extends InodeBlock
{
    public static final String ROOT_DIRECTORY_BLOCK_NAME = "";
    public static final String ROOT_DIRECTORY_BLOCK_PATH = "/";

    /**
     * Instantiate a new DirectoryBlock by cloning the given Block and adding the
     * given name.
     *
     * @param block to clone
     * @param name  to set
     * @throws InvalidNameException if the given name is invalid
     */
    public DirectoryBlock(Block block, String name) throws InvalidNameException
    {
        super(block, name);
    }

    /**
     * Instantiate a new DirectoryBlock by cloning the given Block without naming
     * the DirectoryBlock.
     *
     * @param block to clone
     */
    public DirectoryBlock(Block block)
    {
        super(block);
    }

    /**
     * This method is part of the visitor pattern and is called by the visitor.
     * It tells to the visitor which sort of Block he called.
     *
     * @param visitor calling this method
     * @param arg     to pass
     * @param <R>     generic return type
     * @param <A>     generic argument type
     * @return the visitors return value
     */
    @Override
    public <R, A> R accept(BlockVisitor<R, A> visitor, A arg)
    {
        return visitor.directory(this, arg);
    }

    /**
     * @return size of the directory
     */
    @Override
    public int size()
    {
        throw new ToDoException();
    }
}
