package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.blocks.FileBlock;
import ch.ethz.jcd.main.blocks.ObjectBlock;
import ch.ethz.jcd.main.exceptions.BlockFullException;

import java.io.IOException;

public class VDirectory extends VObject<DirectoryBlock>
{
    public VDirectory(DirectoryBlock block, VDirectory parent)
    {
        super(block, parent);
    }

    public VObject[] getEntries() throws IOException
    {
        ObjectBlock[] entryBlocks = block.getEntries();
        VObject[] entryObjects = new VObject[entryBlocks.length];

        for (int i = 0; i < entryBlocks.length; i++)
        {
            if (entryBlocks[i] instanceof DirectoryBlock)
            {
                entryObjects[i] = new VDirectory((DirectoryBlock) entryBlocks[i], this);
            } else
            {
                entryObjects[i] = new VFile((FileBlock) entryBlocks[i], this);
            }
        }

        return entryObjects;
    }

    public void addEntry(VObject entry) throws IOException, BlockFullException
    {
        block.addEntry(entry.getBlock());
    }

    public void removeEntry(VObject entry) throws IOException
    {
        block.removeEntry(entry.getBlock());
    }
}
