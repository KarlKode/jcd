package ch.ethz.jcd.main.visitor;

import ch.ethz.jcd.main.blocks.*;

public interface BlockVisitor<R, A>
{
    public R visit(Block block, A arg);

    public R block(Block block, A arg);

    public R blockList(BlockList<Block> block, A arg);

    public R directory(DirectoryBlock block, A arg);

    public R file(FileBlock block, A arg);

    public R superBlock(SuperBlock block, A arg);
}
