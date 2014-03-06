package ethz.jcd.main.blocks;

public class Directory extends Inode
{
    protected BlockList<Inode> content = new BlockList<Inode>();

    public BlockList<Inode> getContent()
    {
        return content;
    }

    public void setContent(BlockList<Inode> content)
    {
        this.content = content;
    }
}
