package ch.ethz.jcd.main.blocks;

import java.util.LinkedList;

public class DirectoryBlock extends InodeBlock
{
    private LinkedList<InodeBlock> blocks = new LinkedList<InodeBlock>();

    public DirectoryBlock()
    {
    }

    public DirectoryBlock(Block block)
    {
        super(block);
    }

    public DirectoryBlock(int blockAddress)
    {
        super(blockAddress);
    }

    public DirectoryBlock(byte[] bytes)
    {
        super(bytes);
    }

    public DirectoryBlock(int blockAddress, byte[] bytes)
    {
        super(blockAddress, bytes);
    }

    public void add(InodeBlock inode)
    {
        blocks.add(inode);
    }

    public int size()
    {
        return blocks.size();
    }

    public LinkedList<InodeBlock> getBlocks()
    {
        return blocks;
    }
}
