package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FileBlockTest
{
    private static final int BLOCK_ADDRESS = 0;
    private static final int DATA_BLOCK_1_SIZE = VUtil.BLOCK_SIZE;
    private static final int DATA_BLOCK_2_SIZE = VUtil.BLOCK_SIZE / 2;
    DataBlock dataBlock1, dataBlock2;
    private FileManager fileManager;
    private FileBlock block;

    @Before
    public void setUp() throws Exception
    {
        File tmpFile = File.createTempFile("test", "vfs");
        tmpFile.deleteOnExit();
        fileManager = new FileManager(tmpFile);

        // DataBlocks
        dataBlock1 = new DataBlock(fileManager, BLOCK_ADDRESS + 1);
        dataBlock2 = new DataBlock(fileManager, BLOCK_ADDRESS + 2);

        block = new FileBlock(fileManager, BLOCK_ADDRESS);
        fileManager.writeLong(0, FileBlock.OFFSET_FILE_SIZE, 0L);
        block.addDataBlock(dataBlock1, DATA_BLOCK_1_SIZE);
        block.addDataBlock(dataBlock2, DATA_BLOCK_2_SIZE);
    }

    @Test
    public void testConstructor() throws Exception
    {
        new FileBlock(fileManager, 0);
        new FileBlock(fileManager, BLOCK_ADDRESS);
        new FileBlock(fileManager, Integer.MAX_VALUE);
        try
        {
            new FileBlock(null, 0);
            fail("Exception was expected for invalid file manager");
        } catch (IllegalArgumentException e)
        {
        }
        try
        {
            new FileBlock(fileManager, -1);
            fail("Exception was expected for invalid block address");
        } catch (IllegalArgumentException e)
        {
        }
    }

    @Test
    public void testGetSize() throws Exception
    {
        // TODO Better test
        assertEquals(fileManager.readLong(0, FileBlock.OFFSET_FILE_SIZE), block.size());
        assertEquals(DATA_BLOCK_1_SIZE + DATA_BLOCK_2_SIZE, block.size());
    }

    @Test
    public void testGetDataBlock() throws Exception
    {
        assertEquals(dataBlock1, block.getDataBlock(0));
        assertEquals(dataBlock2, block.getDataBlock(1));
        try
        {
            block.getDataBlock(-1);
            fail("Exception was expected for invalid data block address");
        } catch (IllegalArgumentException e)
        {
        }
        try
        {
            block.getDataBlock((VUtil.BLOCK_SIZE - FileBlock.OFFSET_FIRST_ENTRY) / FileBlock.SIZE_ENTRY);
            fail("Exception was expected for invalid data block address");
        } catch (IllegalArgumentException e)
        {
        }
        try
        {
            block.getDataBlock(3);
            fail("Exception was expected for invalid data block address");
        } catch (IllegalArgumentException e)
        {
        }
    }

    @Test
    public void testAddDataBlock() throws Exception
    {
        DataBlock block0 = new DataBlock(fileManager, BLOCK_ADDRESS + 3);
        block.addDataBlock(block0, 0);
        assertEquals(2 * VUtil.BLOCK_SIZE, block.size());
    }

    @Test
    public void testRemoveLastDataBlock() throws Exception
    {
        block.removeLastDataBlock();
        assertEquals(VUtil.BLOCK_SIZE, block.size());
        assertEquals(dataBlock1, block.getDataBlock(0));
        try
        {
            block.getDataBlock(1);
            fail("Exception was expected for removed data block");
        } catch (IllegalArgumentException e)
        {
        }
        for (int i = 0; i < 1024; i++)
        {
            block.removeLastDataBlock();
        }
        assertEquals(0, block.size());
    }
}
