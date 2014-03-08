package ethz.jcd.main.layer;

import ethz.jcd.main.blocks.Inode;
import ethz.jcd.main.visitor.VTypeVisitor;

public abstract class VType
{
    protected String name;

    protected Inode inode;

    public abstract <R, A> R accept(VTypeVisitor<R, A> visitor, A arg);

    public abstract Inode create();

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Inode getInode()
    {
        return inode;
    }

    public void setInode(Inode inode)
    {
        this.inode = inode;
    }
}
