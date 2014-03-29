package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.blocks.FileBlock;
import ch.ethz.jcd.main.blocks.ObjectBlock;
import ch.ethz.jcd.main.exceptions.BlockFullException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VDirectory extends VObject<DirectoryBlock>
{
    public VDirectory(DirectoryBlock block, VDirectory parent)
    {
        super(block, parent);
    }

    public List<VObject> getEntries() throws IOException
    {
        List<ObjectBlock> entryBlocks = block.getEntries();
        List<VObject> entryObjects = new ArrayList<VObject>(entryBlocks.size());

        for (ObjectBlock entryBlock : entryBlocks)
        {
            entryObjects.add(createCorrectVObject(entryBlock));
        }

        return entryObjects;
    }

    public void addEntry(VObject entry) throws IOException, BlockFullException
    {
        block.addEntry(entry.getBlock());
    }

    public void removeEntry(VObject entry) throws IOException, BlockFullException
    {
        block.removeEntry(entry.getBlock());
    }

    public VObject getEntry(String name) throws IOException
    {
        for (VObject entry : getEntries())
        {
            if (entry.getName().equals(name))
            {
                return entry;
            }
        }

        return null;
    }

    private VObject createCorrectVObject(ObjectBlock block)
    {
        if (block instanceof DirectoryBlock)
        {
            return new VDirectory((DirectoryBlock) block, this);
        } else if (block instanceof FileBlock)
        {
            return new VFile((FileBlock) block, this);
        }

        return null;
    }

    public void clear() throws IOException
    {
        try
        {
            block.setEntryCount(0);
        } catch (BlockFullException e)
        {
            // TODO Handle this
            e.printStackTrace();
        }
    }
}
