package ch.ethz.jcd.main.blocks;

import java.util.LinkedList;

public class BlockList<T extends Block> extends Block
{
    private LinkedList<T> blocks = new LinkedList<T>();

    public BlockList(Block b)
    {
        super(b);
    }

    public void add(T t)
    {
        blocks.add(t);
    }

    public int size()
    {
        return blocks.size();
    }

    public LinkedList<T> list()
    {
        return blocks;
    }
}
