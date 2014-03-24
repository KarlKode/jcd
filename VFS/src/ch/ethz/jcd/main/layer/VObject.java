package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.ObjectBlock;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.utils.VDisk;

public abstract class VObject
{
    protected VDirectory parent;
    protected ObjectBlock block;

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
        }
        parent.addChild(this);
        this.parent = parent;
        block.setParent(parent.getBlock());
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

    public ObjectBlock getBlock()
    {
        return block;
    }
}
