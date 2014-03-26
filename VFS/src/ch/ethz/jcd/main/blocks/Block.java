package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.utils.FileManager;

/**
 * This class represents the general Block. It also holds the byte
 * structure that it could be easily written to or read from disk.
 */
public class Block
{
    protected final FileManager fileManager;
    protected int blockAddress;

    public Block(FileManager fileManager, int blockAddress) throws InvalidBlockAddressException
    {
        if (!isValidBlockAddress(blockAddress)) {
            throw new InvalidBlockAddressException();
        }

        this.fileManager = fileManager;
        this.blockAddress = blockAddress;
    }

    /**
     * Get the block blockAddress of the Block
     *
     * @return block blockAddress of the Block
     */
    public int getBlockAddress()
    {
        return blockAddress;
    }

    /**
     * Set the block blockAddress of the Block
     *
     * @param blockAddress new block blockAddress of the block
     */
    public void setBlockAddress(int blockAddress)
    {
        this.blockAddress = blockAddress;
    }

    protected boolean isValidBlockAddress(int blockAddress) {
        return blockAddress >= 0;
    }
}
