package ethz.jcd.main.blocks;

import ethz.jcd.main.Config;

public class SuperBlock extends Directory
{
    public static final int SUPER_BLOCK_SIZE = 64;
    public static int SUPER_BLOCK_ADDRESS = 0;

    private int blockSize;
    private int blockCount;
    private int rootDirectoryBlock;

    public SuperBlock(byte[] bytes)
    {
        super(bytes);

        address = SUPER_BLOCK_ADDRESS;
        blockSize = block.getInt(0);
        blockCount = block.getInt(4);
        rootDirectoryBlock = block.getInt(8);
    }

    public void setBlockSize(int blockSize)
    {
        this.blockSize = blockSize;
        block.putInt(0, this.blockSize);
    }

    /**
     * This method reads the SuperBlock buffer to determine the BlockSize
     * of the VFS
     *
     * @return size of Block in bytes
     */
    public int getBlockSize()
    {
        return blockSize;
    }

    public void setBlockCount(int blockCount)
    {
        this.blockCount = blockCount;
        block.putInt(4, this.blockSize);
    }

    /**
     * This method reads the SuperBlock buffer to determine the number of Blocks
     * allocated in the VFS
     *
     * @return number of allocated Blocks
     */
    public int getBlockCount()
    {
        return blockCount;
    }

    public int getRootDirectoryBlock()
    {
        return rootDirectoryBlock;
    }

    public int getFirstBitMapBlock()
    {
        return 1;
    }

    public int getLastBitMapBlock()
    {
        // TODO: Calculate the real size of the bit map
        return 1;
    }

    public int getFirstDataBlock()
    {
        return 2;
    }

    /**
     * This method returns the byte offset after the super block and
     * the freelist where the first block starts.
     * <p/>
     * eg. VFS_SUPER_BLOCk_SIZE = 1024 bytes,  blockCount = 1024, blocksize = 512
     * <p/>
     * => 1024 bits = 128 bytes for freelist needed
     * <p/>
     * since the freelist is stored in blocks,
     * <p/>
     * => ceil(128 bytes / blockSize) = blocksneeded = 1
     * <p/>
     * => offset = superblock + blocksneeded * blockSize = 1516 bytes
     *
     * @return byte offset of the blocks start point
     */
    public int startOfBlocks()
    {
        // TODO ev long oder double oder irgend was (possible int overflow)
        return Config.VFS_SUPER_BLOCK_SIZE + (int) Math.ceil(blockCount / (blockSize * 8)) * blockSize;
    }
}