package ethz.jcd;

import ethz.jcd.allocator.Allocator;
import ethz.jcd.blocks.Block;
import ethz.jcd.blocks.Inode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * Created by phgamper on 3/6/14.
 */
public class VUtil<T extends List<Integer>>
{
    private RandomAccessFile raf;

    private File vdisk;

    private Allocator<T> allocator;

    public VUtil( )
    {
        vdisk = new File(Config.VFS_FILE_PATH);

        try
        {
            // TODO file erstelle wennsess nid git
            raf = new RandomAccessFile(vdisk, "rwd");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        allocator = new Allocator<T>( this.loadFreeList() );
    }

    public T loadFreeList( )
    {
        return null;
    }

    public void storeFreeList( T list )
    {

    }

    public Inode read( Integer blockAddress )
    {
        return null;
    }

    /**
     * This method writes a given Block in the VFS and returns the address
     * of the allocated Block
     *
     * @param i Block to store in the VFS
     * @return blockAddress
     */
    public Integer write( Block i )
    {
        //TODO do mitem allocater platz mache, denn ineschriebe, write passiert den entsprechend Block type

        return 0;
    }

    public void seek( Integer blockAddress )
    {

    }
}
