package ethz.jcd.layer;

import ethz.jcd.blocks.Inode;
import ethz.jcd.visitor.VTypeVisitor;

/**
 * Created by phgamper on 3/6/14.
 */
public class VFile extends VType
{
    protected int size;

    protected Byte content;

    @Override
    public <R, A> R accept(VTypeVisitor<R, A> visitor, A arg)
    {
        return visitor.file(this, arg);
    }

    public Inode create( )
    {
        return null;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }
}
