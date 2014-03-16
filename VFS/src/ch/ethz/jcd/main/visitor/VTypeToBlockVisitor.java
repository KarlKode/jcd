package ch.ethz.jcd.main.visitor;

import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.blocks.FileBlock;
import ch.ethz.jcd.main.blocks.InodeBlock;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VInode;

/**
 * This Visitor is used to convert a VInode into a Block.
 */
public class VTypeToBlockVisitor implements VTypeVisitor<InodeBlock, Block>
{
    /**
     * This method visits the given VInode.
     *
     * @param type to visit
     * @param arg Block containing information to use
     * @return created Block
     */
    @Override
    public InodeBlock visit(VInode type, Block arg)
    {
        return type.accept(this, arg);
    }

    /**
     * This method converts a VDirectory into a DirectoryBlock according to a
     * given Block.
     *
     * @param vdir to convert
     * @param arg block to use as reference
     * @return the created DirectoryBlock
     */
    @Override
    public InodeBlock directory(VDirectory vdir, Block arg)
    {
        try
        {
            return new DirectoryBlock(arg, vdir.getName());
        }
        catch (InvalidNameException e)
        {
            //should never happen
            return null;
        }
    }

    /**
     * This method converts a VFile into a FileBlock according to a given Block
     *
     * @param vfile to convert
     * @param arg block to use as reference
     * @return the created FileBlock
     */
    @Override
    public InodeBlock file(VFile vfile, Block arg)
    {
        try
        {
            return new FileBlock(arg, vfile.getName());
        }
        catch (InvalidNameException e)
        {
            //should never happen
            return null;
        }
    }
}
