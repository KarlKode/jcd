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
        ENTRIES.clear();
        ENTRIES.add(new DirectoryBlock(fileManager, 1));
        ENTRIES.add(new DirectoryBlock(fileManager, 2));
        ENTRIES.add(new FileBlock(fileManager, 3));
        for (int i = 0; i < ENTRIES.size(); i++)
        {
            fileManager.writeInt(0, DirectoryBlock.OFFSET_FIRST_ENTRY + i * DirectoryBlock.SIZE_ENTRY, ENTRIES.get(i).getBlockAddress());
            ENTRIES.get(i).setType((ENTRIES.get(i) instanceof DirectoryBlock) ? ObjectBlock.TYPE_DIRECTORY : ObjectBlock.TYPE_FILE);
            ENTRIES.get(i).setName("test_" + String.valueOf(i));
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
        assertEquals(ENTRIES, entries);
    }

    @Test
    public void testSetEntries() throws Exception
    {
        ObjectBlock newEntry = new FileBlock(fileManager, 4);
        newEntry.setName("new file");

        List<ObjectBlock> newEntries = new ArrayList<>(ENTRIES);
        newEntries.add(newEntry);
        block.setEntries(newEntries);

        assertEquals(newEntries, block.getEntries());
    }

    @Test
    public void testAddEntry() throws Exception
    {
        ObjectBlock newEntry = new FileBlock(fileManager, 4);
        newEntry.setType(ObjectBlock.TYPE_FILE);
        newEntry.setName("test file");
        ENTRIES.add(newEntry);
        block.addEntry(newEntry);
        assertEquals(ENTRIES, block.getEntries());
    }

    @Test
    public void testRemoveEntry() throws Exception
    {
        ObjectBlock entry = ENTRIES.get(0);
        ENTRIES.remove(entry);
        block.removeEntry(entry);
        assertEquals(ENTRIES, block.getEntries());
    }
}
