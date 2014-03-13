package ch.ethz.jcd.main.visitor;

import ch.ethz.jcd.main.utils.Allocator;
import ch.ethz.jcd.main.utils.VUtil;
import ch.ethz.jcd.main.blocks.*;

/**
 * This visitor copies each block which could be reached from the given VFile / VDirectory.
 * InodeBlock as return type is used to create the Block tree according to its given VType tree.
 */
public class CopyVisitor implements BlockVisitor<Block, Void>
{
    private VUtil vUtil;
    private Allocator allocator;

    public CopyVisitor(VUtil vUtil, Allocator allocator)
    {
        this.vUtil = vUtil;
        this.allocator = allocator;
    }

    @Override
    public Block visit(Block block, Void arg)
    {
        return block.accept(this, arg);
    }

    @Override
    public Block block(Block block, Void arg)
    {
        vUtil.write(block);
        return new Block(block.getAddress());
    }

    @Override
    public Block directory(DirectoryBlock block, Void arg)
    {
        DirectoryBlock dir = new DirectoryBlock(allocator.allocate());

        for (Block b : block.getBlocks())
        {
            dir.add((InodeBlock) visit(b, arg));
        }

        vUtil.write(dir);

        return dir;
    }

    @Override
    public Block file(FileBlock block, Void arg)
    {
        FileBlock file = new FileBlock();

        for (Block b : block.getBlocks())
        {
            file.add(visit(b, arg));
        }

        vUtil.write(file);

        return file;
    }

    @Override
    public Block superBlock(SuperBlock block, Void arg)
    {
        return directory(block, arg);
    }
}

