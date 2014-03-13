package ch.ethz.jcd.main.visitor;

import ch.ethz.jcd.main.layer.VType;
import ch.ethz.jcd.main.utils.VUtil;
import ch.ethz.jcd.main.blocks.*;

import java.util.Iterator;

/**
 * This visitor is used to determine whether a file exists or not.
 * Returns the loaded Block if the file exists, null otherwise.
 *
 * TODO de block muess au no glese werde
 *
 * @param <T> either a DirectoryBlock or a FileBlock
 */
public class SeekVisitor<T extends InodeBlock> implements BlockVisitor<T, Void>
{
    private String dest;
    private VUtil vUtil;

    /**
     *
     * @param dest Destination to seek for
     */
    public SeekVisitor(String dest, VUtil vUtil)
    {
        this.dest = dest;
        this.vUtil = vUtil;
    }

    /**
     *
     * @param dest Destination to seek for
     */
    public SeekVisitor(VType dest, VUtil vUtil)
    {
        this.dest = dest.getName();
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
     *
     * @param block current Block in search progress
     * @param arg VUtil used to load Blocks form disk
     * @return the loaded Block if the given destination is found, null otherwise
     */
    @Override
    public T directory(DirectoryBlock block, Void arg)
    {
        if (dest.equals(block.getName()))
        {
            return (T) block;
        }

        InodeBlock inode = null;

        Iterator<Integer> i = block.getBlockAddressList().iterator();

        while(i.hasNext() && inode == null)
        {
            Block b = vUtil.read(i.next());

            inode = visit(b, arg);
        }

        return (T) inode;
    }

    /**
     *
     * @param block current Block in search progress
     * @param arg VUtil used to load Blocks form disk
     * @return the loaded Block if the given destination is found, null otherwise
     */
    @Override
    public T file(FileBlock block, Void arg)
    {
        if (dest.equals(block.getName()))
        {
            return (T) block;
        }
        return null;
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
