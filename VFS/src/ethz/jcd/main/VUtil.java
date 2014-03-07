package ethz.jcd.main;

import ethz.jcd.main.allocator.Allocator;
import ethz.jcd.main.blocks.Block;
import ethz.jcd.main.blocks.Inode;
import ethz.jcd.main.exceptions.InvalidBlockSize;
import ethz.jcd.main.exceptions.InvalidSize;
import ethz.jcd.main.exceptions.VDiskCreationException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class VUtil
{
    private RandomAccessFile raf;
    private String vDiskFile;

    public VUtil( String vDiskFile ) throws FileNotFoundException {
        this.vDiskFile = vDiskFile;
        raf = new RandomAccessFile(vDiskFile, "rwd");

        throw new NotImplementedException();
    }

    public VUtil( String vDiskFile, long size, int blockSize ) throws InvalidSize, InvalidBlockSize, VDiskCreationException {
        this.vDiskFile = vDiskFile;

        // Check size and blockSize for validity
        if (size <= 0 || size % blockSize != 0) {
            throw new InvalidSize();
        }
        // TODO: check for minimum block size (to fit at least the superblock)
        if (blockSize <= 0) {
            throw new InvalidBlockSize();
        }

        File fp = new File(this.vDiskFile);
        try {
            if (!fp.createNewFile()) {
                throw new VDiskCreationException();
            }
        } catch (IOException e) {
            throw new VDiskCreationException();
        }

        throw new NotImplementedException();
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
