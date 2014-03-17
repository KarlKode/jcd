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
import ch.ethz.jcd.main.layer.VInode;
import ch.ethz.jcd.main.visitor.CopyVisitor;
import ch.ethz.jcd.main.visitor.DeleteVisitor;
import ch.ethz.jcd.main.visitor.SeekVisitor;
import ch.ethz.jcd.main.visitor.VTypeToBlockVisitor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileNotFoundException;
import java.util.Queue;

/**
 * This class provides an abstract interface to the user, for example a
 * console application. The user don't have to know anything about how
 * the disk operation are implemented.
 */
public class VDisk
{
    private VUtil vUtil;
    private Allocator allocator;
    private VTypeToBlockVisitor vtbv = new VTypeToBlockVisitor( );
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

    /**
     * This method prepares the loaded disk for usage.
     */
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
            // TODO
            throw new NotImplementedException();
        }
    }

    /**
     * This method creates either an EMPTY directory or an Empty file.
     *
     * @param inode   either a VDirectory or a VFile
     * @param dest  destination
     * @throws DiskFullException                if there is no space for disk creation left
     * @throws InvalidNameException             if filename of the source file is invalid
     * @throws BlockFullException               if the destination directory is full
     * @throws NoSuchFileOrDirectoryException   if the destination is not found
     */
    public void create(VInode inode, VDirectory dest) throws DiskFullException, InvalidNameException, BlockFullException, NoSuchFileOrDirectoryException
    {
        SeekVisitor<DirectoryBlock> sv = new SeekVisitor<>(vUtil);
        InodeBlock block = vtbv.visit(inode, allocator.allocate());
        DirectoryBlock destDir = sv.visit(root, dest.getPathQueue());

        if(destDir == null)
        {
            throw new NoSuchFileOrDirectoryException();
        }

        destDir.add(block);
        vUtil.write(block);
        vUtil.write(destDir);
    }

    /**
     * This method deletes the given File/Directory from disk.
     *
     * @param dest to delete
     */
    public void delete(VInode dest) throws NoSuchFileOrDirectoryException
    {
        SeekVisitor<DirectoryBlock> sv = new SeekVisitor<>(vUtil);
        InodeBlock inode = sv.visit(root, dest.getPathQueue());

        DeleteVisitor dv = new DeleteVisitor(vUtil, allocator);
        dv.visit(inode, null);
    }

    /**
     * This method moves a given file or directory and its structure to an other
     * location. Also use it to rename files or directories
     *
     * @param src   file/directory to move
     * @param dest  where to move
     */
    public void move(VInode src, VInode dest) throws NoSuchFileOrDirectoryException, InvalidNameException
    {
        SeekVisitor<DirectoryBlock> sv = new SeekVisitor<>(vUtil);
        InodeBlock srcInode = sv.visit(root, src.getPathQueue());
        InodeBlock destInode = sv.visit(root, dest.getPathQueue());


    }

    /**
     * This method lists the content of the given directory.
     *
     * @param dir   to list
     */
    public void list(VDirectory dir) throws NoSuchFileOrDirectoryException
    {
        throw new NotImplementedException();
    }

    /**
     * This method copies either a directory or a file. Note that the whole
     * structure is copied. There are no optimization like "copy on write".
     *
     * @param src   source, either a VDirectory or a VFile
     * @param dest  destination
     * @throws BlockFullException               if the destination directory is full
     */
    public void copy(VInode src, VDirectory dest) throws BlockFullException, NoSuchFileOrDirectoryException
    {
        CopyVisitor cv = new CopyVisitor(vUtil, allocator);
        InodeBlock i = (InodeBlock) cv.visit(src.getInode(), null);

        SeekVisitor<DirectoryBlock> sv = new SeekVisitor<>(vUtil);
        DirectoryBlock destDir = sv.visit(root, dest.getPathQueue());

        if(destDir == null)
        {
            throw new NoSuchFileOrDirectoryException();
        }

        destDir.add(i);
        vUtil.write(destDir);
    }

    /**
     * This method stores a file or a directory imported from the host file system
     *
     * @param src   source, either a VDirectory or a VFile
     * @param dest  destination
     */
    public void store(VInode src, VInode dest)
    {
        throw new NotImplementedException();
    }

    /**
     * This method loads a file or a directory and prepare to export to the
     * host file system
     *
     * @param file
     */
    public void load(VInode file)
    {
        throw new NotImplementedException();
    }

    /**
     * This method delivers the stats to the currently loaded disk
     */
    public void stats()
    {
        throw new NotImplementedException();
    }
}
