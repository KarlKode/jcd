package ethz.jcd.main;

import ethz.jcd.main.blocks.Directory;
import ethz.jcd.main.blocks.Inode;
import ethz.jcd.main.blocks.SuperBlock;
import ethz.jcd.main.exceptions.InvalidBlockSize;
import ethz.jcd.main.exceptions.InvalidSize;
import ethz.jcd.main.exceptions.VDiskCreationException;
import ethz.jcd.main.layer.VDirectory;
import ethz.jcd.main.layer.VType;
import ethz.jcd.main.visitor.CopyVisitor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileNotFoundException;

public class VDisk
{
    private VUtil util;

    private SuperBlock root = SuperBlock.getInstance();

    /**
     * Open an existing VDisk file that contains a valid VFS
     *
     * @param vDiskFile path to the VDisk file
     */
    public VDisk(String vDiskFile) throws FileNotFoundException
    {
        util = new VUtil(vDiskFile);
        // TODO
        throw new NotImplementedException();
    }

    /**
     * Create a new VDisk
     *
     * @param vDiskFile path to the VDisk file
     * @param size      total size of the VDisk
     */
    public VDisk(String vDiskFile, long size, int blockSize) throws VDiskCreationException, InvalidBlockSize, InvalidSize
    {
        util = new VUtil(vDiskFile, size, blockSize);
        // TODO
        throw new NotImplementedException();
    }

    /**
     * This method creates either an EMPTY directory or an Empty file.
     * <p/>
     * TODO mönd de no d inode blöck no irgend wie flage das me erkennt obs es directory isch oder es file
     *
     * @param src  - either a VDirectory or a VFile
     * @param dest - destination
     * @return - create Inode in the VFS
     */
    public void create(VType src, VDirectory dest)
    {
        util.write(src.create());
        throw new NotImplementedException();
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

        Inode i = (Inode) cv.visit(src.getInode(), util);

        Directory dir = (Directory) dest.getInode();
        //dir.add(i);
        util.write(dir);
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
