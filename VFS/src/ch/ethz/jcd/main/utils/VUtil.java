package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.BitMapBlock;
import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.blocks.SuperBlock;
import ch.ethz.jcd.main.exceptions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This class provides an interface to read and write on a VDisk.
 */
public class VUtil
{
    private RandomAccessFile raf;
    private SuperBlock superBlock;
    private BitMapBlock bitMapBlock;
    private DirectoryBlock rootBlock;

    /**
     * Create a new VUtil instance for the VFS in the file at vDiskFileName
     *
     * @param vDiskFileName name of the file that contains the VFS
     * @throws FileNotFoundException the VFS file can not be found
     */
    public VUtil(String vDiskFileName) throws FileNotFoundException
    {
        raf = new RandomAccessFile(vDiskFileName, "rwd");

        // Load super block
        byte[] superBlockBytes = new byte[SuperBlock.MIN_SUPER_BLOCK_SIZE];
        try
        {
            raf.read(superBlockBytes);
            // Read the whole super block
            superBlockBytes = new byte[new SuperBlock(superBlockBytes).getBlockSize()];
            raf.seek(SuperBlock.SUPER_BLOCK_ADDRESS);
            raf.read(superBlockBytes);
            superBlock = new SuperBlock(superBlockBytes);
        } catch (InvalidBlockSizeException | IOException e)
        {
            // This should never happen
            e.printStackTrace();
        }

        // Load bitmap block
        bitMapBlock = new BitMapBlock(read(superBlock.getFirstBitMapBlock()));
        rootBlock = new DirectoryBlock(read(superBlock.getRootDirectoryBlock()));
    }

    /**
     * Format the file at vDiskFileName so that it contains a valid empty VFS with the given parameters
     *
     * @param vDiskFileName the path of the file that should be formatted
     * @param diskSize      total size of the VDisk (in bytes).
     *                      has to be a multiple of blockSize and have space for at least 16 blocks (size >= blockSize * 16)
     * @param blockSize     block size of the new VFS
     * @throws InvalidBlockSizeException Invalid block size
     * @throws InvalidSizeException      Invalid disk size
     * @throws VDiskCreationException    Internal error while creating the VFS file
     */
    public static void format(String vDiskFileName, long diskSize, int blockSize) throws InvalidBlockSizeException, InvalidSizeException, VDiskCreationException
    {
        // Check block size
        if (blockSize < SuperBlock.MIN_SUPER_BLOCK_SIZE)
        {
            throw new InvalidBlockSizeException();
        }

        // Check disk size
        if (diskSize <= 0 || diskSize % blockSize != 0 || diskSize < 16 * blockSize)
        {
            throw new InvalidSizeException();
        }

        File file = new File(vDiskFileName);
        if (file.exists())
        {
            file.delete();
        }
        try
        {
            if (!file.createNewFile())
            {
                throw new VDiskCreationException();
            }
        } catch (IOException e)
        {
            throw new VDiskCreationException();
        }

        // Create the superblock of the VDisk
        ByteArray bytes = new ByteArray(new byte[blockSize]);
        bytes.putInt(SuperBlock.OFFSET_BLOCK_SIZE, blockSize);
        bytes.putInt(SuperBlock.OFFSET_BLOCK_COUNT, (int) (diskSize / blockSize));
        bytes.putInt(SuperBlock.OFFSET_ROOT_DIRECTORY_BLOCK, 2);
        SuperBlock superBlock = new SuperBlock(bytes.getBytes());
        BitMapBlock bitMapBlock;
        DirectoryBlock rootBlock;

        // Create the bit map and the root directory of the VDisk
        try
        {
            bitMapBlock = new BitMapBlock(superBlock.getFirstBitMapBlock(), new byte[blockSize]);
            rootBlock = new DirectoryBlock(new Block(superBlock.getRootDirectoryBlock(), new byte[blockSize]));

            // Set the SuperBlock, the BitMapBlock and the rootBlock as used
            bitMapBlock.setUsed(superBlock.getAddress());
            bitMapBlock.setUsed(bitMapBlock.getAddress());
            bitMapBlock.setUsed(rootBlock.getAddress());
        } catch (BlockAddressOutOfBoundException e)
        {
            // This should never happen
            throw new VDiskCreationException();
        }

        // Write formatted disk
        RandomAccessFile raf;
        try
        {
            raf = new RandomAccessFile(vDiskFileName, "rwd");
        } catch (FileNotFoundException e)
        {
            // This should never happen
            throw new VDiskCreationException();
        }

        write(raf, superBlock, superBlock);
        write(raf, superBlock, bitMapBlock);
        write(raf, superBlock, rootBlock);

        try
        {
            raf.close();
        } catch (IOException e)
        {
            // This should never happen
            throw new VDiskCreationException();
        }
    }

    /**
     * Write the Block to the VFS in file
     *
     * @param file  RandomAccessFile to access the VFS file
     * @param block Block to write
     */
    private static void write(RandomAccessFile file, SuperBlock superBlock, Block block)
    {
        try
        {
            file.seek(getBlockOffset(superBlock.getBlockSize(), block.getAddress()));
            file.write(block.getBytes());
        } catch (IOException e)
        {
            // TODO exception handling
            e.printStackTrace();
        }
    }

    /**
     * @param blockSize    block size of the VFS
     * @param blockAddress to compute the offset
     * @return offset to the given blockAddress in bytes
     */
    private static long getBlockOffset(long blockSize, int blockAddress)
    {
        return blockSize * ((long) blockAddress);
    }

    /**
     * Write the Block to disk
     *
     * @param block Block to permanently write to disk
     */
    public void write(Block block)
    {
        write(raf, superBlock, block);
    }

    /**
     * This method reads the Block to a given block address
     *
     * @param blockAddress to read at
     * @return read Block
     */
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

    /**
     * This method returns the super block of the loaded disks
     *
     * @return SuperBlock of loaded VDisk
     */
    public SuperBlock getSuperBlock()
    {
        return superBlock;
    }

    /**
     * This method returns the bit map block of the loaded disk
     *
     * @return BitMapBlock of loaded VDisk
     */
    public BitMapBlock getBitMapBlock()
    {
        return bitMapBlock;
    }

    /**
     * This method returns the root directory block of the loaded disk
     *
     * @return DirectoryBlock
     */
    public DirectoryBlock getRootDirectoryBlock()
    {
        return rootBlock;
    }

    /**
     * @param blockAddress to compute the offset
     * @return offset to the given blockAddress in bytes
     */
    private long getBlockOffset(int blockAddress)
    {
        return getBlockOffset(superBlock.getBlockSize(), blockAddress);
    }

    /**
     * Allocate a previously free block in the VFS
     *
     * @return Block instance that contains the data of the now used block
     * @throws DiskFullException
     */
    public Block allocate() throws DiskFullException
    {
        // Get the next free block and set it to used
        int freeBlockAddress;
        try
        {
            freeBlockAddress = bitMapBlock.allocateBlock();
        } catch (BlockAddressOutOfBoundException e)
        {
            throw new DiskFullException();
        }

        // Sync
        write(bitMapBlock);

        return new Block(freeBlockAddress, new byte[superBlock.getBlockSize()]);
    }

    /**
     * Set a previously used block as free in the VFS
     *
     * @param block Block instance that contains the data of the now free block
     */
    public void free(Block block)
    {
        try
        {
            bitMapBlock.setUnused(block.getAddress());
        } catch (BlockAddressOutOfBoundException e)
        {
            // This should never happen
            e.printStackTrace();
        }

        // Sync
        write(bitMapBlock);
    }
}
