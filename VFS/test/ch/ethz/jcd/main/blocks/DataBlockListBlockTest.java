package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DataBlockListBlockTest
{

    private static final int BLOCK_ADDRESS = 0;
    private static final int DATA_BLOCK_1_SIZE = 0;
    private static final int DATA_BLOCK_2_SIZE = 1;
    DataBlock dataBlock1, dataBlock2;
    private FileManager fileManager;
    DataBlockListBlock block;

    @Before
    public void setUp() throws Exception
    {
        File tmpFile = File.createTempFile("test", "vfs");
        tmpFile.deleteOnExit();
        fileManager = new FileManager(tmpFile);

        // DataBlocks
        dataBlock1 = new DataBlock(fileManager, BLOCK_ADDRESS + 1);
        dataBlock2 = new DataBlock(fileManager, BLOCK_ADDRESS + 2);

        block = new DataBlockListBlock(fileManager, BLOCK_ADDRESS);
        block.setDataBlockAddress(0, dataBlock1.getBlockAddress());
        block.setDataBlockAddress(1, dataBlock2.getBlockAddress());
        block.setUsedBlocks(2);
    }

    @Test
    public void testConstructor() throws Exception
    {

        block = new DataBlockListBlock(fileManager, 0);
        assertEquals(2, block.getUsedBlocks());
    }

    @Test
    public void testGetCapacity() throws Exception
    {
        assertEquals((VUtil.BLOCK_SIZE - 4) / 4, DataBlockListBlock.getCapacity());
    }

    @Test
    public void testGetUsedBlocks() throws Exception
    {
        assertEquals(2, block.getUsedBlocks());
        fileManager.writeInt(0, DataBlockListBlock.OFFSET_USED_BLOCKS, 0);
        assertEquals(0, block.getUsedBlocks());
        fileManager.writeInt(0, DataBlockListBlock.OFFSET_USED_BLOCKS, 123);
        assertEquals(123, block.getUsedBlocks());
        fileManager.writeInt(0, DataBlockListBlock.OFFSET_USED_BLOCKS, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, block.getUsedBlocks());
    }

    @Test
    public void testSetUsedBlocks() throws Exception
    {
        block.setUsedBlocks(0);
        assertEquals(0, fileManager.readInt(0, DataBlockListBlock.OFFSET_USED_BLOCKS));
        block.setUsedBlocks(123);
        assertEquals(123, fileManager.readInt(0, DataBlockListBlock.OFFSET_USED_BLOCKS));
        block.setUsedBlocks(block.getUsedBlocks() + 1);
        assertEquals(124, fileManager.readInt(0, DataBlockListBlock.OFFSET_USED_BLOCKS));
        block.setUsedBlocks(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, fileManager.readInt(0, DataBlockListBlock.OFFSET_USED_BLOCKS));
    }

    @Test
    public void testGetDataBlockAddress() throws Exception
    {
        assertEquals(BLOCK_ADDRESS + 1, block.getDataBlockAddress(0));
        assertEquals(BLOCK_ADDRESS + 2, block.getDataBlockAddress(1));
        fileManager.writeInt(0, DataBlockListBlock.OFFSET_BLOCK_LIST, 123);
        assertEquals(123, block.getDataBlockAddress(0));
        fileManager.writeInt(0, DataBlockListBlock.OFFSET_BLOCK_LIST + DataBlockListBlock.SIZE_ENTRY, 124);
        assertEquals(124, block.getDataBlockAddress(1));
        int cap = DataBlockListBlock.getCapacity();
        fileManager.writeInt(0, DataBlockListBlock.OFFSET_BLOCK_LIST + (cap - 1) * DataBlockListBlock.SIZE_ENTRY, 125);
        assertEquals(125, block.getDataBlockAddress(cap - 1));
        fileManager.writeInt(0, DataBlockListBlock.OFFSET_BLOCK_LIST, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, block.getDataBlockAddress(0));
        fileManager.writeInt(0, DataBlockListBlock.OFFSET_BLOCK_LIST + DataBlockListBlock.SIZE_ENTRY, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, block.getDataBlockAddress(0));
        fileManager.writeInt(0, DataBlockListBlock.OFFSET_BLOCK_LIST + (cap - 1) * DataBlockListBlock.SIZE_ENTRY, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, block.getDataBlockAddress(cap - 1));

        try
        {
            block.getDataBlockAddress(-1);
            fail("Exception was expected for invalid block address");
        } catch (IllegalArgumentException e)
        {
        }

        try
        {
            block.getDataBlockAddress(DataBlockListBlock.getCapacity());
            fail("Exception was expected for invalid block address");
        } catch (IllegalArgumentException e)
        {
        }
    }

    @Test
    public void testSetDataBlockAddress() throws Exception
    {
        int cap = DataBlockListBlock.getCapacity() - 1;
        block.setDataBlockAddress(0, 123);
        assertEquals(123, fileManager.readInt(0, DataBlockListBlock.OFFSET_BLOCK_LIST));
        block.setDataBlockAddress(1, 124);
        assertEquals(124, fileManager.readInt(0, DataBlockListBlock.OFFSET_BLOCK_LIST + DataBlockListBlock.SIZE_ENTRY));
        block.setDataBlockAddress(cap, 125);
        assertEquals(125, fileManager.readInt(0, DataBlockListBlock.OFFSET_BLOCK_LIST + cap * DataBlockListBlock.SIZE_ENTRY));
        block.setDataBlockAddress(cap, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, fileManager.readInt(0, DataBlockListBlock.OFFSET_BLOCK_LIST + cap * DataBlockListBlock.SIZE_ENTRY));

        try
        {
            block.setDataBlockAddress(-1, 0);
            fail("Exception was expected for invalid block address");
        } catch (IllegalArgumentException e)
        {
        }

        try
        {
            block.setDataBlockAddress(DataBlockListBlock.getCapacity(), 0);
            fail("Exception was expected for invalid block address");
        } catch (IllegalArgumentException e)
        {
        }
    }
}