package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockAddressOutOfBoundException;
import org.junit.Test;

import java.util.BitSet;

import static org.junit.Assert.*;

public class BitMapBlockTest
{
    @Test
    public void testConstructor() throws Exception
    {
        int blockAddress = 0;
        byte[] bytes = new byte[16];

        BitMapBlock block = new BitMapBlock(blockAddress, bytes);
        assertEquals(blockAddress, block.getBlockAddress());
        assertEquals(bytes, block.getBytes());
        assertTrue(block.isUnused(blockAddress));
    }

    @Test
    public void testAllocateBlock() throws Exception
    {
        int[] usedBlocks = new int[]{0, 1, 3, 4};
        BitSet bitSet = new BitSet();
        for (int usedBlock : usedBlocks)
        {
            bitSet.set(usedBlock);
        }
        BitMapBlock block = new BitMapBlock(0, bitSet.toByteArray());
        int allocatedBlock = block.allocateBlock();

        // Hardcoded
        assertEquals(2, allocatedBlock);

        // BitSet stuff
        assertEquals(bitSet.nextClearBit(0), allocatedBlock);

        assertFalse(block.isUnused(allocatedBlock));
    }

    @Test
    public void testSetUsed() throws Exception
    {
        BitMapBlock block = new BitMapBlock(0, new byte[16]);

        block.setUsed(0);
        assertFalse(block.isUnused(0));

        block.setUsed(2);
        assertTrue(block.isUnused(1));
        assertFalse(block.isUnused(2));
    }

    @Test
    public void testSetFree() throws Exception
    {
        BitMapBlock block = new BitMapBlock(0, new byte[16]);

        // set used blocks
        block.setUsed(0);
        block.setUsed(1);
        block.setUsed(2);

        block.setUnused(1);
        assertFalse(block.isUnused(0));
        assertTrue(block.isUnused(1));
        assertFalse(block.isUnused(2));
    }

    @Test(expected = BlockAddressOutOfBoundException.class)
    public void testBlockAddressOutOfBound0() throws Exception
    {
        BitMapBlock block = new BitMapBlock(0, new byte[1]);
        block.setUsed(8);
    }

    @Test(expected = BlockAddressOutOfBoundException.class)
    public void testBlockAddressOutOfBound1() throws Exception
    {
        BitMapBlock block = new BitMapBlock(0, new byte[1]);
        block.setUnused(8);
    }

    @Test(expected = BlockAddressOutOfBoundException.class)
    public void testBlockAddressOutOfBound2() throws Exception
    {
        BitMapBlock block = new BitMapBlock(0, new byte[1]);
        block.isUnused(8);
    }
}
