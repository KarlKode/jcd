package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockAddressOutOfBoundException;
import ch.ethz.jcd.main.visitor.BlockVisitor;

import java.util.BitSet;

public class BitMapBlock extends Block
{
    private BitSet bitMap;
    private int usedBlocks;

    /**
     * Instantiate a new BitMapBlock
     *
     * @param blockAddress block address of the new BitMapBlock
     * @param b        content of the new BitMapBlock
     */
    public BitMapBlock(int blockAddress, byte[] b)
    {
        super(blockAddress, b);
        bitMap = BitSet.valueOf(b);

        usedBlocks = 0;
        for (int i = bitMap.nextSetBit(0); i >= 0; i = bitMap.nextSetBit(i + 1))
        {
            usedBlocks++;
        }
        bytes.put(0, bitMap.toByteArray());
    }

    /**
     * Instantiate a new BitMapBlock
     *
     * @param block BitMapBlock read as Block
     */
    public BitMapBlock(Block block)
    {
        super(block.getAddress(), block.getBytes());
        bitMap = BitSet.valueOf(block.getBytes());

        usedBlocks = 0;
        for (int i = bitMap.nextSetBit(0); i >= 0; i = bitMap.nextSetBit(i + 1))
        {
            usedBlocks++;
        }
        bytes.put(0, bitMap.toByteArray());
    }

    /**
     * TODO describe
     * @param visitor
     * @param arg
     * @param <R>
     * @param <A>
     * @return
     */
    public <R, A> R accept(BlockVisitor<R, A> visitor, A arg)
    {
        return visitor.bitMapBlock(this, arg);
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
            bytes.put(0, bitMap.toByteArray());
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
            bytes.put(0, bitMap.toByteArray());
            usedBlocks--;
        }
    }

    /**
     * Set all Blocks as unused
     */
    public void clear()
    {
        bitMap.clear();
        bytes.setBytes(new byte[bitMap.size()]);
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
