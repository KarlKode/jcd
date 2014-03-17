package ch.ethz.jcd.main.visitor;

import ch.ethz.jcd.main.blocks.*;
import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.DiskFullException;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.utils.Allocator;
import ch.ethz.jcd.main.utils.VUtil;

/**
 * This visitor a given Block as well as all linked subtrees
 */
public class DeleteVisitor implements BlockVisitor<Void, Void>
{
    private VUtil vUtil;
    private Allocator allocator;

    /**
     * Instantiate a new DeleteVisitor
     *
     * @param vUtil     Interface to read/write on the disk
     * @param allocator Util to allocate/free Blocks
     */
    public DeleteVisitor(VUtil vUtil, Allocator allocator)
    {
        this.vUtil = vUtil;
        this.allocator = allocator;
    }

    /**
     * This method accept the passed Block.
     *
     * @param block to visit next
     * @param arg to pass
     * @return the copied Block
     */
    @Override
    public Void visit(Block block, Void arg)
    {
        return block.accept(this, arg);
    }

    /**
     * This method deletes the Block passed as argument when visited.
     * No future visits needed
     *
     * @param block being visited
     * @param arg passed from last Block
     * @return the copied Block
     */
    @Override
    public Void block(Block block, Void arg)
    {
        allocator.free(block);
        return null;
    }

    /**
     * This method deletes the DirectoryBlock passed as argument when visited.
     * It also reads the given DirectoryBlock and visits all linked InodeBlocks
     *
     * @param block being visited
     * @param arg passed from last Block
     * @return the copied DirectoryBlock
     */
    @Override
    public Void directory(DirectoryBlock block, Void arg)
    {
        //TODO im moment chan me no s root directory lösche

        for (Integer blockAddress : block.getBlockAddressList())
        {
            visit(new InodeBlock(vUtil.read(blockAddress)), arg);
        }
        allocator.free(block);
        return null;
    }

    /**
     * This method copies and writes the FileBlock passed as argument when visited.
     * It also reads FileBlock and visits all linked Blocks
     *
     * @param block being visited
     * @param arg passed from last Block
     * @return the copied FileBlock
     */
    @Override
    public Void file(FileBlock block, Void arg)
    {
        for (Integer blockAddress : block.getBlockAddressList())
        {
            visit(vUtil.read(blockAddress), arg);
        }
        allocator.free(block);
        return null;
    }

    /**
     * This method evaluates whether the visited InodBlock is DirectoryBlock or a
     * FileBlock and visits them
     *
     * @param block being visited
     * @param arg passed from last Block
     * @return the corresponding return value of detected inode type
     */
    @Override
    public Void inode(InodeBlock block, Void arg)
    {
        if(block.isDirectory())
        {
            return visit(new DirectoryBlock(block), arg);
        }
        else if(block.isFile())
        {
            return visit(new FileBlock(block), arg);
        }
        return null;
    }

    /**
     * This method should never been reached
     *
     * @param block being visited
     * @param arg passed from last Block
     * @return null since the SuperBlock should not be copied
     */
    @Override
    public Void superBlock(SuperBlock block, Void arg)
    {
        return null;
    }

    /**
     * This method should never been reached
     *
     * @param block being visited
     * @param arg passed from last Block
     * @return null since the SuperBlock should not be copied
     * */
    @Override
    public Void bitMapBlock(BitMapBlock block, Void arg)
    {
        return null;
    }
}

