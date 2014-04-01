package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.ObjectBlock;
import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.utils.VDisk;

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
     * This Method recursively copies the VObject
     *
     * @param destination where to put the copied VObject
     */
    public abstract void copy(VDirectory destination) throws BlockFullException;

    /**
     * This Method recursively deletes the VObject
     */
    public abstract void delete();

    /**
     *
     * @return parent VDiretory of the VObject
     */
    public VDirectory getParent()
    {
        return parent;
    }

    /**
     *
     * @param parent VDirectory to set
     * @throws BlockFullException if
     */
    public void setParent(VDirectory parent) throws BlockFullException
    {
        // TODO prevent renaming of root directory

        if (parent != null)
        {
            parent.removeEntry(this); //TODO what?
            parent.addEntry(this);
        }
        this.parent = parent;
    }

    /**
     *
     * @return block name of the VObject
     * @throws IOException
     */
    public String getName()
    {
        return block.getName();
    }

    /**
     *
     * @param name of the block to set
     * @throws InvalidNameException if the given name is invalid
     */
    public void setName(String name) throws InvalidNameException
    {
        block.setName(name);
    }

    /**
     *
     * @return absolut path of the VObject
     */
    public String getPath()
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
