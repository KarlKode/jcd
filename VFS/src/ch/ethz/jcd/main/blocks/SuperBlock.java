package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;

/**
 * This class represents the SuperBlock. The SuperBlock contains all
 * information about block size, block count, root directory block,
 * BitMapBlocks, etc.
 */
public class SuperBlock extends Block
{
    public static final int OFFSET_BLOCK_COUNT = 0;
    public static final int OFFSET_ROOT_DIRECTORY_BLOCK = 4;
    public static final int SUPER_BLOCK_ADDRESS = 0;
    public static final int BIT_MAP_BLOCK_ADDRESS = 1;
    public static final int DATA_BLOCK_BEGIN_ADDRESS = 2;

    public SuperBlock(FileManager fileManager, int blockAddress) throws InvalidBlockAddressException
    {
        super(fileManager, blockAddress);

        if (blockAddress != SUPER_BLOCK_ADDRESS) {
            throw new InvalidBlockAddressException();
        }
    }

    /**
     * Get the number of Blocks of the FS that the SuperBlock belongs to
     *
     * @return total number of Blocks of the FS that the SuperBlock belongs to
     */
    public int getBlockCount()
    {
        return fileManager.readInt(VUtil.getBlockOffset(blockAddress), OFFSET_BLOCK_COUNT);
    }

    /**
     * This method sets the number of Blocks the Disk contains
     *
     * @param blockCount to set
     * @throws InvalidBlockCountException if blockCount is invalid
     */
    public void setBlockCount(int blockCount) throws InvalidBlockCountException
    {
        if (!isValidBlockCount(blockCount))
        {
            throw new InvalidBlockCountException();
        }
        this.blockCount = blockCount;
        bytes.putInt(OFFSET_BLOCK_COUNT, this.blockCount);
    }

    /**
     * Get the block blockAddress of the DirectoryBlock of the root directory
     *
     * @return block blockAddress of the DirectoryBlock of the root directory
     */
    public int getRootDirectoryBlock()
    {
        return rootDirectoryBlock;
    }

    /**
     * This method sets the root directory's blockAddress
     *
     * @param blockAddress to set
     */
    public void setRootDirectoryBlock(int blockAddress) throws InvalidBlockAddressException
    {
        if (!isValidRootDirectoryBlock(blockAddress))
        {
            throw new InvalidBlockAddressException();
        }
        this.rootDirectoryBlock = blockAddress;
        bytes.putInt(OFFSET_ROOT_DIRECTORY_BLOCK, this.rootDirectoryBlock);
    }

    /**
     * Get the block blockAddress of the first BitMapBlock of the FS the SuperBlock belongs to
     *
     * @return block blockAddress of the first BitMapBlock of the FS the SuperBlock belongs to
     */
    public int getFirstBitMapBlock()
    {
        return BIT_MAP_BLOCK_ADDRESS;
    }

    /**
     * Get the block blockAddress of the last BitMapBlock of the FS the SuperBlock belongs to
     *
     * @return block blockAddress of the last BitMapBlock of the FS the SuperBlock belongs to
     */
    public int getLastBitMapBlock()
    {
        // TODO: Calculate the real size of the bit map
        return BIT_MAP_BLOCK_ADDRESS;
    }

    /**
     * Get the block blockAddress of the first Block with real data of the FS the SuperBlock belongs to
     *
     * @return block blockAddress of the first Block with real data of the FS the SuperBlock belongs to
     */
    public int getFirstDataBlock()
    {
        return DATA_BLOCK_BEGIN_ADDRESS;
    }

    /**
     * @param blockSize block size
     * @return true if the given block size is valid
     */
    private boolean isValidBlockSize(int blockSize)
    {
        return blockSize >= MIN_BLOCK_SIZE;
    }

    /**
     * @param blockCount block count
     * @return true if the given block size is valid or not
     */
    private boolean isValidBlockCount(int blockCount)
    {
        return blockCount > 0;
    }

    /**
     * @param blockAddress block blockAddress
     * @return if the the given root directory block blockAddress is valid
     */
    private boolean isValidRootDirectoryBlock(int blockAddress)
    {
        return blockAddress >= getFirstDataBlock();
    }
}