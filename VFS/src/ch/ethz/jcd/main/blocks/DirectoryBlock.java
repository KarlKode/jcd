package ch.ethz.jcd.main.blocks;

public class DirectoryBlock extends InodeBlock
{
    protected BlockList<InodeBlock> content = new BlockList<InodeBlock>();

    public DirectoryBlock()
    {
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

    public BlockList<InodeBlock> getContent()
    {
        return content;
    }

    public void setContent(BlockList<InodeBlock> content)
    {
        this.content = content;
    }
}
