package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.InodeBlock;
import ch.ethz.jcd.main.visitor.VTypeVisitor;

public abstract class VType
{
    protected String name;

    protected InodeBlock inode;

    public abstract <R, A> R accept(VTypeVisitor<R, A> visitor, A arg);

    public abstract InodeBlock create();

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public InodeBlock getInode()
    {
        return inode;
    }

    public void setInode(InodeBlock inodeBlock)
    {
        this.inode = inodeBlock;
    }
}
