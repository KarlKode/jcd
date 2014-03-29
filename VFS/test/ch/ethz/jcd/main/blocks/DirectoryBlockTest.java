package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.utils.FileManager;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DirectoryBlockTest
{
    private static final int BLOCK_ADDRESS = 0;
    private static final List<ObjectBlock> ENTRIES = new ArrayList<>();
    private FileManager fileManager;
    private DirectoryBlock block;

    @Before
    public void setUp() throws Exception
    {
        File tmpFile = File.createTempFile("test", "vfs");
        tmpFile.deleteOnExit();
        fileManager = new FileManager(tmpFile);

        // Entries
        ENTRIES.add(new DirectoryBlock(fileManager, 1));
        ENTRIES.add(new DirectoryBlock(fileManager, 2));
        ENTRIES.add(new FileBlock(fileManager, 3));
        for (int i = 0; i < ENTRIES.size(); i++)
        {
            fileManager.writeInt(0, DirectoryBlock.OFFSET_FIRST_ENTRY + i * DirectoryBlock.SIZE_ENTRY, ENTRIES.get(i).getBlockAddress());
            fileManager.writeByte(ENTRIES.get(i).getBlockOffset(), ObjectBlock.OFFSET_TYPE, (ENTRIES.get(i) instanceof DirectoryBlock) ? ObjectBlock.TYPE_DIRECTORY : ObjectBlock.TYPE_FILE);
            fileManager.writeString(ENTRIES.get(i).getBlockOffset(), ObjectBlock.OFFSET_NAME, String.valueOf(i));
        }
        // Entry count
        fileManager.writeInt(BLOCK_ADDRESS, DirectoryBlock.OFFSET_ENTRY_COUNT, ENTRIES.size());

        block = new DirectoryBlock(fileManager, BLOCK_ADDRESS);
    }

    @Test
    public void testConstructor() throws Exception
    {
        new DirectoryBlock(fileManager, 0);
        new DirectoryBlock(fileManager, BLOCK_ADDRESS);
        new DirectoryBlock(fileManager, Integer.MAX_VALUE);
        try
        {
            new DirectoryBlock(null, 0);
            fail("Exception was expected for invalid file manager");
        } catch (IllegalArgumentException e)
        {
        }
        try
        {
            new DirectoryBlock(fileManager, -1);
            fail("Exception was expected for invalid block address");
        } catch (IllegalArgumentException e)
        {
        }
    }

    @Test
    public void testGetEntryCount() throws Exception
    {
        assertEquals(ENTRIES.size(), block.getEntryCount());
    }

    @Test
    public void testSetEntryCount() throws Exception
    {
        int newEntryCount = 0;
        block.setEntryCount(newEntryCount);
        assertEquals(newEntryCount, fileManager.readInt(0, DirectoryBlock.OFFSET_ENTRY_COUNT));
        newEntryCount = 123;
        block.setEntryCount(newEntryCount);
        assertEquals(newEntryCount, fileManager.readInt(0, DirectoryBlock.OFFSET_ENTRY_COUNT));
        try
        {
            block.setEntryCount(Integer.MAX_VALUE);
            fail("Exception was expected for invalid entry count");
        } catch (BlockFullException e)
        {
        }
        try
        {
            block.setEntryCount(-1);
            fail("Exception was expected for invalid entry count");
        } catch (IllegalArgumentException e)
        {
        }
    }

    @Test
    public void testGetEntries() throws Exception
    {
        List<ObjectBlock> entries = block.getEntries();
        assertEquals(ENTRIES.size(), entries.size());
        for (int i = 0; i < ENTRIES.size(); i++)
        {
            assertEquals(ENTRIES.get(i), entries.get(i));
        }
    }

    @Test
    public void testSetEntries() throws Exception
    {

    }

    @Test
    public void testAddEntry() throws Exception
    {

    }

    @Test
    public void testRemoveEntry() throws Exception
    {

    }
}
