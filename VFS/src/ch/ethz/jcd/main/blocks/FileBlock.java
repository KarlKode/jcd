package ch.ethz.jcd.main.blocks;

public class FileBlock extends InodeBlock
{
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
}
