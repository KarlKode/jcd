package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.blocks.FileBlock;
import ch.ethz.jcd.main.blocks.ObjectBlock;
import ch.ethz.jcd.main.exceptions.BlockFullException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * VDirectory is a concrete implementation of VObject coming with additional features such as
 * copy(), delete() and entry manipulation.
 */
public class VDirectory extends VObject<DirectoryBlock>
{
    /**
     * Instantiation of a new VDirectory.
     *
     * @param block containing the byte structure of the VDirectory
     * @param parent of the VDirectory
     */
    public VDirectory(DirectoryBlock block, VDirectory parent)
    {
        super(block, parent);
    }

    /**
     * This Method recursively copies the VDirectory to the given destination
     *
     * @param destination where to put the copied VDirectory
     */
    @Override
    public void copy(VDirectory destination) throws BlockFullException, IOException
    {
        VDirectory copy = new VDirectory(this.block.clone( ), destination);

        for(VObject<ObjectBlock> obj : this.getEntries())
        {
            obj.copy(copy);
        }
        destination.addEntry(copy);
    }

    /**
     * This Method recursively deletes the VDirectory
     */
    @Override
    public void delete() throws IOException
    {
        for(VObject<ObjectBlock> obj : this.getEntries())
        {
            obj.delete();
        }
        this.block.delete( );
    }

    public void addEntry(VObject entry) throws BlockFullException, IOException
    {
        block.addEntry(entry.getBlock());
    }

    public void removeEntry(VObject entry) throws BlockFullException, IOException
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

    public List<VObject> getEntries() throws IOException
    {
        List<ObjectBlock> entryBlocks = block.getEntries();
        List<VObject> entryObjects = new ArrayList<VObject>(entryBlocks.size());

        for (ObjectBlock entryBlock : entryBlocks)
        {
            entryObjects.add(entryBlock.toVObject(this));
        }

        return entryObjects;
    }

    public void clear()
    {
        try
        {
            block.setEntryCount(0);
        } catch (BlockFullException e)
        {
            // TODO Handle this
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
