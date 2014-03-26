package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.exceptions.ToDoException;
import ch.ethz.jcd.main.visitor.BlockVisitor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * This class represents a FileBlock. Each FileBlock contains n BlockAddresses where
 * n is in [0, (blockSize - head)/addressSize]. If the a FileBlock contains no
 * Block, the file is empty. For the user a FileBlock is visible as regular file.
 */
public class FileBlock extends ObjectBlock
{

    public FileBlock(int blockAddress, byte[] bytes)
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

