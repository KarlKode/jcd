package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.exceptions.InvalidBlockSizeException;

/**
 * This class represents the SuperBlock. The SuperBlock contains all
 * information about block size, block count, root directory block,
 * BitMapBlocks, etc.
 */
public class SuperBlock extends Block
{
    public static final int MIN_SUPER_BLOCK_SIZE = 16;
    public static final int MIN_BLOCK_SIZE = InodeBlock.OFFSET_NAME + InodeBlock.MAX_NAME_SIZE;
    public static final int OFFSET_BLOCK_SIZE = 0;
    public static final int OFFSET_BLOCK_COUNT = 4;
    public static final int OFFSET_ROOT_DIRECTORY_BLOCK = 8;
    public static final int SUPER_BLOCK_ADDRESS = 0;
    public static final int BIT_MAP_BLOCK_ADDRESS = 1;

    private int blockSize;
    private int blockCount;
    private int rootDirectoryBlock;

    /**
     * Instantiate a new SuperBlock with the given content
     *
     * @param b the content of the new SuperBlock. Must not be null and bytes.size must be >= MIN_SUPER_BLOCK_SIZE
     * @throws InvalidBlockSizeException if bytes == null or bytes.size < MIN_SUPER_BLOCK_SIZE
     */
    public SuperBlock(byte[] b) throws InvalidBlockSizeException
    {
        super(SUPER_BLOCK_ADDRESS);

        if (b == null || b.length < SuperBlock.MIN_SUPER_BLOCK_SIZE)
        {
            throw new InvalidBlockSizeException();
        }

        this.setBytes(b);
    }

    /**
     * This method sets the ByteArray of the SuperBlock and then resets blockSize, blockCount
     * and rootDirectoryBlock
     *
     * @param b new content of the Block
     */
    @Override
    public void setBytes(byte[] b)
    {
        super.setBytes(b);
        blockSize = bytes.getInt(OFFSET_BLOCK_SIZE);
        blockCount = bytes.getInt(OFFSET_BLOCK_COUNT);
        rootDirectoryBlock = bytes.getInt(OFFSET_ROOT_DIRECTORY_BLOCK);
    }

    /**
     * Set the block size of the SuperBlock
     *
     * @param blockSize the new block size in bytes
     * @throws InvalidBlockSizeException if the new block size is invalid
     */
    public void setBlockSize(int blockSize) throws InvalidBlockSizeException
    {
        if (!isValidBlockSize(blockSize))
        {
            throw new InvalidBlockSizeException();
        }
        this.blockSize = blockSize;
        bytes.putInt(OFFSET_BLOCK_SIZE, this.blockSize);
    }

    /**
     * Get the block size of the SuperBlock
     *
     * @return block size of the SuperBlock in bytes
     */
    public int getBlockSize()
    {
        return blockSize;
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
     * Get the number of Blocks of the FS that the SuperBlock belongs to
     *
     * @return total number of Blocks of the FS that the SuperBlock belongs to
     */
    public int getBlockCount()
    {
        return blockCount;
    }

    /**
     * This method sets the root directory's blockAddress
     *
     * @param blockAddress to set
     */
    public void setRootDirectoryBlock(int blockAddress)
    {
        this.rootDirectoryBlock = blockAddress;
        bytes.putInt(OFFSET_ROOT_DIRECTORY_BLOCK, this.rootDirectoryBlock);
    }

    /**
     * Get the block address of the DirectoryBlock of the root directory
     *
     * @return block address of the DirectoryBlock of the root directory
     */
    public int getRootDirectoryBlock()
    {
        return rootDirectoryBlock;
    }

    /**
     * Get the block address of the first BitMapBlock of the FS the SuperBlock belongs to
     *
     * @return block address of the first BitMapBlock of the FS the SuperBlock belongs to
     */
    public int getFirstBitMapBlock()
    {
        return 1;
    }

    /**
     * Get the block address of the last BitMapBlock of the FS the SuperBlock belongs to
     *
     * @return block address of the last BitMapBlock of the FS the SuperBlock belongs to
     */
    public int getLastBitMapBlock()
    {
        // TODO: Calculate the real size of the bit map
        return 1;
    }

    /**
     * Get the block address of the first Block with real data of the FS the SuperBlock belongs to
     *
     * @return block address of the first Block with real data of the FS the SuperBlock belongs to
     */
    public int getFirstDataBlock()
    {
        return 2;
    }

    /**
     *
     * @param blockSize
     * @return whether the given blockSize is valid or not
     */
    private boolean isValidBlockSize(int blockSize)
    {
        return blockSize >= MIN_BLOCK_SIZE;
    }

    /**
     *
     * @param blockCount
     * @return whether the given blockSize is valid or not
     */
    private boolean isValidBlockCount(int blockCount)
    {
        return blockCount > 0;
    }
}