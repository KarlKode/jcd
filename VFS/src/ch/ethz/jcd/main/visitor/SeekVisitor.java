package ch.ethz.jcd.main.visitor;

import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.layer.VType;
import ch.ethz.jcd.main.utils.VUtil;
import ch.ethz.jcd.main.blocks.*;

import java.util.Iterator;

/**
 * This visitor is used to determine whether a file exists or not.
 * Returns the loaded Block if the file exists, null otherwise.
 *
 * @param <T> either a DirectoryBlock or a FileBlock
 */
public class SeekVisitor<T extends InodeBlock> implements BlockVisitor<T, Void>
{
    private VType dest;
    private VUtil vUtil;

    /**
     *
     * @param dest Destination to seek for
     */
    public SeekVisitor(VType dest, VUtil vUtil)
    {
        this.dest = dest;
        this.vUtil = vUtil;
    }

    /**
     * This method visit the given Block
     *
     * @param block current Block in search progress
     * @param arg VUtil used to load Blocks form disk
     * @return the loaded Block if the given destination is found, null otherwise
     */
    @Override
    public T visit(Block block, Void arg)
    {
        return block.accept(this, arg);
    }

    /**
     * This method returns null because a Block represents a leaf in the file
     * system tree.
     *
     * @param block current Block in search progress
     * @param arg VUtil used to load Blocks form disk
     * @return the loaded Block if the given destination is found, null otherwise
     */
    @Override
    public T block(Block block, Void arg)
    {
        return null;
    }

    /**
     * This method checks if the given DirectoryBlock is the searched one, if not
     * it reads the block and visits all linked InodeBlocks
     *
     * @param block current Block in search progress
     * @param arg VUtil used to load Blocks form disk
     * @return the loaded Block if the given destination is found, null otherwise
     */
    @Override
    public T directory(DirectoryBlock block, Void arg)
    {
        if (dest.getName().equals(block.getName()))
        {
            return (T) block;
        }

        InodeBlock inode = null;

        Iterator<Integer> i = block.getBlockAddressList().iterator();

        while(i.hasNext() && inode == null)
        {
            Block b = vUtil.read(i.next());
            inode = visit(new InodeBlock(b), arg);
        }

        return (T) inode;
    }

    /**
     * This method checks if the given FileBlock match to the searched destination,
     * if it not returns null because a FileBlock represents a leaf in the inode
     * tree structure.
     *
     * @param block current Block in search progress
     * @param arg VUtil used to load Blocks form disk
     * @return the loaded Block if the given destination is found, null otherwise
     */
    @Override
    public T file(FileBlock block, Void arg)
    {
        if (dest.getName().equals(block.getName()))
        {
            return (T) block;
        }
        return null;
    }

    /**
     * This method evaluates whether the visited InodBlock is DirectoryBlock or a
     * FileBlock and visits them
     *
     * @param block current Block in search progress
     * @param arg VUtil used to load Blocks form disk
     * @return the loaded Block if the given destination is found, null otherwise
     */
    @Override
    public T inode(InodeBlock block, Void arg)
    {
        try
        {
            if(block.isDirectory())
            {
                return visit(new DirectoryBlock(block, block.getName()), arg);
            }
            else if(block.isFile())
            {
                return visit(new FileBlock(block, block.getName()), arg);
            }
            return null;
        }
        catch (InvalidNameException e)
        {
            return null;
        }
    }

    /**
     * This method return null in every case because the SuperBlock should never been reached.
     *
     * @param block current Block in search progress
     * @param arg VUtil used to load Blocks form disk
     * @return the loaded Block if the given destination is found, null otherwise
     */
    @Override
    public T superBlock(SuperBlock block, Void arg)
    {
        return null;
    }

    /**
     * This method return null in every case because the BitMapBlock should never been reached.
     *
     * @param block current Block in search progress
     * @param arg VUtil used to load Blocks form disk
     * @return the loaded Block if the given destination is found, null otherwise
     */
    @Override
    public T bitMapBlock(BitMapBlock block, Void arg)
    {
        return null;
    }
}
