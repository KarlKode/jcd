package ch.ethz.jcd.main.blocks;

public abstract class InodeBlock extends Block
{
    protected String name;

    public InodeBlock( )
    {

    }

    public InodeBlock(Block block)
    {
        super(block);
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

    public String getName( )
    {
        return name;
    }
}
