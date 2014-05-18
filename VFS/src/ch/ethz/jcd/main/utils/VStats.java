package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.BitMapBlock;
import ch.ethz.jcd.main.blocks.SuperBlock;

import java.io.IOException;

/**
 * Ths class provides some methods to query statistical information about the
 * virtual file system.
 */
public class VStats
{
    private SuperBlock superBlock;
    private BitMapBlock bitMapBlock;

    public VStats(VUtil vUtil)
    {
        this.superBlock = vUtil.getSuperBlock();
        this.bitMapBlock = vUtil.getBitMapBlock();
    }

    /**
     * @return the disk size
     * @throws IOException
     */
    public int diskSize()
            throws IOException
    {
        return VUtil.BLOCK_SIZE * superBlock.getBlockCount();
    }

    /**
     * @return the amount of free space available on disk
     */
    public long freeSpace()
            throws IOException
    {
        return freeBlocks() * VUtil.BLOCK_SIZE;
    }

    /**
     * @return the amount of used space on disk
     */
    public long usedSpace()
    {
        return usedBlocks() * VUtil.BLOCK_SIZE;
    }

    /**
     * @return the number of free blocks available on disk
     */
    public int freeBlocks()
            throws IOException
    {
        return diskSize() / VUtil.BLOCK_SIZE - usedBlocks();
    }

    /**
     * @return the number of used blocks on disk
     */
    public int usedBlocks()
    {
        return bitMapBlock.getUsedBlocks();
    }
}