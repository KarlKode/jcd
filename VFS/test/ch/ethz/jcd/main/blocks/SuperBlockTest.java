package ch.ethz.jcd.main.blocks;

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
    public static final int VDISK_FIRST_BIT_MAP_BLOCK_ADDRESS = 1;
    public static final int VDISK_LAST_BIT_MAP_BLOCK_ADDRESS = 1;
    public static final int VDISK_FIRST_DATA_BLOCK_ADDRESS = 2;
    private byte[] bytes;
    private ByteBuffer buffer;
    private SuperBlock block;


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
        assertEquals(VDISK_LAST_BIT_MAP_BLOCK_ADDRESS, block.getFirstBitMapBlock());
    }

    @Test
    public void testGetFirstDataBlock() throws Exception
    {
        assertEquals(VDISK_FIRST_DATA_BLOCK_ADDRESS, block.getFirstDataBlock());
    }
}
