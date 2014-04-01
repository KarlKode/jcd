package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.ObjectBlock;
import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.utils.VDisk;

import java.io.IOException;

public class VObject<T extends ObjectBlock>
{
    protected VDirectory parent;
    protected T block;

    public VObject(T block, VDirectory parent)
    {
        this.block = block;
        this.parent = parent;
    }

    public VDirectory getParent()
    {
        return parent;
    }

    public void setParent(VDirectory parent) throws IOException, BlockFullException
    {
        // TODO prevent renaming of root directory

        if (parent != null)
        {
            parent.removeEntry(this);
            parent.addEntry(this);
        }
        this.parent = parent;
    }

    public String getName() throws IOException
    {
        return block.getName();
    }

    public void setName(String name) throws InvalidNameException, IOException
    {
        block.setName(name);
    }

    public String getPath() throws IOException
    {
        if (parent != null) {
            return parent.getPath() + VDisk.PATH_SEPARATOR + getName();
        }

        return VDisk.PATH_SEPARATOR + getName();
    }

    public ObjectBlock getBlock()
    {
        return block;
    }
}
