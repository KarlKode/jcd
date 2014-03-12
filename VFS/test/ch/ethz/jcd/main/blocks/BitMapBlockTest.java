package ch.ethz.jcd.main.blocks;

import org.junit.Test;

import static org.junit.Assert.*;

public class BitMapBlockTest
{
    @Test
    public void testNextFreeBlockAddress() throws Exception
    {
        BitMapBlock block = new BitMapBlock(1, new byte[16]);
        block.setUsed(0);
        block.setUsed(3);
        block.setUsed(4);
        block.setUsed(5);
        block.setUsed(7);
        assertEquals(2, block.allocateBlock());
        assertEquals(6, block.allocateBlock());
        assertEquals(8, block.allocateBlock());
        block.setUnused(5);
        assertEquals(5, block.allocateBlock());
        assertEquals(9, block.allocateBlock());
        block.setUsed(11);
        block.setUnused(9);
        assertEquals(9, block.allocateBlock());
    }

    @Test
    public void testSetUsed() throws Exception
    {
        BitMapBlock block = new BitMapBlock(1, new byte[16]);
        block.setUsed(0);
        block.setUsed(2);
        assertTrue(block.isUnused(4));
        assertTrue(block.isUnused(15));
        assertFalse(block.isUnused(0));
        assertFalse(block.isUnused(1));
        assertFalse(block.isUnused(2));
    }

    @Test
    public void testSetFree() throws Exception
    {
        BitMapBlock block = new BitMapBlock(1, new byte[16]);
        block.setUsed(0);
        block.setUsed(2);
        assertFalse(block.isUnused(0));
        assertFalse(block.isUnused(1));
        assertFalse(block.isUnused(2));
        block.setUnused(0);
        block.setUnused(1);
        block.setUnused(2);
        assertTrue(block.isUnused(0));
        assertTrue(block.isUnused(1));
        assertTrue(block.isUnused(2));
    }

    @Test
    public void testMoreAddressesThenBlockSizeNeeded()
    {
        BitMapBlock block = new BitMapBlock(1, new byte[4]);
        block.setUsed(0);
        block.setUsed(2);
        block.setUsed(3);
        assertEquals(4, block.allocateBlock());
    }
}
