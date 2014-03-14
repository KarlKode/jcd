package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockAddressOutOfBoundException;

import java.util.BitSet;

public class BitMapBlock extends Block
{
    private BitSet bitMap;
    private int usedBlocks;

    /**
     * Instantiate a new BitMapBlock
     *
     * @param blockAddress block address of the new BitMapBlock
     * @param bytes        content of the new BitMapBlock
     */
    public BitMapBlock(int blockAddress, byte[] bytes)
    {
        super(blockAddress, bytes);
        bitMap = BitSet.valueOf(bytes);

        usedBlocks = 0;
        for (int i = bitMap.nextSetBit(0); i >= 0; i = bitMap.nextSetBit(i + 1))
        {
            usedBlocks++;
        }
    }

    /**
     * Allocate a new Block out of the unused Blocks this BitMapBlock controls
     *
     * @return block address of the newly allocated Block
     */
    public int allocateBlock() throws BlockAddressOutOfBoundException
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
    public void setUsed(int blockAddress) throws BlockAddressOutOfBoundException
    {
        if (!isValidBlockAddress(blockAddress))
        {
            throw new BlockAddressOutOfBoundException();
        }

        if (isUnused(blockAddress))
        {
            bitMap.set(blockAddress);
            bytes.setBytes(bitMap.toByteArray());
            usedBlocks++;
        }
    }

    /**
     * Set a Block as unused
     *
     * @param blockAddress block address of the Block that should be set as unused
     */
    public void setUnused(int blockAddress) throws BlockAddressOutOfBoundException
    {
        if (!isValidBlockAddress(blockAddress))
        {
            throw new BlockAddressOutOfBoundException();
        }

        if (!isUnused(blockAddress))
        {
            bitMap.clear(blockAddress);
            bytes.setBytes(bitMap.toByteArray());
            usedBlocks--;
        }
    }

    /**
     * Set all Blocks as unused
     */
    public void clear()
    {
        bitMap.clear();
        bytes.setBytes(bitMap.toByteArray());
        usedBlocks = 0;
    }

    /**
     * Check if a Block is unused
     *
     * @param blockAddress block address of the Block that should be checked
     * @return true if the Block is not used
     */
    public boolean isUnused(int blockAddress) throws BlockAddressOutOfBoundException
    {
        if (!isValidBlockAddress(blockAddress))
        {
            throw new BlockAddressOutOfBoundException();
        }
        return !bitMap.get(blockAddress);
    }

    /**
     * Get the capacity of the BitMapBlock
     *
     * @return maximum block address the BitMapBlock can store
     */
    public int capacity()
    {
        return bytes.size() * 8;
    }

    /**
     * Get the number of used Block in this BitMapBlock
     *
     * @return the number of used Blocks
     */
    public int getUsedBlocks()
    {
        return usedBlocks;
    }

    private boolean isValidBlockAddress(int blockAddress)
    {
        return blockAddress < capacity();
    }
}
