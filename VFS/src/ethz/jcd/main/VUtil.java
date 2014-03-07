package ethz.jcd.main;

import ethz.jcd.main.allocator.Allocator;
import ethz.jcd.main.blocks.Block;
import ethz.jcd.main.blocks.Inode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.List;

public class VUtil
{
    private RandomAccessFile raf;
    private String vDiskFile;

    public VUtil( String vDiskFile ) throws FileNotFoundException {
        this.vDiskFile = vDiskFile;
        File fp = new File(this.vDiskFile);

        raf = new RandomAccessFile(fp, "rwd");

        throw new NotImplementedException();
    }

    public VUtil( String vDiskFile, long size, long blockSize ) throws FileNotFoundException {
        // TODO Create VDisk file

        this(vDiskFile);
    }

    /**
     * This method writes a given Block in the VFS and returns the address
     * of the allocated Block
     *
     * @param block Block to store in the VFS
     * @return blockAddress
     */
    public Integer write( Block block )
    {
        //TODO do mitem allocater platz mache, denn ineschriebe, write passiert den entsprechend Block type

        throw new NotImplementedException();
    }

    public Block read( Integer blockAddress )
    {
        throw new NotImplementedException();
    }
}
