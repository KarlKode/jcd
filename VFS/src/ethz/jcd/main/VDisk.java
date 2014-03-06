package ethz.jcd.main;

import ethz.jcd.main.blocks.SuperBlock;
import ethz.jcd.main.layer.VType;
import ethz.jcd.main.visitor.CopyVisitor;
import java.util.LinkedList;

/**
 * Created by phgamper on 3/6/14.
 */
public class VDisk
{
    private VUtil<LinkedList<Integer>> util;

    private SuperBlock root = SuperBlock.getInstance();

    /**
     * Open an existing VDisk file that contains a valid VFS
     * @param vDiskFile path to the VDisk file
     */
    public VDisk( String vDiskFile )
    {
        util = new VUtil<LinkedList<Integer>>(vDiskFile);
        // TODO
    }

    /**
     * Create a new VDisk
     * @param vDiskFile path to the VDisk file
     * @param size total size of the VDisk
     */
    public VDisk( String vDiskFile, long size)
    {
        util = new VUtil<LinkedList<Integer>>(vDiskFile);
        // TODO
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
    }

    public void delete( VType file )
    {

    }

    public void move( VType file )
    {

    }

    public void list( VType file )
    {

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

    }

    public void load( VType file )
    {

    }

    public void stats( )
    {

    }
}
