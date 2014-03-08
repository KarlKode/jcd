package ethz.jcd.main.allocator;

import ethz.jcd.main.blocks.Block;

import java.util.Stack;

public class Allocator<T extends Stack<Block>>
{
    private T freeList;

    public Allocator(T init)
    {
        freeList = init;
    }

    public Block allocate()
    {
        return freeList.pop();
    }

    public void free(Block block)
    {
        freeList.push(block);
    }

    public boolean isFree(Block block)
    {
        return freeList.contains(block.getAddress());
    }
}
