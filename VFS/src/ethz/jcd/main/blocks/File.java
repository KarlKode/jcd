package ethz.jcd.main.blocks;

import ethz.jcd.main.Config;

public class File extends Inode
{
    protected BlockList<Block> blocks = new BlockList<Block>();

    public File(Block b)
    {
        this.address = b.address;
    }

    public File(int blockAddress)
    {
        super(blockAddress);
    }

    public File(byte[] bytes)
    {
        super(bytes);
    }

    public File(byte[] bytes, int blockAddress)
    {
        super(bytes, blockAddress);
    }

    public int size()
    {
        // TODO: store the block size in the VDisk superblock
        return blocks.size() * Config.VFS_BLOCK_SIZE;
    }

    public BlockList<Block> getBlocks()
    {
        return blocks;
    }

    public void setBlocks(BlockList<Block> blocks)
    {
        this.blocks = blocks;
    }
}
