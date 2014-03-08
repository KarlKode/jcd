package ethz.jcd.main.visitor;

import ethz.jcd.main.VUtil;
import ethz.jcd.main.blocks.*;

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
    public Block directory(DirectoryBlock block, VUtil arg)
    {
        return null;
    }

    @Override
    public Block file(FileBlock block, VUtil arg)
    {
        return null;
    }

    @Override
    public Block superBlock(SuperBlock block, VUtil arg)
    {
        return null;
    }
}
