package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.Config;

public class FileBlock extends InodeBlock
{
    protected BlockList<Block> blocks = new BlockList<Block>();

    public FileBlock(Block b)
    {
        this.address = b.address;
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
