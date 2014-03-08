package ethz.jcd.main.blocks;

import ethz.jcd.main.Config;

public class SuperBlock extends Directory
{
    public static int SUPER_BLOCK_ADDRESS = 0;

    private static SuperBlock instance;

    private int blockSize;

    private int blockCount;

    public static SuperBlock getInstance(byte[] bytes)
    {
        if (instance == null)
        {
            instance = new SuperBlock(bytes);
        }

        return instance;
    }

    public static SuperBlock getInstance()
    {
        if (instance == null)
        {
            instance = new SuperBlock();
        }

        return instance;
    }

    private SuperBlock()
    {
        address = SUPER_BLOCK_ADDRESS;
    }

    private SuperBlock(byte[] bytes)
    {
        super(bytes);

        blockSize = block.getInt(0);

        blockCount = block.getInt(4);

        address = SUPER_BLOCK_ADDRESS;
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

    /**
     * This method returns the byte offset where the freelist starts
     *
     * @return byte offset of the freelists start point
     */
    public int startOfFreeList()
    {
        return Config.VFS_SUPER_BLOCK_SIZE;
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