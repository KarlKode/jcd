package ch.ethz.jcd.main.blocks;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

    }
}
