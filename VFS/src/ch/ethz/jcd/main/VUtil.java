package ch.ethz.jcd.main;

import ch.ethz.jcd.main.blocks.BitMapBlock;
import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.SuperBlock;
import ch.ethz.jcd.main.exceptions.InvalidBlockSize;
import ch.ethz.jcd.main.exceptions.InvalidSize;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class VUtil
{
    private final String vDiskFile;
    private RandomAccessFile raf;
    private SuperBlock superBlock;
    private BitMapBlock bitMapBlock;

    public VUtil(String vDiskFile) throws FileNotFoundException
    {
        this.vDiskFile = vDiskFile;
        raf = new RandomAccessFile(this.vDiskFile, "rwd");
        init();
    }

    public VUtil(String vDiskFile, long size, int blockSize) throws InvalidSize, InvalidBlockSize, VDiskCreationException
    {
        this.vDiskFile = vDiskFile;

        // Check size and blockSize for validity
        if (size <= 0 || size % blockSize != 0)
        {
            throw new InvalidSize();
        }
        // TODO: check for minimum block size (to fit at least the superblock)
        if (blockSize <= 0)
        {
            throw new InvalidBlockSize();
        }

        // Create the VDisk file
        File fp = new File(this.vDiskFile);
        try
        {
            if (!fp.createNewFile())
            {
                throw new VDiskCreationException();
            }
        } catch (IOException e)
        {
            throw new VDiskCreationException();
        }

        // Create the superblock of the VDisk
        SuperBlock newSuperBlock = new SuperBlock(new byte[blockSize]);
        newSuperBlock.setBlockSize(blockSize);
        newSuperBlock.setBlockCount((int) (size / blockSize));
        write(newSuperBlock);

        // Create the bit map of the VDisk
        BitMapBlock newBitMapBlock = new BitMapBlock(newSuperBlock.getFirstBitMapBlock(), new byte[superBlock.getBlockSize()]);

        // Set the superblock and the bitmap block as used
        newBitMapBlock.setUsed(0);
        newBitMapBlock.setUsed(1);
        write(newBitMapBlock);

        // Create the root directory block
        // TODO

        init();

        throw new NotImplementedException();
    }

    private void init()
    {
        superBlock = loadSuperBlock();
        bitMapBlock = loadBitMapBlock();
    }

    private SuperBlock loadSuperBlock()
    {
        // Read the whole super block
        byte[] bytes = new byte[SuperBlock.SUPER_BLOCK_SIZE];
        try
        {
            raf.read(bytes);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return new SuperBlock(bytes);
    }

    private BitMapBlock loadBitMapBlock()
    {
        int bitMapBlockAddress = superBlock.getFirstBitMapBlock();
        return new BitMapBlock(bitMapBlockAddress, read(bitMapBlockAddress).getBytes());
    }

    private Block allocateBlock()
    {
        // Get the next free block and set it to used
        int freeBlockAddress = bitMapBlock.getNextFreeBlockAddress();
        bitMapBlock.setUsed(freeBlockAddress);

        // Sync
        write(bitMapBlock);

        return new Block(freeBlockAddress);
    }

    private void freeBlock(Block block)
    {
        bitMapBlock.setFree(block.getAddress());

        // Sync
        write(bitMapBlock);
    }

    private long getBlockOffset(int blockAddress)
    {
        return ((long) blockAddress) * ((long) superBlock.getBlockSize());
    }

    /**
     * This method writes a given Block in the VFS and returns the address
     * of the allocated Block
     *
     * @param block Block to store in the VFS
     */
    public void write(Block block)
    {
        try
        {
            raf.seek(getBlockOffset(block.getAddress()));
            raf.write(block.getBytes());
        } catch (IOException e)
        {
            // TODO exception handling
            e.printStackTrace();
        }
    }

    public Block read(int blockAddress)
    {
        byte[] blockBytes = new byte[superBlock.getBlockSize()];
        try
        {
            raf.seek(getBlockOffset(blockAddress));
            raf.read(blockBytes);
        } catch (IOException e)
        {
            // TODO exception handling
            e.printStackTrace();
        }

        return new Block(blockAddress, blockBytes);
    }

    public SuperBlock getSuperBlock()
    {
        return superBlock;
    }
}
