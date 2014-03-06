package ethz.jcd.visitor;

import ethz.jcd.VUtil;
import ethz.jcd.blocks.*;

/**
 * Created by phgamper on 3/6/14.
 */
public class SeekVisitor implements  BlockVisitor<Inode, VUtil>
{
    @Override
    public Inode visit(Block block, VUtil arg)
    {
        return block.accept(this, arg);
    }

    @Override
    public Inode block(Block block, VUtil arg)
    {
        return null;
    }

    @Override
    public Inode blockList(BlockList block, VUtil arg)
    {
        return null;
    }

    @Override
    public Inode directory(Directory block, VUtil arg)
    {
        return null;
    }

    @Override
    public Inode file(File block, VUtil arg)
    {
        return null;
    }

    @Override
    public Inode superBlock(SuperBlock block, VUtil arg)
    {
        return null;
    }
}
