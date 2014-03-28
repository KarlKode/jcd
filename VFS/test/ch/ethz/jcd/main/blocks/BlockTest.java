package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BlockTest
{
    private static final int BLOCK_ADDRESS = 12345;
    private FileManager fileManager;
    private Block block;

    @Before
    public void setUp() throws Exception
    {
        File tmpFile = File.createTempFile("test", "vfs");
        tmpFile.deleteOnExit();
        fileManager = new FileManager(tmpFile);
        block = new Block(fileManager, BLOCK_ADDRESS);
    }

    @Test
    public void testConstructor() throws Exception
    {
        new Block(fileManager, 0);
        new Block(fileManager, BLOCK_ADDRESS);
        new Block(fileManager, Integer.MAX_VALUE);
        try
        {
            new Block(null, 0);
            fail("Exception was expected for invalid file manager");
        } catch (IllegalArgumentException e)
        {
        }
        try
        {
            new Block(fileManager, -1);
            fail("Exception was expected for invalid block address");
        } catch (IllegalArgumentException e)
        {
        }
    }

    @Test
    public void testGetBlockAddress() throws Exception
    {
        assertEquals(BLOCK_ADDRESS, block.getBlockAddress());
    }

    @Test
    public void testIsInvalidBlockAddress() throws Exception
    {
        assertEquals(false, block.isInvalidBlockAddress(0));
        assertEquals(false, block.isInvalidBlockAddress(BLOCK_ADDRESS));
        assertEquals(false, block.isInvalidBlockAddress(Integer.MAX_VALUE));
        assertEquals(true, block.isInvalidBlockAddress(-1));
        assertEquals(true, block.isInvalidBlockAddress(Integer.MIN_VALUE));
    }

    @Test
    public void testGetBlockOffset() throws Exception
    {
        assertEquals((long) BLOCK_ADDRESS * VUtil.BLOCK_SIZE, block.getBlockOffset());
    }
}
