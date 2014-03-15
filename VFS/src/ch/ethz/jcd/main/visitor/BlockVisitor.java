package ch.ethz.jcd.main.visitor;

import ch.ethz.jcd.main.blocks.*;
import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.DiskFullException;
import ch.ethz.jcd.main.exceptions.InvalidNameException;

/**
 * BlockVisitor<R, A> provides an interface to visit the Block structure.
 *
 * @param <R> Generic return type
 * @param <A> Generic argument to pass
 */
public interface BlockVisitor<R, A>
{
    /**
     * This method visits a given Block. Usually done by invoking block.accept( ).
     * Because of the dynamic type binding of Java, the specialized Block knows
     * which method of the visitor to call.
     *
     * @param block to visit next
     * @param arg to pass
     * @return Generic type R
     */
    public R visit(Block block, A arg);

    /**
     * This method visits the general Block
     *
     * @param block being visited
     * @param arg passed from last Block
     * @return Generic type R
     */
    public R block(Block block, A arg);

    /**
     * This method visits the DirectoryBlock
     *
     * @param block being visited
     * @param arg passed from last Block
     * @return Generic type R
     */
    public R directory(DirectoryBlock block, A arg);

    /**
     * This method visits the FileBlock
     *
     * @param block being visited
     * @param arg passed from last Block
     * @return Generic type R
     */
    public R file(FileBlock block, A arg);

    /**
     * This method visits the InodeBlock
     *
     * @param block being visited
     * @param arg passed from last Block
     * @return Generic type R
     */
    public R inode(InodeBlock block, A arg);

    /**
     * This method visits the SuperBlock
     *
     * @param block being visited
     * @param arg passed from last Block
     * @return Generic type R
     */
    public R superBlock(SuperBlock block, A arg);

    /**
     * This method visits the BitMapBlock
     *
     * @param block being visited
     * @param arg passed from last Block
     * @return Generic type R
     */
    public R bitMapBlock(BitMapBlock block, A arg);
}
