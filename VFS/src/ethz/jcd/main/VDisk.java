package ethz.jcd.main;

import ethz.jcd.main.blocks.Directory;
import ethz.jcd.main.blocks.Inode;
import ethz.jcd.main.blocks.SuperBlock;
import ethz.jcd.main.layer.VDirectory;
import ethz.jcd.main.layer.VType;
import ethz.jcd.main.visitor.CopyVisitor;
import java.util.LinkedList;

/**
 * Created by phgamper on 3/6/14.
 */
public class VDisk
{
    private VUtil<LinkedList<Integer>> util = new VUtil<LinkedList<Integer>> ();

    private SuperBlock root = SuperBlock.getInstance();

    /**
     * This method creates either an EMPTY directory or an Empty file.
     *
     * TODO mönd de no d inode blöck no irgend wie flage das me erkennt obs es directory isch oder es file
     *
     * @param src - either a VDirectory or a VFile
     * @param  dest - destination
     * @return - create Inode in the VFS
     */
    public void create( VType src, VDirectory dest )
    {
        //util.write( src.create( ) );
    }

    public void delete( VType file )
    {

    }

    public void move( VType src, VType dest )
    {

    }

    public void list( VDirectory file )
    {

    }

    /**
     * This method copies either a directory or a file. Note that the whole
     * structure is copied. There are no optimization like "copy on write".
     *
     * @param src - source, either a VDirectory or a VFile
     * @param  dest - destination
     */
    public void copy( VType src, VDirectory dest )
    {
        CopyVisitor cv = new CopyVisitor();

        Inode i = (Inode) cv.visit(src.getInode(), util);

        Directory dir = (Directory) dest.getInode();
        //dir.add(i);
        util.write(dir);
    }

    public void store( VType src, VType dest )
    {

    }

    public void load( VType file )
    {

    }

    public void stats( )
    {

    }
}
