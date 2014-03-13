package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.BitMapBlock;
import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.SuperBlock;
import ch.ethz.jcd.main.exceptions.*;

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

    public VUtil(String vDiskFile, long diskSize, int blockSize) throws InvalidSizeException, InvalidBlockSizeException, VDiskCreationException, FileNotFoundException, InvalidBlockCountException
    {
        this.vDiskFile = vDiskFile;
        // Check diskSize and blockSize for validity
        if (diskSize <= 0 || diskSize % blockSize != 0)
        {
            throw new InvalidSizeException();
        }

        // Create the VDisk file
        File fp = new File(this.vDiskFile);
        try
        {
            if (!fp.createNewFile())
            {
                throw new VDiskCreationException();
            }
        }
        catch (IOException e)
        {
            throw new VDiskCreationException();
        }

        raf = new RandomAccessFile(this.vDiskFile, "rwd");

        init(diskSize, blockSize);

        //throw new NotImplementedException();
    }

    private void init(long diskSize, int blockSize) throws InvalidBlockSizeException, InvalidBlockCountException
    {
        // Create the superblock of the VDisk
        SuperBlock newSuperBlock = new SuperBlock(new byte[blockSize]);
        newSuperBlock.setBlockSize(blockSize);
        newSuperBlock.setBlockCount((int) (diskSize / blockSize));
        superBlock = newSuperBlock;
        write(newSuperBlock);

        // Create the bit map of the VDisk
        int bitMapBlockAddress = newSuperBlock.getFirstBitMapBlock();
        BitMapBlock newBitMapBlock = new BitMapBlock(bitMapBlockAddress, new byte[superBlock.getBlockSize()]);

        // Set the superblock and the bitmap block as used
        try
        {
            newBitMapBlock.setUsed(SuperBlock.SUPER_BLOCK_ADDRESS);
            newBitMapBlock.setUsed(bitMapBlockAddress);
        } catch (BlockAddressOutOfBoundException e)
        {
            // This should never happen
            e.printStackTrace();
        }
        write(newBitMapBlock);

        // TODO Create the root directory block

        init( );
    }

    private void init()
    {
        superBlock = loadSuperBlock();
        bitMapBlock = loadBitMapBlock();
    }

    private SuperBlock loadSuperBlock()
    {
        byte[] bytes = new byte[SuperBlock.MIN_SUPER_BLOCK_SIZE];
        try
        {
            raf.read(bytes);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        SuperBlock block = null;
        try
        {
            block = new SuperBlock(bytes);
        } catch (InvalidBlockSizeException e)
        {
            // This should never happen
            e.printStackTrace();
        }

        // Read the whole super block
        bytes = new byte[block.getBlockSize()];
        try
        {
            raf.read(bytes);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return block;
    }

    private BitMapBlock loadBitMapBlock()
    {
        int bitMapBlockAddress = superBlock.getFirstBitMapBlock();
        return new BitMapBlock(bitMapBlockAddress, read(bitMapBlockAddress).getBytes());
    }

    /**
     * This method writes a given Block in the VFS
     *
     * @param block Block to store in the VFS
     */
    public void write(Block block)
    {
        block.commit();

        try
        {
            raf.seek(getBlockOffset(block.getAddress()));
            raf.write(block.getBytes());
        }
        catch (IOException e)
        {
            // TODO exception handling
            e.printStackTrace();
        }
    }

    /**
     * This method reads the Block to a given block address
     *
     * @param blockAddress
     * @return read Block
     */
    public Block read(int blockAddress)
    {
        byte[] blockBytes = new byte[superBlock.getBlockSize()];
        try
        {
            raf.seek(getBlockOffset(blockAddress));
            raf.read(blockBytes);
        }
        catch (IOException e)
        {
            // TODO exception handling
            e.printStackTrace();
        }

        return new Block(blockAddress, blockBytes);
    }

    /**
     *
     * @return SuperBlock of loaded VDisk
     */
    public SuperBlock getSuperBlock()
    {
        return superBlock;
    }

    /**
     *
     * @return BitMapBlock of loaded VDisk
     */
    public BitMapBlock getBitMapBlock( ) { return bitMapBlock; }

    /**
     *
     * @param blockAddress
     * @return offset to the given blockAddress in bytes
     */
    private long getBlockOffset(int blockAddress)
    {
        return ((long) blockAddress) * ((long) superBlock.getBlockSize());
    }
}
