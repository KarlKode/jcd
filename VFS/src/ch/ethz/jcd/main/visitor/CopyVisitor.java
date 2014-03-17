package ch.ethz.jcd.main.visitor;

import ch.ethz.jcd.main.blocks.*;
import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.DiskFullException;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.utils.Allocator;
import ch.ethz.jcd.main.utils.VUtil;

/**
 * This visitor copies each block which could be reached from the given VFile / VDirectory.
 * InodeBlock as return type is used to create the Block tree according to its given VInode tree.
 */
public class CopyVisitor implements BlockVisitor<Block, Void>
{
    private VUtil vUtil;
    private Allocator allocator;

    /**
     * This constructor builds a CopyVisitor.
     *
     * @param vUtil     Interface to read/write on the disk
     * @param allocator Util to allocate/free Blocks
     */
    public CopyVisitor(VUtil vUtil, Allocator allocator)
    {
        this.vUtil = vUtil;
        this.allocator = allocator;
    }

    /**
     * This method accept the passed Block.
     *
     * @param block to visit next
     * @param arg   to pass
     * @return the copied Block
     */
    @Override
    public Block visit(Block block, Void arg)
    {
        return block.accept(this, arg);
    }

    /**
     * This method copies and writes the Block passed as argument when visited.
     * No future visits needed
     *
     * @param block being visited
     * @param arg   passed from last Block
     * @return the copied Block
     */
    @Override
    public Block block(Block block, Void arg)
    {
        Block b;
        try
        {
            b = new Block(allocator.allocate());
            vUtil.write(b);
        } catch (DiskFullException e)
        {
            b = null;
        }
        return b;
    }

    /**
     * This method copies and writes the DirectoryBlock passed as argument when visited.
     * It also reads the given DirectoryBlock and visits all linked InodeBlocks
     *
     * @param block being visited
     * @param arg   passed from last Block
     * @return the copied DirectoryBlock
     */
    @Override
    public Block directory(DirectoryBlock block, Void arg)
    {
        DirectoryBlock dir;
        try
        {
            dir = new DirectoryBlock(allocator.allocate(), block.getName());

            for (Integer blockAddress : block.getBlockAddressList())
            {
                dir.add(visit(new InodeBlock(vUtil.read(blockAddress)), arg));
            }
            vUtil.write(dir);
        } catch (InvalidNameException e)
        {
            dir = null;
        } catch (DiskFullException e)
        {
            dir = null;
        } catch (BlockFullException e)
        {
            dir = null;
        }
        return dir;
    }

    /**
     * This method copies and writes the FileBlock passed as argument when visited.
     * It also reads FileBlock and visits all linked Blocks
     *
     * @param block being visited
     * @param arg   passed from last Block
     * @return the copied FileBlock
     */
    @Override
    public Block file(FileBlock block, Void arg)
    {
        FileBlock file;
        try
        {
            file = new FileBlock(allocator.allocate(), block.getName());
            for (Integer blockAddress : block.getBlockAddressList())
            {
                file.add(visit(vUtil.read(blockAddress), arg));
            }

            vUtil.write(file);
        } catch (InvalidNameException e)
        {
            file = null;
        } catch (DiskFullException e)
        {
            file = null;
        } catch (BlockFullException e)
        {
            file = null;
        }
        return file;
    }

    /**
     * This method evaluates whether the visited InodBlock is DirectoryBlock or a
     * FileBlock and visits them
     *
     * @param block being visited
     * @param arg   passed from last Block
     * @return the corresponding return value of detected inode type
     */
    @Override
    public Block inode(InodeBlock block, Void arg)
    {
        try
        {
            if (block.isDirectory())
            {
                return visit(new DirectoryBlock(block, block.getName()), arg);
            } else if (block.isFile())
            {
                return visit(new FileBlock(block, block.getName()), arg);
            }
            return null;
        } catch (InvalidNameException e)
        {
            return null;
        }
    }

    /**
     * This method should never been reached
     *
     * @param block being visited
     * @param arg   passed from last Block
     * @return null since the SuperBlock should not be copied
     */
    @Override
    public Block superBlock(SuperBlock block, Void arg)
    {
        return null;
    }

    /**
     * This method should never been reached
     *
     * @param block being visited
     * @param arg   passed from last Block
     * @return null since the SuperBlock should not be copied
     */
    @Override
    public Block bitMapBlock(BitMapBlock block, Void arg)
    {
        return null;
    }
}

