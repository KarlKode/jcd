package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.BitMapBlock;
import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.blocks.SuperBlock;
import ch.ethz.jcd.main.exceptions.*;
import ch.ethz.jcd.main.layer.VDirectory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class VUtil
{
    public static final int BLOCK_SIZE = 1024;

    private final FileManager fileManager;
    private final SuperBlock superBlock;
    private BitMapBlock bitMapBlock;
    private DirectoryBlock rootBlock;
    private VDirectory rootDirectory;

    public VUtil(File vDiskFile) throws FileNotFoundException
    {

        fileManager = new FileManager(vDiskFile);

        // Load super block
        superBlock = new SuperBlock(fileManager, SuperBlock.SUPER_BLOCK_ADDRESS);

        // Load bitmap block
        try
        {
            bitMapBlock = new BitMapBlock(fileManager, superBlock.getFirstBitMapBlock());
        } catch (InvalidBlockAddressException e)
        {
            // This should never happen
            throw new InternalError();
        }

        // Load directory bloc
        try
        {
            rootBlock = new DirectoryBlock(fileManager, superBlock.getRootDirectoryBlock());
            rootDirectory = new VDirectory(rootBlock, null);
        } catch (IOException e)
        {
            throw new InternalError("Could not load root directory");
        }
    }

    public static long getBlockOffset(int blockAddress)
    {
        return ((long) blockAddress) * ((long) BLOCK_SIZE);
    }

    public static void format(File diskFile, long diskSize) throws InvalidSizeException, VDiskCreationException, IOException, InvalidBlockCountException, InvalidBlockAddressException
    {
        // Check disk size
        if (diskSize <= 0 || diskSize % BLOCK_SIZE != 0 || diskSize < 16 * BLOCK_SIZE)
        {
            throw new InvalidSizeException();
        }
        int blockCount = (int) (diskSize / BLOCK_SIZE);

        if (diskFile.exists())
        {
            diskFile.delete();
        }
        try
        {
            if (!diskFile.createNewFile())
            {
                throw new VDiskCreationException();
            }
        } catch (IOException e)
        {
            throw new VDiskCreationException();
        }

        // Allocate data
        RandomAccessFile file = new RandomAccessFile(diskFile, "rw");
        file.seek(diskSize - 1);
        file.write((byte) 0x00);
        file.close();

        VUtil vUtil = new VUtil(diskFile);

        // Initialize super block
        vUtil.getSuperBlock().setBlockCount(blockCount);
        vUtil.getSuperBlock().setRootDirectoryBlock(vUtil.getSuperBlock().getFirstDataBlock());

        // Initialize bitmap block
        vUtil.getBitMapBlock().initialize();

        // Initialize root directory
        vUtil.getRootDirectory().clear();

        vUtil.close();
    }

    public void close() throws IOException
    {
        fileManager.close();
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
     * Allocate a previously free block in the VFS
     *
     * @return Block instance that contains the data of the now used block
     * @throws DiskFullException
     */
    public Block allocateBlock() throws DiskFullException, IOException
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

        return new Block(fileManager, freeBlockAddress);
    }

    public DirectoryBlock allocateDirectoryBlock() throws DiskFullException, IOException, InvalidBlockAddressException
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

        return new DirectoryBlock(fileManager, freeBlockAddress);
    }

    /**
     * Set a previously used block as free in the VFS
     *
     * @param block Block instance that contains the data of the now free block
     */
    public void free(Block block) throws IOException
    {
        try
        {
            bitMapBlock.setUnused(block.getBlockAddress());
        } catch (BlockAddressOutOfBoundException e)
        {
            // This should never happen
            e.printStackTrace();
        }
    }

    public VDirectory getRootDirectory()
    {
        return rootDirectory;
    }
}
