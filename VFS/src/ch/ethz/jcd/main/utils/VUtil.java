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
    private final String vDiskFile;
    private RandomAccessFile raf;
    private SuperBlock superBlock;
    private BitMapBlock bitMapBlock;
    private DirectoryBlock rootBlock;

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
        rootBlock = loadRootDirectoryBlock();
    }

    /**
     * This constructor builds a new VUtil. Use it if you want to create a new
     * virtual disk.
     *
     * @param vDiskFile name of the disk to create
     * @param diskSize  of the disk to create
     * @param blockSize of the disk to create
     * @throws InvalidSizeException       if the given diskSize is invalid
     * @throws InvalidBlockSizeException  if the blockSize is invalid
     * @throws VDiskCreationException     if the disk file not could be created
     * @throws FileNotFoundException      if RandomAccessFile could not find the disk file
     * @throws InvalidBlockCountException if the blockSize does not fit into the diskSize
     */
    public VUtil(String vDiskFile, long diskSize, int blockSize) throws InvalidSizeException, InvalidBlockSizeException, VDiskCreationException, InvalidBlockCountException
    {
        this.vDiskFile = vDiskFile;
        // Check diskSize and blockSize for validity
        if (diskSize <= 0 || diskSize % blockSize != 0)
        {
            throw new InvalidSizeException();
        }

        if (blockSize < SuperBlock.MIN_SUPER_BLOCK_SIZE)
        {
            throw new InvalidBlockSizeException();
        }

        try
        {
            // Create the VDisk file
            File fp = new File(this.vDiskFile);
            if (!fp.createNewFile())
            {
                throw new VDiskCreationException();
            }
            raf = new RandomAccessFile(this.vDiskFile, "rw");
        } catch (IOException e)
        {
            throw new VDiskCreationException();
        }

        init(diskSize, blockSize);
    }

    /**
     * This method initializes the new created VDisk. A SuperBlock and a BitMapBlock
     * are created
     *
     * @param diskSize  of created VDisk
     * @param blockSize of created VDisk
     * @throws InvalidBlockSizeException  if the blockSize is invalid
     * @throws InvalidBlockCountException if the blockSize does not fit into the specified diskSize
     */
    private void init(long diskSize, int blockSize) throws InvalidBlockSizeException, InvalidBlockCountException
    {
        // Create the superblock of the VDisk
        ByteArray bytes = new ByteArray(new byte[blockSize]);
        bytes.putInt(SuperBlock.OFFSET_BLOCK_SIZE, blockSize);
        bytes.putInt(SuperBlock.OFFSET_BLOCK_COUNT, (int) (diskSize / blockSize));
        bytes.putInt(SuperBlock.OFFSET_ROOT_DIRECTORY_BLOCK, 2);
        superBlock = new SuperBlock(bytes.getBytes());

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
            e.printStackTrace();
        }
        //Sync
        write(superBlock);
        write(bitMapBlock);
        write(rootBlock);
    }

    /**
     * This method tries to load the SuperBlock stored on the first n bytes.
     *
     * @return loaded SuperBlock
     */
    private SuperBlock loadSuperBlock()
    {
        SuperBlock block = null;
        byte[] bytes = new byte[SuperBlock.MIN_SUPER_BLOCK_SIZE];

        try
        {
            raf.read(bytes);
            // Read the whole super block
            bytes = new byte[new SuperBlock(bytes).getBlockSize()];
            raf.seek(SuperBlock.SUPER_BLOCK_ADDRESS);
            raf.read(bytes);
            block = new SuperBlock(bytes);
        } catch (InvalidBlockSizeException | IOException e)
        {
            // This should never happen
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
        return new BitMapBlock(read(superBlock.getFirstBitMapBlock()));
    }

    /**
     * This method tries to load the BitMapBlock(s) stored after the SuperBlock.
     *
     * @return loaded BitMapBlock(s)
     */
    private DirectoryBlock loadRootDirectoryBlock()
    {
        return new DirectoryBlock(read(superBlock.getRootDirectoryBlock()));
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
        } catch (IOException e)
        {
            // TODO exception handling
            e.printStackTrace();
        }
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
        return ((long) blockAddress) * ((long) superBlock.getBlockSize());
    }
}
