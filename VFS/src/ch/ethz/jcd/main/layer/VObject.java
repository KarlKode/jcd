package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.blocks.ObjectBlock;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.utils.VDisk;

public abstract class VObject<T extends ObjectBlock>
{
    protected VDirectory parent;
    protected T block;

    public VDirectory getParent()
    {
        return parent;
    }

    public void setParent(VDirectory parent)
    {
        // TODO prevent renaming of root directory

        if (parent != null)
        {
            parent.removeChild(this);
            parent.addChild(this);

            // TODO Check cast
            block.setParent((DirectoryBlock) parent.getBlock());
        }
        this.parent = parent;
    }

    public String getName()
    {
        return block.getName();
    }

    public void setName(String name) throws InvalidNameException
    {
        block.setName(name);
    }

    public String getPath()
    {
        if (parent != null) {
            return parent.getPath() + VDisk.PATH_SEPARATOR + getName();
        }

        return VDisk.PATH_SEPARATOR + getName();
    }

    public long getSize()
    {
        return block.getSize();
    }

    public ObjectBlock getBlock()
    {
        return block;
    }
}
