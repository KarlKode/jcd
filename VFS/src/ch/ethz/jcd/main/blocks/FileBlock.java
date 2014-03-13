package ch.ethz.jcd.main.blocks;

import java.util.LinkedList;

public class FileBlock extends InodeBlock
{
    private LinkedList<Block> blocks = new LinkedList<Block>();

    public FileBlock(Block b)
    {
        super(b);
    }

    public FileBlock(int blockAddress)
    {
        super(blockAddress);
    }

    public FileBlock(byte[] bytes)
    {
        super(bytes);
    }

    public FileBlock(byte[] bytes, int blockAddress)
    {
        super(blockAddress, bytes);
    }

    public void add(Block block)
    {
        blocks.add(block);
    }

    public int size()
    {
        //TODO richtig berechne
        return blocks.size();
    }

    public LinkedList<Block> getBlocks()
    {
        return blocks;
    }
}
