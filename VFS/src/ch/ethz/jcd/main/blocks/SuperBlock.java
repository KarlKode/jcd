package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.utils.FileManager;

import java.io.IOException;

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

    public int getBlockCount() throws IOException
    {
        return fileManager.readInt(getBlockOffset(), OFFSET_BLOCK_COUNT);
    }

    public void setBlockCount(int blockCount) throws InvalidBlockCountException, IOException
    {
        // Check validity of block count
        // TODO: Block count > number of supported blocks
        if (blockCount < 0) {
            throw new InvalidBlockCountException();
        }

        fileManager.writeInt(getBlockOffset(), OFFSET_BLOCK_COUNT, blockCount);
    }

    public int getRootDirectoryBlock() throws IOException
    {
        return fileManager.readInt(getBlockOffset(), OFFSET_ROOT_DIRECTORY_BLOCK);
    }

    public void setRootDirectoryBlock(int rootDirectoryBlockAddress) throws InvalidBlockAddressException, IOException
    {
        // Check validity of root directory block address
        if (!isValidBlockAddress(rootDirectoryBlockAddress)) {
            throw new InvalidBlockAddressException();
        }

        fileManager.writeInt(getBlockOffset(), OFFSET_ROOT_DIRECTORY_BLOCK, rootDirectoryBlockAddress);
    }

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
}