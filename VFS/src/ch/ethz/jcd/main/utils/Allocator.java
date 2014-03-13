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
     * Instantiate a new Allocator
     *
     * @param vUtil - VUtil of the VDisk the Allocator belongs to
     */
    public Allocator(VUtil vUtil)
    {
        this.vUtil = vUtil;
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
        } catch (BlockAddressOutOfBoundException e)
        {
            throw new DiskFullException();
        }

        Block newBlock = new Block(freeBlockAddress, new byte[vUtil.getSuperBlock().getBlockSize()]);

        // Sync
        vUtil.write(vUtil.getBitMapBlock());
        vUtil.write(newBlock);

        return newBlock;
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
            // TODO This should never happen!
            e.printStackTrace();
        }

        // Sync
        vUtil.write(vUtil.getBitMapBlock());
    }

    /**
     * Completely clear a VDisk and initialize it as empty
     */
    public void format()
    {
        vUtil.getBitMapBlock().clear();
        try
        {
            vUtil.getBitMapBlock().setUsed(SuperBlock.SUPER_BLOCK_ADDRESS);
            vUtil.getBitMapBlock().setUsed(SuperBlock.BIT_MAP_BLOCK_ADDRESS);
        } catch (BlockAddressOutOfBoundException e)
        {
            // This should never happen
            e.printStackTrace();
        }

        //TODO Root Directory Block Address
        vUtil.write(vUtil.getSuperBlock());
        vUtil.write(vUtil.getBitMapBlock());
    }
}
