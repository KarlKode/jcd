package ethz.jcd.blocks;

import ethz.jcd.Config;

/**
 * Created by phgamper on 3/6/14.
 */
public class File extends Inode
{
    protected BlockList<Block> blocks = new BlockList<Block>();

    public int size( )
    {
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
