package ethz.jcd.main.allocator;

import ethz.jcd.main.blocks.Block;
import ethz.jcd.main.blocks.Inode;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by phgamper on 3/6/14.
 */
public class Allocator<T extends List<Integer>>
{
    private T freeList;

    public Allocator(T init)
    {
        freeList = init;
    }

    public LinkedList<Block> allocate( Inode i )
    {
        return null;
    }
    public void free( Block block )
    {
        // TODO entsprechend block typ halt s züügs freigeh ;-)
    }

    public boolean isFree( Block block )
    {
        return false;
    }
}
