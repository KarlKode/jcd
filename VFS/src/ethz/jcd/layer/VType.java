package ethz.jcd.layer;

import ethz.jcd.blocks.Inode;
import ethz.jcd.visitor.VTypeVisitor;

/**
 * Created by phgamper on 3/6/14.
 */
public abstract class VType
{
    protected String name;

    protected Inode inode;

    public abstract <R, A> R accept(VTypeVisitor<R, A> visitor, A arg);

    public abstract Inode create( );

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
