package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.exceptions.InvalidBlockSizeException;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class SuperBlockTest
{
    private static final int VDISK_SIZE = SuperBlock.MIN_SUPER_BLOCK_SIZE * 8;
    private static final int VDISK_BLOCK_SIZE = SuperBlock.MIN_SUPER_BLOCK_SIZE;
    private static final int VDISK_BLOCK_COUNT = VDISK_SIZE / VDISK_BLOCK_SIZE;
    private static final int VDISK_ROOT_DIRECTORY_BLOCK = 2;
    public static final int VDISK_FIRST_BIT_MAP_BLOCK_ADDRESS = SuperBlock.BIT_MAP_BLOCK_ADDRESS;
    public static final int VDISK_LAST_BIT_MAP_BLOCK_ADDRESS = SuperBlock.BIT_MAP_BLOCK_ADDRESS;
    public static final int VDISK_FIRST_DATA_BLOCK_ADDRESS = SuperBlock.DATA_BLOCK_BEGIN_ADDRESS;
    private ByteBuffer buffer;
    private SuperBlock block;

    /**
     * constants to test whether the constructor sets blockSize, blockCount,
     * rootDirectoryBlock correctly
     */
    private static final byte[] TEST_CONSTRUCTOR = new byte[]{0, 0, 0, 64, 0, 0, 1, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final int TEST_CONSTRUCTOR_BLOCK_SIZE = 64;
    private static final int TEST_CONSTRUCTOR_BLOCK_COUNT = 256;
    private static final int TEST_CONSTRUCTOR_ROOT_DIRECTORY_BLOCK_ADDRESS = 2;


    @Before
    public void setUp() throws Exception
    {
        byte[] bytes = new byte[VDISK_BLOCK_SIZE];
        buffer = ByteBuffer.wrap(bytes);
        buffer.putInt(SuperBlock.OFFSET_BLOCK_SIZE, VDISK_BLOCK_SIZE);
        buffer.putInt(SuperBlock.OFFSET_BLOCK_COUNT, VDISK_BLOCK_COUNT);
        buffer.putInt(SuperBlock.OFFSET_ROOT_DIRECTORY_BLOCK, VDISK_ROOT_DIRECTORY_BLOCK);
        block = new SuperBlock(bytes);
    }

    @Test(expected = InvalidBlockSizeException.class)
    public void testConstructorInvalid0() throws Exception
    {
        block = new SuperBlock(new byte[0]);
    }

    @Test(expected = InvalidBlockSizeException.class)
    public void testConstructorInvalid1() throws Exception
    {
        block = new SuperBlock(null);
    }

    @Test
    public void testConstructor() throws InvalidBlockSizeException
    {
        SuperBlock superBlock = new SuperBlock(TEST_CONSTRUCTOR);
        assertEquals(TEST_CONSTRUCTOR_BLOCK_SIZE, superBlock.getBlockSize());
        assertEquals(TEST_CONSTRUCTOR_BLOCK_COUNT, superBlock.getBlockCount());
        assertEquals(TEST_CONSTRUCTOR_ROOT_DIRECTORY_BLOCK_ADDRESS, superBlock.getRootDirectoryBlock());
    }

    @Test(expected = InvalidBlockSizeException.class)
    public void testSetBlockSize() throws Exception
    {
        int blockSize;

        blockSize = VDISK_BLOCK_SIZE;
        block.setBlockSize(blockSize);
        assertEquals(blockSize, block.getBlockSize());
        assertEquals(blockSize, buffer.getInt(SuperBlock.OFFSET_BLOCK_SIZE));

        blockSize = VDISK_BLOCK_SIZE * 2;
        block.setBlockSize(blockSize);
        assertEquals(blockSize, block.getBlockSize());
        assertEquals(blockSize, buffer.getInt(SuperBlock.OFFSET_BLOCK_SIZE));

        blockSize = VDISK_SIZE;
        block.setBlockSize(blockSize);
        assertEquals(blockSize, block.getBlockSize());
        assertEquals(blockSize, buffer.getInt(SuperBlock.OFFSET_BLOCK_SIZE));

        blockSize = 0;
        block.setBlockSize(blockSize);
        assertEquals(blockSize, block.getBlockSize());
        assertEquals(blockSize, buffer.getInt(SuperBlock.OFFSET_BLOCK_SIZE));
    }

    @Test
    public void testGetBlockSize() throws Exception
    {
        assertEquals(VDISK_BLOCK_SIZE, block.getBlockSize());
    }

    @Test(expected = InvalidBlockCountException.class)
    public void testSetBlockCount() throws Exception
    {
        int blockCount;

        blockCount = VDISK_BLOCK_COUNT;
        block.setBlockCount(blockCount);
        assertEquals(blockCount, block.getBlockCount());
        assertEquals(blockCount, buffer.getInt(SuperBlock.OFFSET_BLOCK_COUNT));

        blockCount = VDISK_BLOCK_SIZE / VDISK_BLOCK_SIZE;
        block.setBlockCount(blockCount);
        assertEquals(blockCount, block.getBlockCount());
        assertEquals(blockCount, buffer.getInt(SuperBlock.OFFSET_BLOCK_COUNT));

        blockCount = 0;
        block.setBlockCount(blockCount);
    }

    @Test
    public void testGetBlockCount() throws Exception
    {
        assertEquals(VDISK_BLOCK_COUNT, block.getBlockCount());
    }

    @Test(expected = InvalidBlockAddressException.class)
    public void testSetRootDirectoryBlock() throws Exception
    {
        int rootDirectoryBlockAddress;

        rootDirectoryBlockAddress = VDISK_ROOT_DIRECTORY_BLOCK;
        block.setRootDirectoryBlock(rootDirectoryBlockAddress);
        assertEquals(rootDirectoryBlockAddress, block.getRootDirectoryBlock());
        assertEquals(rootDirectoryBlockAddress, buffer.getInt(SuperBlock.OFFSET_ROOT_DIRECTORY_BLOCK));

        rootDirectoryBlockAddress = -1;
        block.setRootDirectoryBlock(rootDirectoryBlockAddress);
    }

    @Test
    public void testGetRootDirectoryBlock() throws Exception
    {
        assertEquals(VDISK_ROOT_DIRECTORY_BLOCK, block.getRootDirectoryBlock());
    }

    @Test
    public void testGetFirstBitMapBlock() throws Exception
    {
        assertEquals(VDISK_FIRST_BIT_MAP_BLOCK_ADDRESS, block.getFirstBitMapBlock());
    }

    @Test
    public void testGetLastBitMapBlock() throws Exception
    {
        assertEquals(VDISK_LAST_BIT_MAP_BLOCK_ADDRESS, block.getLastBitMapBlock());
    }

    @Test
    public void testGetFirstDataBlock() throws Exception
    {
        assertEquals(VDISK_FIRST_DATA_BLOCK_ADDRESS, block.getFirstDataBlock());
    }
}
