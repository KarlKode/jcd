package ch.ethz.jcd.main.blocks;

/**
 * This class represents the general Block. It also holds the byte
 * structure that it could be easily written to or read from disk.
 */
public class Block
{
    protected int address;

    /**
     * Get the block address of the Block
     *
     * @return block address of the Block
     */
    public int getAddress()
    {
        return address;
    }

    /**
     * Set the block address of the Block
     *
     * @param address new block address of the block
     */
    public void setAddress(int address)
    {
        this.address = address;
    }
}
