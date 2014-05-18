package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.IOException;

/**
 * Super block that stores all metadata of the VFS
 */
public class SuperBlock extends Block {
    public static final int OFFSET_BLOCK_COUNT = 0;
    public static final int OFFSET_VDISK_FLAGS = 4;
    public static final int OFFSET_ROOT_DIRECTORY_BLOCK = 8;
    public static final int SUPER_BLOCK_ADDRESS = 0;
    public static final int BIT_MAP_BLOCK_ADDRESS = 1;
    public static final int DATA_BLOCK_BEGIN_ADDRESS = 2;

    public SuperBlock(FileManager fileManager, int blockAddress)
            throws IllegalArgumentException {
        super(fileManager, blockAddress);

        if (blockAddress != SUPER_BLOCK_ADDRESS) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * This method loads the maximum number of Blocks that could be stored in
     * the virtual file system.
     *
     * @return block count
     * @throws IOException
     */
    public int getBlockCount()
            throws IOException {
        return fileManager.readInt(getBlockOffset(), OFFSET_BLOCK_COUNT);
    }

    /**
     * @param blockCount new block count
     * @throws IOException
     * @throws IllegalArgumentException if new block count is invalid
     */
    public void setBlockCount(int blockCount)
            throws IOException, IllegalArgumentException {
        // Check validity of block count
        // TODO: Block count > number of supported blocks
        if (blockCount < 0) {
            throw new IllegalArgumentException();
        }

        fileManager.writeInt(getBlockOffset(), OFFSET_BLOCK_COUNT, blockCount);
    }

    /**
     * This method loads the maximum number of Blocks that could be stored in
     * the virtual file system.
     *
     * @return block count
     * @throws IOException
     */
    public int getVDiskFlags()
            throws IOException {
        return fileManager.readInt(getBlockOffset(), OFFSET_VDISK_FLAGS);
    }

    /**
     * Sets the properties of the loaded VDisk computed by interpreting the given flag as follows
     * <p>
     * Flags
     * ------
     * <p>
     * 0x00       file system does not have any properties
     * 0x01       file system is compressed
     * 0x02      file system is encrypted
     * 0x04       file system is indexed
     * <p>
     * eg. given flag = 5  =>  5 = 2^2 + 2^1 means that the file system is compressed and indexed
     *
     * @param flag to set
     * @throws IOException
     * @throws IllegalArgumentException if new block count is invalid
     */
    public void setVDiskFlags(int flag)
            throws IOException, IllegalArgumentException {
        // Check validity of flag
        if (flag < 0) {
            throw new IllegalArgumentException();
        }
        fileManager.writeInt(getBlockOffset(), OFFSET_VDISK_FLAGS, flag);
    }

    /**
     * @return block address of root directory block
     * @throws IOException
     */
    public int getRootDirectoryBlock()
            throws IOException {
        return fileManager.readInt(getBlockOffset(), OFFSET_ROOT_DIRECTORY_BLOCK);
    }


    /**
     * @param rootDirectoryBlockAddress new root directory block
     * @throws IOException
     */
    public void setRootDirectoryBlock(int rootDirectoryBlockAddress)
            throws IOException {
        fileManager.writeInt(getBlockOffset(), OFFSET_ROOT_DIRECTORY_BLOCK, rootDirectoryBlockAddress);
    }

    /**
     * @return block address of first bit map block
     */
    public int getFirstBitMapBlock() {
        return BIT_MAP_BLOCK_ADDRESS;
    }

    /**
     * @return block address of last bit map block
     */
    public int getLastBitMapBlock() {
        // TODO: Calculate the real size of the bit map
        return BIT_MAP_BLOCK_ADDRESS;
    }

    /**
     * @return block address of first block that does not store any filesystem metadata and stores actual content
     */
    public int getFirstDataBlock() throws IOException {
        return BIT_MAP_BLOCK_ADDRESS + (int) (Math.ceil((double) getBlockCount() / (VUtil.BLOCK_SIZE * 8)));
    }
}