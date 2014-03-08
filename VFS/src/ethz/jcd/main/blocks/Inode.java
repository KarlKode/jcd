package ethz.jcd.main.blocks;

public abstract class Inode extends Block
{
    public Inode()
    {
    }

    public Inode(int blockAddress)
    {
        super(blockAddress);
    }

    public Inode(byte[] bytes)
    {
        super(bytes);
    }

    public Inode(byte[] bytes, int blockAddress)
    {
        super(bytes, blockAddress);
    }
}
