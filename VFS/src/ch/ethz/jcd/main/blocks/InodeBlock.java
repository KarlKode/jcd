package ch.ethz.jcd.main.blocks;

public abstract class InodeBlock extends Block
{
    public InodeBlock()
    {
    }

    public InodeBlock(int blockAddress)
    {
        super(blockAddress);
    }

    public InodeBlock(byte[] bytes)
    {
        super(bytes);
    }

    public InodeBlock(int blockAddress, byte[] bytes)
    {
        super(blockAddress, bytes);
    }
}
