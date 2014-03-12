package ch.ethz.jcd.main.blocks;

import java.util.BitSet;

public class BitMapBlock extends Block
{
    private BitSet bitMap;

    /**
     * Instantiate a new BitMapBlock
     *
     * @param blockAddress block address of the new BitMapBlock
     * @param bytes        content of the new BitMapBlock
     */
    public BitMapBlock(int blockAddress, byte[] bytes)
    {
        super(blockAddress, bytes);
        bitMap = BitSet.valueOf(block);
    }

    /**
     * Allocate a new Block out of the unused Blocks this BitMapBlock controls
     *
     * @return block address of the newly allocated Block
     */
    public int allocateBlock()
    {
        int newBlock = bitMap.nextClearBit(0);
        setUsed(newBlock);
        return newBlock;
    }

    /**
     * Set a Block as used
     *
     * @param blockAddress block address of the Block that should be set as used
     */
    public void setUsed(int blockAddress)
    {
        bitMap.set(blockAddress);
        bytes = bitMap.toByteArray();
    }

    /**
     * Set a Block as unused
     *
     * @param blockAddress block address of the Block that should be set as unused
     */
    public void setUnused(int blockAddress)
    {
        bitMap.clear(blockAddress);
        bytes = bitMap.toByteArray();
    }

    /**
     * Set all Blocks as unused
     */
    public void clear()
    {
        bitMap.clear();
        bytes = bitMap.toByteArray();
    }

    /**
     * Check if a Block is unused
     *
     * @param blockAddress block address of the Block that should be checked
     * @return true if the Block is not used
     */
    public boolean isUnused(int blockAddress)
    {
        return !bitMap.get(blockAddress);
    }
}
