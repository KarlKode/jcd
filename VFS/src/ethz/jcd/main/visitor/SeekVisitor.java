package ethz.jcd.main.visitor;

import ethz.jcd.main.VUtil;
import ethz.jcd.main.blocks.*;

/**
 * Created by phgamper on 3/6/14.
 */
public class SeekVisitor implements BlockVisitor<Block, VUtil>
{
    @Override
    public Block visit(Block block, VUtil arg)
    {
        return block.accept(this, arg);
    }

    @Override
    public Block block(Block block, VUtil arg)
    {
        return null;
    }

    @Override
    public Block blockList(BlockList block, VUtil arg)
    {
        return null;
    }

    @Override
    public Block directory(Directory block, VUtil arg)
    {
        return null;
    }

    @Override
    public Block file(File block, VUtil arg)
    {
        return null;
    }

    @Override
    public Block superBlock(SuperBlock block, VUtil arg)
    {
        return null;
    }
}
