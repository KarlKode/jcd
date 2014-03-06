package ethz.jcd.main;

import ethz.jcd.main.allocator.Allocator;
import ethz.jcd.main.blocks.Block;
import ethz.jcd.main.blocks.Inode;

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
    private String vDiskFile;

    public VUtil( String vDiskFile )
    {
        this.vDiskFile = vDiskFile;
        vdisk = new File(this.vDiskFile);

        try
        {
            raf = new RandomAccessFile(vdisk, "rwd");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        allocator = new Allocator<T>( this.loadFreeList() );
    }

    public VUtil( String vDiskFile, long size, long blockSize )
    {
        // TODO Create VDisk file

        this(vDiskFile);
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
