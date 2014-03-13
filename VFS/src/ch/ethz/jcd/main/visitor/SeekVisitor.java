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
public class SeekVisitor<T extends InodeBlock> implements BlockVisitor<T, VUtil>
{
    private String dest;

    /**
     *
     * @param dest Destination to seek for
     */
    public SeekVisitor(String dest)
    {
        this.dest = dest;
    }

    /**
     *
     * @param dest Destination to seek for
     */
    public SeekVisitor(VType dest)
    {
        this.dest = dest.getName();
    }

    /**
     * This method visit the given Block
     *
     * @param block current Block in search progress
     * @param arg VUtil used to load Blocks form disk
     * @return the loaded Block if the given destination is found, null otherwise
     */
    @Override
    public T visit(Block block, VUtil arg)
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
    public T block(Block block, VUtil arg)
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
    public T blockList(BlockList block, VUtil arg)
    {
        InodeBlock inode = null;

        Iterator<Block> i = block.list().iterator();

        while(i.hasNext() && inode == null)
        {
            //TODO entweder Block oder Inode, ... mömmer wohrschindli im block ine ablege
            Block b = arg.read(i.next().getAddress());

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
    public T directory(DirectoryBlock block, VUtil arg)
    {
        if (dest.equals(block.getName()))
        {
            return (T) block;
        }

        BlockList<InodeBlock> list = new BlockList<InodeBlock>(arg.read(block.getContent().getAddress()));

        return (T) visit(list, arg);
    }

    /**
     *
     * @param block current Block in search progress
     * @param arg VUtil used to load Blocks form disk
     * @return the loaded Block if the given destination is found, null otherwise
     */
    @Override
    public T file(FileBlock block, VUtil arg)
    {
        if (dest.equals(block.getName()))
        {
            return (T) block;
        }

        // BlockList<Block> list = new BlockList<Block>(arg.read(block.getBlocks().getAddress()));
        // return (T) visit(list, arg);
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
    public T superBlock(SuperBlock block, VUtil arg)
    {
        return null;
    }
}
