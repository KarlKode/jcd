package ch.ethz.jcd.main.blocks;

public class SuperBlock extends DirectoryBlock
{
    public static final int MIN_SUPER_BLOCK_SIZE = 16;
    public static int SUPER_BLOCK_ADDRESS = 0;
    public static int BIT_MAP_BLOCK_ADDRESS = 1;

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
}