package ethz.jcd.main;

import ethz.jcd.main.blocks.SuperBlock;
import ethz.jcd.main.layer.VType;
import ethz.jcd.main.visitor.CopyVisitor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileNotFoundException;
import java.util.LinkedList;

public class VDisk
{
    private VUtil util;

    private SuperBlock root = SuperBlock.getInstance();

    /**
     * Open an existing VDisk file that contains a valid VFS
     * @param vDiskFile path to the VDisk file
     */
    public VDisk( String vDiskFile ) throws FileNotFoundException {
        util = new VUtil(vDiskFile);
        // TODO
        throw new NotImplementedException();
    }

    /**
     * Create a new VDisk
     * @param vDiskFile path to the VDisk file
     * @param size total size of the VDisk
     */
    public VDisk( String vDiskFile, long size, long blockSize) throws FileNotFoundException {
        util = new VUtil(vDiskFile, size, blockSize);
        // TODO
        throw new NotImplementedException();
    }

    /**
     * This method creates either an EMPTY directory or an Empty file.
     *
     * TODO mönd de no d inode blöck no irgend wie flage das me erkennt obs es directory isch oder es file
     *
     * @param file - either a VDirectory or a VFile
     * @return - create Inode in the VFS
     */
    public void create( VType file )
    {
        util.write( file.create( ) );
        throw new NotImplementedException();
    }

    public void delete( VType file )
    {
        throw new NotImplementedException();
    }

    public void move( VType file )
    {
        throw new NotImplementedException();
    }

    public void list( VType file )
    {
        throw new NotImplementedException();
    }

    /**
     * This method copies either a directory or a file. Note that the whole
     * structure is copied. There are no optimization like "copy on write".
     *
     * @param file - either a VDirectory or a VFile
     */
    public void copy( VType file )
    {
        CopyVisitor cv = new CopyVisitor();

        cv.visit(file.getInode(), util);
    }

    public void store( VType file )
    {
        throw new NotImplementedException();
    }

    public void load( VType file )
    {
        throw new NotImplementedException();
    }

    public void stats( )
    {
        throw new NotImplementedException();
    }
}
