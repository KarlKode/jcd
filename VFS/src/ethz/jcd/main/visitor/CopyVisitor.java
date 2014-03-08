package ethz.jcd.main.visitor;

import ethz.jcd.main.VUtil;
import ethz.jcd.main.blocks.*;

/**
 * <p/>
 * This visitor copies each block which could be reached from the given VFile / VDirectory.
 * InodeBlock as return type is used to create the Block tree according to its given VType tree.
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

        for (Block b : block.list())
        {
            bl.add(visit(b, arg));
        }

        bl.setAddress(arg.write(bl));

        return bl;
    }

    @Override
    public Block directory(DirectoryBlock block, VUtil arg)
    {
        DirectoryBlock dir = new DirectoryBlock();

        BlockList<InodeBlock> bl = (BlockList<InodeBlock>) visit(block.getContent(), arg);

        dir.setContent(bl);

        dir.setAddress(arg.write(dir));

        return dir;
    }

    @Override
    public Block file(FileBlock block, VUtil arg)
    {
        FileBlock fileBlock = new FileBlock();

        BlockList<Block> bl = (BlockList<Block>) visit(block.getBlocks(), arg);

        fileBlock.setBlocks(bl);

        fileBlock.setAddress(arg.write(fileBlock));

        return fileBlock;
    }

    @Override
    public Block superBlock(SuperBlock block, VUtil arg)
    {
        return directory(block, arg);
    }
}

