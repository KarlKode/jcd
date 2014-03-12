package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.BitMapBlock;
import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.SuperBlock;
import ch.ethz.jcd.main.exceptions.BlockAddressOutOfBoundException;
import ch.ethz.jcd.main.exceptions.DiskFullException;

/**
 * The Allocator provides an interface to allocate and free Blocks to
 * make sure that no where else BitMapBlock changes are needed to perform.
 * It keeps also the BitMapBlock in sync.
 * <p/>
 * Created by phgamper on 3/12/14.
 */
public class Allocator
{
    private BitMapBlock bitMapBlock;
    private VUtil vUtil;

    /**
     * This constructor creates a new Allocator.
     *
     * @param vUtil - VUtil used to keep BitMapBlock in sync with the VDisk
     *              must be initialized correctly
     */
    public Allocator(VUtil vUtil)
    {
        this.vUtil = vUtil;
        this.bitMapBlock = vUtil.getBitMapBlock();
    }

    /**
     * This method allocates a Block
     *
     * @return the allocated Block
     * @throws DiskFullException if no unused Block is available
     */
    public Block allocate() throws DiskFullException
    {
        // Get the next free block and set it to used
        int freeBlockAddress = 0;
        try
        {
            freeBlockAddress = bitMapBlock.allocateBlock();
        } catch (BlockAddressOutOfBoundException e)
        {
            throw new DiskFullException();
        }

        // Sync
        vUtil.write(bitMapBlock);
        return new Block(freeBlockAddress);
    }

    /**
     * This method frees a Block
     *
     * @param block
     */
    public void free(Block block)
    {
        try
        {
            bitMapBlock.setUnused(block.getAddress());
        } catch (BlockAddressOutOfBoundException e)
        {
            // TODO This should never happen!
            e.printStackTrace();
        }
        // Sync
        vUtil.write(bitMapBlock);
    }

    /**
     * This method checks whether the block address of given Block is used or not
     *
     * @param block - Block containing at least the block address
     * @return True if given Block is free
     */
    public boolean isFree(Block block)
    {
        try
        {
            return bitMapBlock.isUnused(block.getAddress());
        } catch (BlockAddressOutOfBoundException e)
        {
            // This should never happen
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method clears and initialize the BitMapBlock.
     */
    public void format()
    {
        bitMapBlock.clear();
        try
        {
            bitMapBlock.setUsed(SuperBlock.SUPER_BLOCK_ADDRESS);
            bitMapBlock.setUsed(SuperBlock.BIT_MAP_BLOCK_ADDRESS);
        } catch (BlockAddressOutOfBoundException e)
        {
            // This should never happen
            e.printStackTrace();
        }

        //TODO Root Directory Block Address
        vUtil.write(bitMapBlock);
    }
}
