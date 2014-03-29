package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;

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

    public boolean equals(Object other)
    {
        // The other has to be on the same VFS and have the same block address
        return other instanceof Block && !(((Block) other).fileManager != fileManager || ((Block) other).blockAddress != blockAddress);
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

    /**
     * Gets the offset of the block in the VFS file
     *
     * @return offset of block
     */
    protected long getBlockOffset()
    {
        return VUtil.getBlockOffset(blockAddress);
    }
}
