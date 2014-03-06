package ethz.jcd.main.layer;

import ethz.jcd.main.blocks.Inode;
import ethz.jcd.main.visitor.VTypeVisitor;

import java.util.LinkedList;

/**
 * Created by phgamper on 3/6/14.
 */
public class VDirectory extends VType
{
    protected LinkedList<VType> content = new LinkedList<VType>();

    public void add(VType type)
    {
        content.add(type);
    }

    public LinkedList<VType> list( )
    {
        return content;
    }

    @Override
    public Inode create()
    {
        return null;
    }

    @Override
    public <R, A> R accept(VTypeVisitor<R, A> visitor, A arg)
    {
        return visitor.directory(this, arg);
    }
}
