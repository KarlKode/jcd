package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.InodeBlock;
import ch.ethz.jcd.main.visitor.VTypeVisitor;

import java.util.LinkedList;

public class VDirectory extends VType
{
    protected LinkedList<VType> content = new LinkedList<VType>();

    public void add(VType type)
    {
        content.add(type);
    }

    public LinkedList<VType> list()
    {
        return content;
    }

    @Override
    public InodeBlock create()
    {
        return null;
    }

    @Override
    public <R, A> R accept(VTypeVisitor<R, A> visitor, A arg)
    {
        return visitor.directory(this, arg);
    }
}