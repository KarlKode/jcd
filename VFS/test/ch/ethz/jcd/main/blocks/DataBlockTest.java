package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class DataBlockTest
{
    private static final int BLOCK_ADDRESS = 0;
    private FileManager fileManager;
    private DataBlock block;
    private byte[] bytes;

    @Before
    public void setUp() throws Exception
    {
        File tmpFile = File.createTempFile("test", "vfs");
        tmpFile.deleteOnExit();
        fileManager = new FileManager(tmpFile);
        block = new DataBlock(fileManager, BLOCK_ADDRESS);
        bytes = new byte[VUtil.BLOCK_SIZE];
        for (int i = 0; i < bytes.length; i++)
        {
            if (i % 2 == 0)
            {
                bytes[i] = (byte) 0xFA;
            } else
            {
                bytes[i] = (byte) 0xAF;
            }
        }
        fileManager.writeBytes(0, 0, bytes);
    }

    @Test
    public void testConstructor() throws Exception
    {
        new DataBlock(fileManager, 0);
        new DataBlock(fileManager, BLOCK_ADDRESS);
        new DataBlock(fileManager, Integer.MAX_VALUE);
        try
        {
            new DataBlock(null, 0);
            fail("Exception was expected for invalid file manager");
        } catch (IllegalArgumentException e)
        {
        }
        try
        {
            new DataBlock(fileManager, -1);
            fail("Exception was expected for invalid block address");
        } catch (IllegalArgumentException e)
        {
        }
    }

    @Test
    public void testGetContent() throws Exception
    {
        assertArrayEquals(fileManager.readBytes(0, 0, VUtil.BLOCK_SIZE), block.getContent());

        // Ensure updates are visible immediately
        fileManager.writeByte(0, 0, (byte) 0xFF);
        assertArrayEquals(fileManager.readBytes(0, 0, VUtil.BLOCK_SIZE), block.getContent());
    }

    @Test
    public void testGetContentWithOffset() throws Exception
    {
        int offset = 3;
        int length = 10;

        assertArrayEquals(fileManager.readBytes(0, offset, length), block.getContent(offset, length));

        // Ensure updates are visible immediately
        fileManager.writeByte(0, offset, (byte) 0xFF);
        assertArrayEquals(fileManager.readBytes(0, offset, length), block.getContent(offset, length));
    }

    @Test
    public void testSetContent() throws Exception
    {
        byte[] newBytes = new byte[VUtil.BLOCK_SIZE];
        for (int i = 0; i < newBytes.length; i++)
        {
            newBytes[i] = (byte) i;
        }
        block.setContent(newBytes);
        assertArrayEquals(fileManager.readBytes(0, 0, VUtil.BLOCK_SIZE), newBytes);

        try
        {
            block.setContent(null);
            fail("Exception was expected for invalid content");
        } catch (IllegalArgumentException e)
        {
        }

        try
        {
            block.setContent(new byte[VUtil.BLOCK_SIZE + 1]);
            fail("Exception was expected for invalid content");
        } catch (BlockFullException e)
        {
        }
    }

    @Test
    public void testSetContentWithOffset() throws Exception
    {
        int offset = 3;
        int length = 10;

        byte[] newBytes = new byte[length];
        for (int i = 0; i < newBytes.length; i++)
        {
            newBytes[i] = (byte) i;
        }
        block.setContent(newBytes, offset);
        assertArrayEquals(fileManager.readBytes(0, offset, length), newBytes);

        try
        {
            block.setContent(null, 1);
            fail("Exception was expected for invalid content");
        } catch (IllegalArgumentException e)
        {
        }

        try
        {
            block.setContent(new byte[1], -1);
            fail("Exception was expected for invalid offset");
        } catch (IllegalArgumentException e)
        {
        }

        try
        {
            block.setContent(new byte[1], VUtil.BLOCK_SIZE);
            fail("Exception was expected for invalid offset");
        } catch (IllegalArgumentException e)
        {
        }

        try
        {
            block.setContent(new byte[VUtil.BLOCK_SIZE + 1], 0);
            fail("Exception was expected for invalid content size and offset");
        } catch (BlockFullException e)
        {
        }

        try
        {
            block.setContent(new byte[VUtil.BLOCK_SIZE], 1);
            fail("Exception was expected for invalid content size and offset");
        } catch (BlockFullException e)
        {
        }
    }
}
