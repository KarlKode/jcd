package ethz.jcd.blocks;

import java.util.LinkedList;

/**
 * Created by phgamper on 3/6/14.
 */
public class BlockList<T extends Block> extends Block
{
    private LinkedList<T> blocks = new LinkedList<T>();

    public void add(T t)
    {
        blocks.add(t);
    }

    public int size( )
    {
        return blocks.size();
    }

    public LinkedList<T> list( )
    {
        return blocks;
    }
}
