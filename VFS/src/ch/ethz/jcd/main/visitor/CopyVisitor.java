package ch.ethz.jcd.main.visitor;

import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.DiskFullException;
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
    public Block block(Block block, Void arg) throws DiskFullException
    {
        Block b = new Block(allocator.allocate());
        vUtil.write(b);
        return b;
    }

    @Override
    public Block directory(DirectoryBlock block, Void arg) throws DiskFullException, BlockFullException
    {
        DirectoryBlock dir = new DirectoryBlock(allocator.allocate());

        for (Integer blockAddress : block.getBlockAddressList())
        {
            dir.add(visit(vUtil.read(blockAddress), arg));
        }

        vUtil.write(dir);

        return dir;
    }

    @Override
    public Block file(FileBlock block, Void arg) throws DiskFullException, BlockFullException
    {
        FileBlock file = new FileBlock(allocator.allocate());

        for (Integer blockAddress : block.getBlockAddressList())
        {
            file.add(visit(vUtil.read(blockAddress), arg));
        }

        vUtil.write(file);

        return file;
    }

    @Override
    public Block superBlock(SuperBlock block, Void arg)
    {
        return null;
    }

    @Override
    public Block bitMapBlock(BitMapBlock block, Void arg)
    {
        return null;
    }
}

