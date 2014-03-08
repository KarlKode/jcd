package ethz.jcd.main.blocks;

import java.util.BitSet;

public class BitMapBlock extends Block
{
    private BitSet bitMap;

    public BitMapBlock(int blockAddress, byte[] bytes) {
        super(blockAddress, bytes);
        bitMap = BitSet.valueOf(block);
    }

    public int getNextFreeBlockAddress()
    {
        return bitMap.nextClearBit(0);
    }

    public void setUsed(int blockAddress) {
        bitMap.set(blockAddress);
        bytes = bitMap.toByteArray();
    }

    public void setFree(int blockAddress)
    {
        bitMap.clear(blockAddress);
        bytes = bitMap.toByteArray();
    }
}
