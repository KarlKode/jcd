package ch.ethz.jcd.main.visitor;

import ch.ethz.jcd.main.blocks.*;
import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.DiskFullException;
import ch.ethz.jcd.main.exceptions.InvalidNameException;

public interface BlockVisitor<R, A>
{
    public R visit(Block block, A arg);

    public R block(Block block, A arg) throws DiskFullException;

    public R directory(DirectoryBlock block, A arg) throws DiskFullException, BlockFullException, InvalidNameException;

    public R file(FileBlock block, A arg) throws DiskFullException, BlockFullException, InvalidNameException;

    public R inode(InodeBlock block, A arg) throws InvalidNameException;

    public R superBlock(SuperBlock block, A arg);

    public R bitMapBlock(BitMapBlock block, A arg);
}
