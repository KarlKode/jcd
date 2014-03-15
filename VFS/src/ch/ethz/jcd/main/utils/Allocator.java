package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.SuperBlock;
import ch.ethz.jcd.main.exceptions.BlockAddressOutOfBoundException;
import ch.ethz.jcd.main.exceptions.DiskFullException;

/**
 * The Allocator provides an interface to allocate and free Blocks to
 * make sure that no where else BitMapBlock changes are needed to perform.
 * It keeps also the BitMapBlock in sync.
 */
public class Allocator
{
    private VUtil vUtil;

    /**
     * Instantiate a new Allocator. The SuperBlock stored in the VUtil contains
     * knows about the first block that could be allocated. The ones before are
     * reserved.
     *
     * @param vUtil - VUtil of the VDisk the Allocator belongs to
     */
    public Allocator(VUtil vUtil)
    {
        this.vUtil = vUtil;
        this.reserve();
    }

    /**
     * This method reserves the first n Blocks according to the SuperBlock
     */
    private void reserve()
    {
        int len = vUtil.getSuperBlock().getFirstDataBlock();
        for(int i = 0; i < len; i++)
        {
            try
            {
                vUtil.getBitMapBlock().setUsed(i);
            }
            catch (BlockAddressOutOfBoundException e)
            {
                //TODO should never happen since the Disk is at least of the header size
                e.printStackTrace();
            }
        }
        // Sync
        vUtil.write(vUtil.getBitMapBlock());
    }

    /**
     * Allocate a new Block
     *
     * @return the allocated Block
     * @throws DiskFullException if no more unused Blocks are available
     */
    public Block allocate() throws DiskFullException
    {
        // Get the next free block and set it to used
        int freeBlockAddress;
        try
        {
            freeBlockAddress = vUtil.getBitMapBlock().allocateBlock();
        }
        catch (BlockAddressOutOfBoundException e)
        {
            throw new DiskFullException();
        }

        // Sync
        vUtil.write(vUtil.getBitMapBlock());

        return new Block(freeBlockAddress, new byte[vUtil.getSuperBlock().getBlockSize()]);
    }

    /**
     * Free a Block
     *
     * @param block Block to be freed
     */
    public void free(Block block)
    {
        try
        {
            vUtil.getBitMapBlock().setUnused(block.getAddress());
        } catch (BlockAddressOutOfBoundException e)
        {
            // This should never happen
            e.printStackTrace();
        }

        // Sync
        vUtil.write(vUtil.getBitMapBlock());
    }

    /**
     * Check if a Block is unused
     *
     * @return true if block is free, false otherwise
     */
    public boolean isFree(Block block)
    {
        try
        {
            return vUtil.getBitMapBlock().isUnused(block.getAddress());
        }
        catch (BlockAddressOutOfBoundException e)
        {
            // This should never happen
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Completely clear a VDisk and initialize it as empty
     */
    public void format()
    {
        vUtil.getBitMapBlock().clear();
        this.reserve();
        // Sync
        vUtil.write(vUtil.getBitMapBlock());
    }

    /**
     * Get the number of used Block in this BitMapBlock
     *
     * @return the number of used Blocks
     */
    public int getUsedBlocks()
    {
        return vUtil.getBitMapBlock().getUsedBlocks();
    }
}
