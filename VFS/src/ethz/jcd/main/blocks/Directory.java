package ethz.jcd.main.blocks;

public class Directory extends Inode
{
    protected BlockList<Inode> content = new BlockList<Inode>();

    public Directory()
    {
    }

    public Directory(int blockAddress)
    {
        super(blockAddress);
    }

    public Directory(byte[] bytes)
    {
        super(bytes);
    }

    public Directory(byte[] bytes, int blockAddress)
    {
        super(bytes, blockAddress);
    }

    public BlockList<Inode> getContent()
    {
        return content;
    }

    public void setContent(BlockList<Inode> content)
    {
        this.content = content;
    }
}
