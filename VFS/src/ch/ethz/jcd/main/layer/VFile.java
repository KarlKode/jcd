package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.FileBlock;
import ch.ethz.jcd.main.blocks.InodeBlock;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.visitor.VTypeVisitor;

public class VFile extends VType
{
    protected int size;

    protected Byte content;

    public VFile(String path)
    {
        super(path);
    }

    @Override
    public <R, A> R accept(VTypeVisitor<R, A> visitor, A arg)
    {
        return visitor.file(this, arg);
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
