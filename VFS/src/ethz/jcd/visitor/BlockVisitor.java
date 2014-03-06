package ethz.jcd.visitor;

import ethz.jcd.blocks.*;
import ethz.jcd.layer.VDirectory;
import ethz.jcd.layer.VFile;
import ethz.jcd.layer.VType;

/**
 * Created by phgamper on 3/6/14.
 */
public interface BlockVisitor<R, A>
{
    public R visit(Block block, A arg);

    public R block(Block block, A arg);

    public R blockList(BlockList<Block> block, A arg);

    public R directory(Directory block, A arg);

    public R file(File block, A arg);

    public R superBlock(SuperBlock block, A arg);
}
