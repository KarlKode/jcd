package ethz.jcd.main.visitor;

import ethz.jcd.main.VUtil;
import ethz.jcd.main.blocks.*;

/**
 * Created by phgamper on 3/6/14.
 * <p/>
 * This visitor copies each block which could be reached from the given VFile / VDirectory.
 * Inode as return type is used to create the Block tree according to its given VType tree.
 */
public class CopyVisitor implements BlockVisitor<Block, VUtil>
{
    @Override
    public Block visit(Block block, VUtil arg)
    {
        return block.accept(this, arg);
    }

    @Override
    public Block block(Block block, VUtil arg)
    {
        return new Block(arg.write(block));
    }

    @Override
    public Block blockList(BlockList<Block> block, VUtil arg)
    {
        BlockList<Block> bl = new BlockList<Block>();

        for(Block b : block.list())
        {
            bl.add(visit(b, arg));
        }

        bl.setAddress(arg.write(bl));

        return bl;
    }

    @Override
    public Block directory(Directory block, VUtil arg)
    {
        Directory dir = new Directory();

        BlockList<Inode> bl = (BlockList<Inode>) visit(block.getContent(), arg);

        dir.setContent(bl);

        dir.setAddress(arg.write(dir));

        return dir;
    }

    @Override
    public Block file(File block, VUtil arg)
    {
        File file = new File();

        BlockList<Block> bl = (BlockList<Block>) visit(block.getBlocks(), arg);

        file.setBlocks(bl);

        file.setAddress(arg.write(file));

        return file;
    }

    @Override
    public Block superBlock(SuperBlock block, VUtil arg)
    {
        return directory(block, arg);
    }
}

