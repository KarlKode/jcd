package ch.ethz.jcd.main.blocks;

import java.util.BitSet;

public class BitMapBlock extends Block
{
    private BitSet bitMap;

    public BitMapBlock(int blockAddress, byte[] bytes)
    {
        super(blockAddress, bytes);
        bitMap = BitSet.valueOf(block);
        this.setUsed(blockAddress);
    }

    public int getNextFreeBlockAddress()
    {
        int next = bitMap.nextClearBit(0);
        this.setUsed(next);
        return next;
    }

    public void setUsed(int blockAddress)
    {
        bitMap.set(blockAddress);
        bytes = bitMap.toByteArray();
    }

    public void setFree(int blockAddress)
    {
        bitMap.clear(blockAddress);
        bytes = bitMap.toByteArray();
    }

    public boolean isFree(int blockAddress)
    {
        return !bitMap.get(blockAddress);
    }
}
