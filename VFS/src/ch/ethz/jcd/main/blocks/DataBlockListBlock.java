package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.IOException;

public class DataBlockListBlock extends Block
{
    public static final int SIZE_USED_BLOCKS = 4;
    public static final int SIZE_ENTRY = 4;
    public static final int OFFSET_USED_BLOCKS = 0;
    public static final int OFFSET_BLOCK_LIST = OFFSET_USED_BLOCKS + SIZE_USED_BLOCKS;

    /**
     * @param fileManager  file manager instance
     * @param blockAddress block address of instance
     * @throws IllegalArgumentException if fileManager is null or block address is invalid
     */
    public DataBlockListBlock(FileManager fileManager, int blockAddress) throws IllegalArgumentException, IOException
    {
        super(fileManager, blockAddress);
    }

    public static int getCapacity()
    {
        return (VUtil.BLOCK_SIZE - OFFSET_BLOCK_LIST) / SIZE_ENTRY;
    }

    public int getUsedBlocks() throws IOException
    {
        return fileManager.readInt(getBlockOffset(), OFFSET_USED_BLOCKS);
    }

    public void setUsedBlocks(int usedBlocks) throws IOException
    {
        fileManager.writeInt(getBlockOffset(), OFFSET_USED_BLOCKS, usedBlocks);
    }

    public int getDataBlockAddress(int index) throws IOException
    {
        if (index < 0 || index >= getCapacity())
        {
            throw new IllegalArgumentException();
        }
        return fileManager.readInt(getBlockOffset(), OFFSET_BLOCK_LIST + SIZE_ENTRY * index);
    }

    public void setDataBlockAddress(int index, int blockAddress) throws IOException
    {
        if (index < 0 || index >= getCapacity())
        {
            throw new IllegalArgumentException();
        }
        fileManager.writeInt(getBlockOffset(), OFFSET_BLOCK_LIST + SIZE_ENTRY * index, blockAddress);
    }
}
