package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.exceptions.ToDoException;
import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VDisk;
import ch.ethz.jcd.main.utils.VUtil;
import ch.ethz.jcd.main.visitor.BlockVisitor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

    public ObjectBlock[] getEntries() throws IOException
    {
        int entryCount = getEntryCount();
        ObjectBlock[] entries = new ObjectBlock[entryCount];

        for (int i = 0; i < entryCount; i++) {
            int entryBlockAddress = fileManager.readInt(getBlockOffset(), OFFSET_FIRST_ENTRY + (i * SIZE_ENTRY));

            try
            {
                // TODO: Should we just check the type of the entry here without instantiating a new ObjectBlock?
                ObjectBlock objectBlock = new ObjectBlock(fileManager, entryBlockAddress);

                if (objectBlock.getType() == ObjectBlock.TYPE_DIRECTORY) {
                    entries[i]= new DirectoryBlock(fileManager, entryBlockAddress);
                } else if (objectBlock.getType() == ObjectBlock.TYPE_FILE) {
                    entries[i] = new FileBlock(fileManager, entryBlockAddress);
                } else {
                    // TODO: Throw correct exception
                    throw new NotImplementedException();
                }
            } catch (InvalidBlockAddressException e)
            {
                // TODO: Throw new error or something
                e.printStackTrace();
            }
        }

        return entries;
    }

    public void addEntry(ObjectBlock entry) throws IOException, BlockFullException
    {
        int entryCount = getEntryCount();

        // Check if the directory has room for an additional entry
        int maxEntries = (VUtil.BLOCK_SIZE - OFFSET_FIRST_ENTRY) / SIZE_ENTRY;
        if (entryCount >= maxEntries) {
            throw new BlockFullException();
        }

        // Write the block address of the new entry
        fileManager.writeInt(getBlockOffset(), OFFSET_FIRST_ENTRY + (entryCount * SIZE_ENTRY), entry.getBlockAddress());
        // Write the new entry count
        fileManager.writeInt(getBlockOffset(), OFFSET_ENTRY_COUNT, entryCount + 1);
    }

    public void removeEntry(ObjectBlock entry) throws IOException
    {
        int entryCount = getEntryCount();
        ObjectBlock[] entries = getEntries();

        boolean found = false;

        // Check each entry of the directory if it is equal to the one from the parameter.
        // If yes overwrite all entries beginning with it with their successor.
        // The last entry won't be overwritten but will be ignored in the future because of the updated entry count.
        // Example:
        //     Remove entry with index i (out of n) (i < n)
        //     Entry i will be overwritten with entry i + 1, entry i + 1 with i + 2 ... n - 1 with n
        for (int i = 0; i < entryCount; i++) {
            if (entry.equals(entries[i])) {
                // Do not start the overwriting process here, i could be zero and therefore i - 1 = -1!
                found = true;
                // Write new entry count
                fileManager.writeInt(getBlockOffset(), OFFSET_ENTRY_COUNT, entryCount - 1);
            } else if (found) {
                // Overwrite the last entry with the current one (the last entry will not be modified)
                fileManager.writeInt(getBlockOffset(), OFFSET_FIRST_ENTRY + ((i - 1) * SIZE_ENTRY), entries[i].getBlockAddress());
            }
        }

        if (!found) {
            // TODO: Throw correct exception
            throw new NotImplementedException();
        }
    }
}
