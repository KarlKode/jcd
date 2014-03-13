package ch.ethz.jcd.main.visitor;

import ch.ethz.jcd.main.layer.VType;
import ch.ethz.jcd.main.utils.VUtil;
import ch.ethz.jcd.main.blocks.*;

public class SeekVisitor<T extends InodeBlock> implements BlockVisitor<T, VUtil>
{
    private String dest;

    public SeekVisitor(String dest)
    {
        this.dest = dest;
    }

    public SeekVisitor(VType dest)
    {
        this.dest = dest.getName();
    }

    @Override
    public T visit(Block block, VUtil arg)
    {
        return block.accept(this, arg);
    }

    @Override
    public T block(Block block, VUtil arg)
    {
        return null;
    }

    @Override
    public T blockList(BlockList block, VUtil arg)
    {
        return null;
    }

    @Override
    public T directory(DirectoryBlock block, VUtil arg)
    {
        return null;
    }

    @Override
    public T file(FileBlock block, VUtil arg)
    {
        return null;
    }

    @Override
    public T superBlock(SuperBlock block, VUtil arg)
    {
        return null;
    }
}
