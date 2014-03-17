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
        byte[] bytes = null;
        Block block = new Block(bytes);
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
        byte[] bytes = null;
        Block block = new Block(bytes);
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
    public void testGetBytes0() throws Exception
    {
        byte[] bytes = null;
        Block block = new Block(bytes);
        assertEquals(null, block.getBytes());
    }

    @Test
    public void testGetBytes1() throws Exception
    {
        byte[] bytes = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        Block block = new Block(0, bytes);
        assertEquals(bytes, block.getBytes());
    }

    @Test
    public void testSetBytes0() throws Exception
    {
        byte[] bytes = null;
        Block block = new Block(bytes);
        bytes = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        block.setBytes(bytes);
        assertEquals(bytes, block.getBytes());
    }

    @Test
    public void testSetBytes1() throws Exception
    {
        byte[] bytes = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        Block block = new Block(bytes);
        assertEquals(bytes, block.getBytes());
    }
}
