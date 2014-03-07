package ethz.jcd.main;

import ethz.jcd.main.allocator.Allocator;
import ethz.jcd.main.blocks.Block;
import ethz.jcd.main.blocks.Inode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    private VFSHeader header;

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

        header = loadVFSHeader();

        allocator = new Allocator<T>( this.loadFreeList() );
    }

    public VFSHeader loadVFSHeader( )
    {
        byte[] header = new byte[Config.VFS_HEADER_LEN];

        try
        {
            raf.read(header);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return new VFSHeader(header);
    }

    public void storeVFSHeader( )
    {

    }

    public T loadFreeList( )
    {
        byte[] flags = new byte[Config.VFS_BLOCK_SIZE];

        try
        {
            raf.read(flags);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        for(int i = 0; i < Config.VFS_BLOCK_COUNT; i++)
        {

        }
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
