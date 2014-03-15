package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.BitMapBlock;
import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.SuperBlock;
import ch.ethz.jcd.main.exceptions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This class provides an interface to read and write on a VDisk.
 *
 */
public class VUtil
{
    private final String vDiskFile;
    private RandomAccessFile raf;
    private SuperBlock superBlock;
    private BitMapBlock bitMapBlock;

    /**
     * This constructor builds a new VUtil. Use it if you want to mount an
     * existing virtual disk.
     *
     * @param vDiskFile name of disk to mount
     * @throws FileNotFoundException if RandomAccessFile could not find the disk file
     */
    public VUtil(String vDiskFile) throws FileNotFoundException
    {
        this.vDiskFile = vDiskFile;
        raf = new RandomAccessFile(this.vDiskFile, "rwd");
        superBlock = loadSuperBlock();
        bitMapBlock = loadBitMapBlock();
    }

    /**
     * This constructor builds a new VUtil. Use it if you want to create a new
     * virtual disk.
     *
     * @param vDiskFile name of the disk to create
     * @param diskSize of the disk to create
     * @param blockSize of the disk to create
     * @throws InvalidSizeException if the given diskSize is invalid
     * @throws InvalidBlockSizeException if the blockSize is invalid
     * @throws VDiskCreationException if the disk file not could be created
     * @throws FileNotFoundException if RandomAccessFile could not find the disk file
     * @throws InvalidBlockCountException if the blockSize does not fit into the diskSize
     */
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

        raf = new RandomAccessFile(this.vDiskFile, "rw");

        init(diskSize, blockSize);

        //throw new NotImplementedException();
    }

    /**
     * This method initializes the new created VDisk. A SuperBlock and a BitMapBlock
     * are created
     *
     * @param diskSize of created VDisk
     * @param blockSize of created VDisk
     * @throws InvalidBlockSizeException if the blockSize is invalid
     * @throws InvalidBlockCountException if the blockSize does not fit into the specified diskSize
     */
    private void init(long diskSize, int blockSize) throws InvalidBlockSizeException, InvalidBlockCountException
    {
        // Create the superblock of the VDisk
        superBlock = new SuperBlock(new byte[blockSize]);
        superBlock.setBlockSize(blockSize);
        superBlock.setBlockCount((int) (diskSize / blockSize));

        // Create the bit map of the VDisk
        try
        {
            bitMapBlock = new BitMapBlock(superBlock.getFirstBitMapBlock(), new byte[superBlock.getBlockSize()]);
            // Set the SuperBlock and the BitMapBlock as used
            bitMapBlock.setUsed(SuperBlock.SUPER_BLOCK_ADDRESS);
            bitMapBlock.setUsed(superBlock.getFirstBitMapBlock());
        }
        catch (BlockAddressOutOfBoundException e)
        {
            // This should never happen
            e.printStackTrace();
        }

        //Sync
        write(superBlock);
        write(bitMapBlock);

        // TODO Create the root directory block
    }

    /**
     * This method tries to load the SuperBlock stored on the first n bytes.
     *
     * @return loaded SuperBlock
     */
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
        }
        catch (InvalidBlockSizeException e)
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

    /**
     * This method tries to load the BitMapBlock(s) stored after the SuperBlock.
     *
     * @return loaded BitMapBlock(s)
     */
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
