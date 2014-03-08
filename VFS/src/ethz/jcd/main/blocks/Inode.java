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

    public Inode(int blockAddress, byte[] bytes)
    {
        super(blockAddress, bytes);
    }
}
