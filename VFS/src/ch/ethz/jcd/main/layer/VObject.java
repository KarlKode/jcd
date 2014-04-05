package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.ObjectBlock;
import ch.ethz.jcd.main.exceptions.*;
import ch.ethz.jcd.main.utils.VDisk;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.IOException;

/**
 * Abstract representation of VFile and VDirectory. Provides the general
 * properties and functionality.
 *
 * @param <T> either a FileBlock or a DirectoryBlock
 */
public abstract class VObject<T extends ObjectBlock>
{
    protected VDirectory parent;
    protected T block;

    /**
     * Instantiation of a new VObject.
     *
     * @param block containing the byte structure of the VObject
     * @param parent of the VObject
     */
    public VObject(T block, VDirectory parent)
    {
        this.block = block;
        this.parent = parent; // TODO why we don't use the setter here?
    }

    /**
     * This method removes this object from its current parent and attach it to
     * the given directory. Short this object is moved to another location.
     *
     * @param parent VDirectory to set
     * @throws BlockFullException
     */
    public void move(VDirectory parent) throws BlockFullException, IOException
    {
        this.parent.removeEntry(this);

        if(parent != null)
        {
            parent.addEntry(this);
        }
    }

    /**
     * This Method recursively copies the VObject
     *
     * @param vUtil used to allocate Blocks
     * @param destination where to put the copied VObject
     * @return the root object of the copied structure
     * @throws BlockFullException
     * @throws IOException
     * @throws InvalidBlockAddressException
     * @throws DiskFullException
     * @throws InvalidBlockSizeException
     */
    public abstract VObject copy(VUtil vUtil, VDirectory destination) throws BlockFullException, IOException, InvalidBlockAddressException, DiskFullException, InvalidBlockSizeException, InvalidNameException;

    /**
     * This Method recursively deletes the VObject
     *
     * @param vUtil used to free the corresponding Blocks
     * @throws IOException
     */
    public abstract void delete(VUtil vUtil) throws IOException;

    /**
     * This method crops this object from its parent. Cropping an object from
     * its parent may lead to dead blocks, thus visibility is set to protected
     * to prevent abuse.
     */
    protected void crop( ) throws IOException
    {
        if(parent != null)
        {
            parent.block.removeEntry(block);
            parent = null;
        }
    }

    /**
     *
     * @return parent VDirectory of the VObject
     */
    public VDirectory getParent()
    {
        return parent;
    }

    /**
     *
     * @return block name of the VObject
     * @throws IOException
     */
    public String getName() throws IOException
    {
        return block.getName();
    }

    /**
     *
     * @param name of the block to set
     * @throws InvalidNameException if the given name is invalid
     */
    public void setName(String name) throws InvalidNameException, IOException
    {
        block.setName(name);
    }

    /**
     *
     * @return absolute path of the VObject
     */
    public String getPath() throws IOException
    {
        if (parent != null) {
            return parent.getPath() + VDisk.PATH_SEPARATOR + getName();
        }

        return VDisk.PATH_SEPARATOR + getName();
    }

    /**
     *
     * @return underlying ObjectBlock either a FileBlock or a DirectoryBlock of the VObject
     */
    public ObjectBlock getBlock()
    {
        return block;
    }
}
