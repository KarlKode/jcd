package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.utils.FileManager;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ObjectBlockTest
{
    private static final int BLOCK_ADDRESS = 0;
    private static final byte TYPE = ObjectBlock.TYPE_DIRECTORY;
    private static final String NAME = "äÿ23456789" + "0123" + "01234567890123456789012345678901234567890123456789";
    private FileManager fileManager;
    private ObjectBlock block;

    @Before
    public void setUp() throws Exception
    {
        File tmpFile = File.createTempFile("test", "vfs");
        tmpFile.deleteOnExit();
        fileManager = new FileManager(tmpFile);
        // Type
        fileManager.writeByte(0, ObjectBlock.OFFSET_TYPE, TYPE);
        // Name
        fileManager.writeString(0, ObjectBlock.OFFSET_NAME, NAME);
        block = new ObjectBlock(fileManager, BLOCK_ADDRESS);
    }

    @Test
    public void testConstructor() throws Exception
    {
        new ObjectBlock(fileManager, 0);
        new ObjectBlock(fileManager, BLOCK_ADDRESS);
        new ObjectBlock(fileManager, Integer.MAX_VALUE);
        try
        {
            new ObjectBlock(null, 0);
            fail("Exception was expected for invalid file manager");
        } catch (IllegalArgumentException e)
        {
        }
        try
        {
            new ObjectBlock(fileManager, -1);
            fail("Exception was expected for invalid block address");
        } catch (IllegalArgumentException e)
        {
        }
    }

    @Test
    public void testGetType() throws Exception
    {
        assertEquals(TYPE, block.getType());
    }

    @Test
    public void testSetType() throws Exception
    {
        block.setType(ObjectBlock.TYPE_DIRECTORY);
        assertEquals(ObjectBlock.TYPE_DIRECTORY, fileManager.readByte(0, ObjectBlock.OFFSET_TYPE));
        block.setType(ObjectBlock.TYPE_FILE);
        assertEquals(ObjectBlock.TYPE_FILE, fileManager.readByte(0, ObjectBlock.OFFSET_TYPE));
        try
        {
            block.setType((byte) 0xFF);
            fail("Exception was expected for invalid type");
        } catch (IllegalArgumentException e)
        {
        }
    }

    @Test
    public void testGetName() throws Exception
    {
        assertEquals(NAME, block.getName());
    }

    @Test
    public void testSetName() throws Exception
    {
        String newName = "AA";
        block.setName(newName);
        assertEquals(newName, block.getName());
        try
        {
            block.setName("0123456789012345678901234567890123456789012345678901234567890123456789");
            fail("Exception was expected for invalid name");
        } catch (IllegalArgumentException e)
        {
        }
    }
}
