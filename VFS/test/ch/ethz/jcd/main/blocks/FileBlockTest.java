package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockEmptyException;
import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FileBlockTest
{
    private static final int BLOCK_ADDRESS = 0;
    private static final int DATA_BLOCK_1_SIZE = VUtil.BLOCK_SIZE;
    private static final int DATA_BLOCK_2_SIZE = VUtil.BLOCK_SIZE / 2;
    DataBlockListBlock dataBlockListBlock;
    DataBlock dataBlock1, dataBlock2;
    private FileManager fileManager;
    private FileBlock block;
    private File tmpFile;

    @Before
    public void setUp() throws Exception
    {
        tmpFile = File.createTempFile("test", "vfs");
        tmpFile.deleteOnExit();
        fileManager = new FileManager(tmpFile);

        // DataBlocks
        dataBlockListBlock = new DataBlockListBlock(fileManager, BLOCK_ADDRESS + 1);
        dataBlockListBlock.setUsedBlocks(0);
        dataBlock1 = new DataBlock(fileManager, BLOCK_ADDRESS + 2);
        dataBlock2 = new DataBlock(fileManager, BLOCK_ADDRESS + 3);

        block = new FileBlock(fileManager, BLOCK_ADDRESS);
        fileManager.writeLong(0, FileBlock.OFFSET_FILE_SIZE, 0L);
        block.addDataBlockListBlock(dataBlockListBlock);
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
            block.getDataBlock((VUtil.BLOCK_SIZE - FileBlock.OFFSET_FIRST_DATA_BLOCK_LIST_ENTRY) / FileBlock.SIZE_DATA_BLOCK_LIST_ENTRY);
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
    public void testGetDataBlockListBlock() throws Exception
    {
        assertEquals(dataBlockListBlock, block.getDataBlockListBlock(0));
        try
        {
            block.getDataBlockListBlock(-1);
            fail("Exception was expected for invalid data block address");
        } catch (IllegalArgumentException e)
        {
        }
        try
        {
            block.getDataBlockListBlock(1);
            fail("Exception was expected for invalid data block address");
        } catch (IllegalArgumentException e)
        {
        }
        try
        {
            block.getDataBlockListBlock(Integer.MAX_VALUE);
            fail("Exception was expected for invalid data block address");
        } catch (IllegalArgumentException e)
        {
        }
    }

    @Test
    public void testAddDataBlock() throws Exception
    {
        DataBlock block0 = new DataBlock(fileManager, BLOCK_ADDRESS + 4);
        block.addDataBlock(block0, VUtil.BLOCK_SIZE);
        assertEquals(3 * VUtil.BLOCK_SIZE, block.size());

        for (int i = 3; i < DataBlockListBlock.getCapacity(); i++)
        {
            block.addDataBlock(block0, VUtil.BLOCK_SIZE);
        }

        try
        {
            block.addDataBlock(block0, VUtil.BLOCK_SIZE);
            fail("Exception was expected for full block.");
        } catch (BlockFullException e)
        {
        }

        DataBlockListBlock dataBlockListBlock1 = new DataBlockListBlock(fileManager, BLOCK_ADDRESS + 5);
        dataBlockListBlock1.setUsedBlocks(0);
        block.addDataBlockListBlock(dataBlockListBlock1);
        block.addDataBlock(block0, VUtil.BLOCK_SIZE);
        assertEquals((DataBlockListBlock.getCapacity() + 1) * VUtil.BLOCK_SIZE, block.size());

        try
        {
            block.addDataBlock(null, VUtil.BLOCK_SIZE);
            fail("Exception was expected for null block");
        } catch (IllegalArgumentException e)
        {
        }

        try
        {
            block.addDataBlock(block0, -1);
            fail("Exception was expected for full DataBlockListBlock.");
        } catch (IllegalArgumentException e)
        {
        }

        try
        {
            block.addDataBlock(block0, 0);
            fail("Exception was expected for full DataBlockListBlock.");
        } catch (IllegalArgumentException e)
        {
        }
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
        long blockSize = block.count();
        for (int i = 0; i < blockSize; i++)
        {
            block.removeLastDataBlock();
        }
        assertEquals(0, block.count());

        try
        {
            block.removeLastDataBlock();
            fail("Exception was expected for removed data block");
        } catch (BlockEmptyException e)
        {
        }
    }

    @Test
    public void testGetDataBlockList() throws Exception
    {
        List<DataBlock> dataBlocks = block.getDataBlockList();
        assertEquals(block.count(), dataBlocks.size());
        assertEquals(dataBlock1, dataBlocks.get(0));
        assertEquals(dataBlock2, dataBlocks.get(1));
    }

    @Test
    public void testGetDataBlockListBlockList() throws Exception
    {
        List<DataBlockListBlock> dataBlockListBlocks = block.getDataBlockListBlockList();
        assertEquals(block.getDataBlockListBlocks(), dataBlockListBlocks.size());
        assertEquals(dataBlockListBlock, dataBlockListBlocks.get(0));
    }

    @Test
    public void testGetDataBlockListBlocks() throws Exception
    {
        assertEquals(1, block.getDataBlockListBlocks());
    }
}
