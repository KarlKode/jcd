package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.ObjectBlock;
import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
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
     * This Method recursively copies the VObject
     *
     * @param vUtil used to allocate Blocks
     * @param destination where to put the copied VObject
     * @throws BlockFullException
     * @throws IOException
     */
    public abstract void copy(VUtil vUtil, VDirectory destination) throws BlockFullException, IOException;

    /**
     * This Method recursively deletes the VObject
     *
     * @param vUtil used to free the corresponding Blocks
     * @throws IOException
     */
    public abstract void delete(VUtil vUtil) throws IOException;

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
    public void setParent(VDirectory parent) throws BlockFullException, IOException
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
     * @return absolut path of the VObject
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
