package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DirectoryBlock extends ObjectBlock
{
    public static final int SIZE_ENTRY_COUNT = 4;
    public static final int SIZE_ENTRY = 4;
    public static final int OFFSET_ENTRY_COUNT = OFFSET_CONTENT;
    public static final int OFFSET_FIRST_ENTRY = OFFSET_ENTRY_COUNT + SIZE_ENTRY_COUNT;

    public DirectoryBlock(FileManager fileManager, int blockAddress) throws InvalidBlockAddressException
    {
        super(fileManager, blockAddress);
    }

    public int getEntryCount() throws IOException
    {
        return fileManager.readInt(getBlockOffset(), OFFSET_ENTRY_COUNT);
    }

    public void setEntryCount(int entryCount) throws IOException, BlockFullException, IllegalArgumentException
    {
        if (entryCount < 0)
        {
            throw new IllegalArgumentException();
        }
        int maxEntries = (VUtil.BLOCK_SIZE - OFFSET_FIRST_ENTRY) / SIZE_ENTRY;
        if (entryCount > maxEntries)
        {
            throw new BlockFullException();
        }

        fileManager.writeInt(getBlockOffset(), OFFSET_ENTRY_COUNT, entryCount);
    }

    public List<ObjectBlock> getEntries() throws IOException
    {
        int entryCount = getEntryCount();
        List<ObjectBlock> entries = new ArrayList<>(entryCount);

        for (int i = 0; i < entryCount; i++)
        {
            int entryBlockAddress = fileManager.readInt(getBlockOffset(), OFFSET_FIRST_ENTRY + (i * SIZE_ENTRY));

            try
            {
                // TODO: Should we just check the type of the entry here without instantiating a new ObjectBlock?
                ObjectBlock objectBlock = new ObjectBlock(fileManager, entryBlockAddress);

                if (objectBlock.getType() == ObjectBlock.TYPE_DIRECTORY)
                {
                    entries.add(new DirectoryBlock(fileManager, entryBlockAddress));
                } else if (objectBlock.getType() == ObjectBlock.TYPE_FILE)
                {
                    entries.add(new FileBlock(fileManager, entryBlockAddress));
                } else
                {
                    // TODO: Throw correct exception
                    throw new NotImplementedException();
                }
            } catch (InvalidBlockAddressException e)
            {
                // TODO: Throw new error or something
                e.printStackTrace();
                throw new NotImplementedException();
            }
        }

        return entries;
    }

    public void setEntries(List<ObjectBlock> entries) throws BlockFullException, IOException
    {
        // Write new entry count
        setEntryCount(entries.size());

        // Write new entry list
        for (int i = 0; i < entries.size(); i++)
        {
            fileManager.writeInt(getBlockOffset(), OFFSET_FIRST_ENTRY + (i * SIZE_ENTRY), entries.get(i).getBlockAddress());
        }
    }

    public void addEntry(ObjectBlock entry) throws IOException, BlockFullException
    {
        List<ObjectBlock> entries = getEntries();
        entries.add(entry);
        setEntries(entries);
    }

    public void removeEntry(ObjectBlock entry) throws IOException, BlockFullException
    {
        List<ObjectBlock> entries = getEntries();
        entries.remove(entry);
        setEntries(entries);
    }
}
