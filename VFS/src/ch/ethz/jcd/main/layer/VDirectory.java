package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.blocks.InodeBlock;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.visitor.VTypeVisitor;

import java.util.LinkedList;

public class VDirectory extends VType
{
    protected LinkedList<VType> content = new LinkedList<VType>();

    public VDirectory(String path)
    {
        super(path);
    }

    public void add(VType type)
    {
        content.add(type);
    }

    public LinkedList<VType> list()
    {
        return content;
    }

    @Override
    public <R, A> R accept(VTypeVisitor<R, A> visitor, A arg)
    {
        return visitor.directory(this, arg);
    }

    @Override
    public InodeBlock toBlock(Block block) throws InvalidNameException
    {
        return new DirectoryBlock(block, this.name);
    }
}
