package ch.ethz.jcd.main.utils;

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

    /**
     * Open an existing VDisk file that contains a valid VFS
     *
     * @param vDiskFile path to the VDisk file
     */
    public VDisk(String vDiskFile) throws FileNotFoundException
    {
        vUtil = new VUtil(vDiskFile);
        allocator = new Allocator(vUtil);
        // TODO
        throw new NotImplementedException();
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
        allocator = new Allocator(vUtil);
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
    public void create(VType src, VDirectory dest) throws DiskFullException, NoSuchFileOrDirectoryException
    {
        SeekVisitor<DirectoryBlock> sv = new SeekVisitor<DirectoryBlock>(dest);

        InodeBlock block = src.toBlock(allocator.allocate());
        DirectoryBlock root = new DirectoryBlock(vUtil.getSuperBlock().getRootDirectoryBlock());
        DirectoryBlock dir = sv.visit(root, vUtil);
        dir.add(block);

        vUtil.write(block);
        vUtil.write(dir);
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
    public void copy(VType src, VDirectory dest)
    {
        CopyVisitor cv = new CopyVisitor();
        InodeBlock i = (InodeBlock) cv.visit(src.getInode(), vUtil);
        DirectoryBlock dir = (DirectoryBlock) dest.getInode();
        dir.add(i);
        vUtil.write(dir);
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
