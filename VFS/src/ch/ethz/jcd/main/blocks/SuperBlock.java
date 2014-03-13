package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.exceptions.InvalidBlockSizeException;

public class SuperBlock extends DirectoryBlock
{
    public static final int MIN_SUPER_BLOCK_SIZE = 16;
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
     * @param bytes the content of the new SuperBlock. Must not be null and bytes.length must be >= MIN_SUPER_BLOCK_SIZE
     * @throws InvalidBlockSizeException if bytes == null or bytes.length < MIN_SUPER_BLOCK_SIZE
     */
    public SuperBlock(byte[] bytes) throws InvalidBlockSizeException
    {
        super(bytes);

        if (bytes == null || bytes.length < SuperBlock.MIN_SUPER_BLOCK_SIZE)
        {
            throw new InvalidBlockSizeException();
        }

        address = SUPER_BLOCK_ADDRESS;
        blockSize = block.getInt(OFFSET_BLOCK_SIZE);
        blockCount = block.getInt(OFFSET_BLOCK_COUNT);
        rootDirectoryBlock = block.getInt(OFFSET_ROOT_DIRECTORY_BLOCK);
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
        block.putInt(OFFSET_BLOCK_SIZE, this.blockSize);
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

    public void setBlockCount(int blockCount) throws InvalidBlockCountException
    {
        if (!isValidBlockCount(blockCount))
        {
            throw new InvalidBlockCountException();
        }
        this.blockCount = blockCount;
        block.putInt(OFFSET_BLOCK_COUNT, this.blockCount);
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
     * Get the block address of the DirectoryBlock of the root directory
     * @return block address of the DirectoryBlock of the root directory
     */
    public int getRootDirectoryBlock()
    {
        return rootDirectoryBlock;
    }

    /**
     * Get the block address of the first BitMapBlock of the FS the SuperBlock belongs to
     * @return block address of the first BitMapBlock of the FS the SuperBlock belongs to
     */
    public int getFirstBitMapBlock()
    {
        return 1;
    }

    /**
     * Get the block address of the last BitMapBlock of the FS the SuperBlock belongs to
     * @return block address of the last BitMapBlock of the FS the SuperBlock belongs to
     */
    public int getLastBitMapBlock()
    {
        // TODO: Calculate the real size of the bit map
        return 1;
    }

    /**
     * Get the block address of the first Block with real data of the FS the SuperBlock belongs to
     * @return block address of the first Block with real data of the FS the SuperBlock belongs to
     */
    public int getFirstDataBlock()
    {
        return 2;
    }

    private boolean isValidBlockSize(int blockSize)
    {
        return blockSize >= SuperBlock.MIN_SUPER_BLOCK_SIZE;
    }

    private boolean isValidBlockCount(int blockCount)
    {
        return blockCount > 0;
    }
}