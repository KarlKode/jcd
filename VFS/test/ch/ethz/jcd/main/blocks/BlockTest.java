package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.ToDoException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BlockTest
{
    @Test
    public void testGetAddress() throws Exception
    {
        int blockAddress;
        Block block;

        // Default value for integer
        blockAddress = 0;
        block = new Block(blockAddress, null);
        assertEquals(blockAddress, block.getAddress());

        blockAddress = 1024;
        block = new Block(blockAddress, null);
        assertEquals(blockAddress, block.getAddress());

        blockAddress = 1024;
        block = new Block(blockAddress, null);
        assertEquals(blockAddress, block.getAddress());
    }

    @Test
    public void testSetAddress() throws Exception
    {
        int blockAddress;
        Block block = new Block(0, null);
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
        byte[] bytes;
        Block block;

        block = new Block(0, null);
        assertEquals(null, block.getBytes());

        bytes = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        block = new Block(0, bytes);
        assertEquals(bytes, block.getBytes());
    }

    @Test
    public void testSetBytes() throws Exception
    {
        byte[] bytes;
        Block block;

        block = new Block(0, null);
        bytes = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        block.setBytes(bytes);
        assertEquals(bytes, block.getBytes());
    }

    @Test
    public void testGetByteArray() throws Exception
    {
        Block block = new Block(0, null);
        block.getByteArray();
        throw new ToDoException();
    }
}
