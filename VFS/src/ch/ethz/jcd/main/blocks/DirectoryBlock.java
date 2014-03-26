package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.exceptions.ToDoException;
import ch.ethz.jcd.main.visitor.BlockVisitor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * This class represents a DirectoryBlock. Each DirectoryBlock contains n BlockAddresses where
 * n is in [0, (blockSize - head)/addressSize]. If the a DirectoryBlock contains no
 * Block, the directory is empty. For the user a DirectoryBlock is visible as regular directory.
 */
public class DirectoryBlock extends ObjectBlock
{
    public DirectoryBlock(int blockAddress, byte[] bytes)
    {
        super(blockAddress, bytes);
    }

    @Override
    public long getSize()
    {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public List<ObjectBlock> getChildren()
    {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public void addChild(Block block)
    {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public void removeChild(Block block)
    {
        // TODO
        throw new NotImplementedException();
    }
}
