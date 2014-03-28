package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.utils.FileManager;

/**
 * Basic block.
 */
public class Block
{
    protected final FileManager fileManager;
    protected int blockAddress;

    /**
     * @param fileManager  file manager instance
     * @param blockAddress block address of instance
     * @throws IllegalArgumentException if fileManager is null or block address is invalid
     */
    public Block(FileManager fileManager, int blockAddress) throws IllegalArgumentException
    {
        if (fileManager == null || isInvalidBlockAddress(blockAddress))
        {
            throw new IllegalArgumentException();
        }

        this.fileManager = fileManager;
        this.blockAddress = blockAddress;
    }


    /**
     * Gets block address of instance
     *
     * @return block address
     */
    public int getBlockAddress()
    {
        return blockAddress;
    }

    /**
     * Checks if the block address is invalid
     *
     * @param blockAddress block address to test
     * @return true if block address is invalid
     */
    protected boolean isInvalidBlockAddress(int blockAddress)
    {
        return blockAddress < 0;
    }
}
