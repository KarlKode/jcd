package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.blocks.InodeBlock;
import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.exceptions.InvalidBlockSizeException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import ch.ethz.jcd.main.exceptions.*;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VType;
import ch.ethz.jcd.main.visitor.CopyVisitor;
import ch.ethz.jcd.main.visitor.SeekVisitor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileNotFoundException;

public class VDisk
{
    private VUtil vUtil;
    private Allocator allocator;

    private DirectoryBlock root;

    /**
     * Open an existing VDisk file that contains a valid VFS
     *
     * @param vDiskFile path to the VDisk file
     */
    public VDisk(String vDiskFile) throws FileNotFoundException
    {
        vUtil = new VUtil(vDiskFile);
        init( );
    }

    /**
     * Create a new VDisk
     *
     * @param vDiskFile path to the VDisk file
     * @param size      total size of the VDisk
     */
    public VDisk(String vDiskFile, long size, int blockSize) throws VDiskCreationException, InvalidBlockSizeException, InvalidSizeException, FileNotFoundException, InvalidBlockCountException
    {
        vUtil = new VUtil(vDiskFile, size, blockSize);
        init( );
    }

    private void init( )
    {
        allocator = new Allocator(vUtil);
        Block b = vUtil.read(vUtil.getSuperBlock().getRootDirectoryBlock());
        try
        {
            root = new DirectoryBlock(b, "");
        }
        catch (InvalidNameException e)
        {
            e.printStackTrace();
        }
        // TODO
        throw new NotImplementedException();
    }

    /**
     * This method creates either an EMPTY directory or an Empty file.
     *
     * @param src  - either a VDirectory or a VFile
     * @param dest - destination
     * @return - create InodeBlock in the VFS
     */
    public void create(VType src, VDirectory dest) throws DiskFullException, NoSuchFileOrDirectoryException, BlockFullException, InvalidNameException
    {
        SeekVisitor<DirectoryBlock> sv = new SeekVisitor<>(dest, vUtil);

        InodeBlock block = src.toBlock(allocator.allocate());
        DirectoryBlock destDir = sv.visit(root, null);
        destDir.add(block);

        vUtil.write(block);
        vUtil.write(destDir);
    }

    public void delete(VType file)
    {
        throw new NotImplementedException();
    }

    public void move(VType src, VType dest)
    {
        throw new NotImplementedException();
    }

    public void list(VDirectory file)
    {
        throw new NotImplementedException();
    }

    /**
     * This method copies either a directory or a file. Note that the whole
     * structure is copied. There are no optimization like "copy on write".
     *
     * @param src  - source, either a VDirectory or a VFile
     * @param dest - destination
     */
    public void copy(VType src, VDirectory dest) throws BlockFullException
    {
        CopyVisitor cv = new CopyVisitor(vUtil, allocator);
        InodeBlock i = (InodeBlock) cv.visit(src.getInode(), null);

        SeekVisitor<DirectoryBlock> sv = new SeekVisitor<>(dest, vUtil);
        DirectoryBlock destDir = sv.visit(root, null);
        destDir.add(i);

        vUtil.write(destDir);
    }

    public void store(VType src, VType dest)
    {
        throw new NotImplementedException();
    }

    public void load(VType file)
    {
        throw new NotImplementedException();
    }

    public void stats()
    {
        throw new NotImplementedException();
    }
}
