package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.blocks.ObjectBlock;
import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.DiskFullException;
import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.exceptions.InvalidBlockSizeException;
import ch.ethz.jcd.main.utils.VUtil;

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
     * @param vUtil used to allocate Blocks
     * @param destination where to put the copied VDirectory
     * @throws BlockFullException
     * @throws IOException
     * @throws InvalidBlockAddressException
     * @throws DiskFullException
     * @throws InvalidBlockSizeException
     */
    @Override
    public void copy(VUtil vUtil, VDirectory destination) throws BlockFullException, IOException, InvalidBlockAddressException, DiskFullException, InvalidBlockSizeException
    {
        DirectoryBlock directoryBlock = vUtil.allocateDirectoryBlock();
        VDirectory copy = new VDirectory(directoryBlock, destination);

        for(VObject<ObjectBlock> obj : this.getEntries())
        {
            obj.copy(vUtil, copy);
        }
        destination.addEntry(copy);
    }

    /**
     * This Method recursively deletes the VDirectory
     *
     * @param vUtil used to free the corresponding Blocks
     * @throws BlockFullException
     * @throws IOException
     */
    @Override
    public void delete(VUtil vUtil) throws IOException
    {
        for(VObject<ObjectBlock> obj : this.getEntries())
        {
            obj.delete(vUtil);
        }
        vUtil.free(block);
    }

    /**
     * Deletes all the entries this directory contains
     */
    public void clear(VUtil vUtil) throws IOException
    {
        for(VObject object : this.getEntries())
        {
            this.removeEntry(object);
            object.delete(vUtil);
        }
    }

    /**
     * This method adds either a file or a directory to this directory. It first
     * crops the given entry from its current parent and then attach it to this
     * directory.
     *
     * @param entry to add
     * @throws BlockFullException
     * @throws IOException
     */
    public void addEntry(VObject entry) throws BlockFullException, IOException
    {
        //TODO throw invalid name / duplicate exception

        entry.crop();
        entry.parent = this;
        block.addEntry(entry.getBlock());
    }

    /**
     * This method removes either a file or a directory from this directory
     *
     * WARNING: removing an entry without moving oder deleting it leads to
     * unlinked blocks. Make sure you either relink or free the affected blocks
     * when using this method.
     *
     * @param entry to remove
     * @throws IOException
     */
    public void removeEntry(VObject entry) throws IOException
    {
        entry.crop( );
    }

    /**
     * This method searches for an entry by name.
     *
     * @param name of object to return
     * @return either the object or null if not found
     * @throws IOException
     */
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

    /**
     * Removes all the entries this directory contains
     *
     * WARNING: removing entries without moving oder deleting it leads to
     * unlinked blocks. Make sure you either relink or free the affected blocks
     * when using this method.
     *

     */
    public void removeAll() throws IOException
    {
        for(VObject object : this.getEntries())
        {
            object.crop();
        }
    }

    /**
     * This method returns all the object which this directory contains
     *
     * @return a list of all objects
     * @throws IOException
     */
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
}
