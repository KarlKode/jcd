package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.ToDoException;
import org.junit.Test;

import static org.junit.Assert.*;

public class BlockTest
{
    @Test
    public void testGetAddress0() throws Exception
    {
        // Default value for integer
        int blockAddress = 0;
        Block block = new Block();
        assertEquals(blockAddress, block.getAddress());
    }

    @Test
    public void testGetAddress1() throws Exception
    {
        int blockAddress = 1024;
        Block block = new Block(blockAddress);
        assertEquals(blockAddress, block.getAddress());
    }

    @Test
    public void testGetAddress2() throws Exception
    {
        int blockAddress = 1024;
        Block block = new Block(blockAddress, null);
        assertEquals(blockAddress, block.getAddress());
    }

    @Test
    public void testSetAddress() throws Exception
    {
        int blockAddress = 1024;
        Block block = new Block();
        blockAddress = 0;
        block.setAddress(blockAddress);
        assertEquals(blockAddress, block.getAddress());
        blockAddress = 1024;
        block.setAddress(blockAddress);
        assertEquals(blockAddress, block.getAddress());
        blockAddress = Integer.MAX_VALUE;
        block.setAddress(blockAddress);
        assertEquals(blockAddress, block.getAddress());
    }

    @Test
    public void testGetBytes() throws Exception
    {
        throw new ToDoException( );
    }

    /**
     * Tests to BitMapBlock.java
     *
     */

    @Test
    public void testNextFreeBlockAddress() throws Exception
    {
        BitMapBlock block = new BitMapBlock(1, new byte[16]);
        block.setUsed(0);
        block.setUsed(3);
        block.setUsed(4);
        block.setUsed(5);
        block.setUsed(7);
        assertEquals(2, block.getNextFreeBlockAddress());
        assertEquals(6, block.getNextFreeBlockAddress());
        assertEquals(8, block.getNextFreeBlockAddress());
        block.setFree(5);
        assertEquals(5, block.getNextFreeBlockAddress());
        assertEquals(9, block.getNextFreeBlockAddress());
        block.setUsed(11);
        block.setFree(9);
        assertEquals(9, block.getNextFreeBlockAddress());
    }

    @Test
    public void testSetUsed() throws Exception
    {
        BitMapBlock block = new BitMapBlock(1, new byte[16]);
        block.setUsed(0);
        block.setUsed(2);
        assertTrue(block.isFree(4));
        assertTrue(block.isFree(15));
        assertFalse(block.isFree(0));
        assertFalse(block.isFree(1));
        assertFalse(block.isFree(2));
    }

    @Test
    public void testSetFree() throws Exception
    {
        BitMapBlock block = new BitMapBlock(1, new byte[16]);
        block.setUsed(0);
        block.setUsed(2);
        assertFalse(block.isFree(0));
        assertFalse(block.isFree(1));
        assertFalse(block.isFree(2));
        block.setFree(0);
        block.setFree(1);
        block.setFree(2);
        assertTrue(block.isFree(0));
        assertTrue(block.isFree(1));
        assertTrue(block.isFree(2));
    }

    @Test
    public void testMoreAddressesThenBlockSizeNeeded() throws Exception
    {
        throw new ToDoException( );
    }
}
