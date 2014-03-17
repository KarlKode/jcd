package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.exceptions.ToDoException;
import ch.ethz.jcd.main.utils.ByteArray;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.Assert.*;

public class InodeBlockTest
{
    private static final int BLOCK_SIZE = 1024;

    private static final byte[] FILE_HEAD = new byte[] { InodeBlock.TYPE_FILE, 'f', 'i', 'l', 'e', '.', 't', 'x', 't' };
    private static final String FILE_NAME = "file.txt";

    private static final byte[] DIRECTORY_HEAD = new byte[] { InodeBlock.TYPE_DIRECTORY, 'h', 'o', 'm', 'e' };
    private static final String DIRECTORY_NAME = "home";

    private InodeBlock init(byte[] head)
    {
        ByteArray buf = new ByteArray(new byte[BLOCK_SIZE]);
        buf.put(0, head);
        return new InodeBlock(new Block(buf.getBytes()));
    }

    private InodeBlock initName(byte[] head, String name) throws InvalidNameException
    {
        ByteArray buf = new ByteArray(new byte[BLOCK_SIZE]);
        buf.put(0, head);
        return new InodeBlock(new Block(buf.getBytes()), name);
    }


    private InodeBlock initAddressList(byte[] head, byte[] list)
    {
        ByteArray buf = new ByteArray(new byte[BLOCK_SIZE]);
        buf.put(0, head);
        buf.put(InodeBlock.OFFSET_BLOCKS, list);
        return new InodeBlock(new Block(buf.getBytes()));
    }


    private InodeBlock initFull(byte[] head)
    {
        ByteArray buf = new ByteArray(new byte[BLOCK_SIZE]);
        buf.put(0, head);
        for(int i = InodeBlock.OFFSET_BLOCKS; i < BLOCK_SIZE; i++)
        {
            buf.put(i, Byte.MAX_VALUE);
        }
        return new InodeBlock(new Block(buf.getBytes()));
    }


    @Test
    public void testInodeBlockOneArg()
    {
        InodeBlock inode = init(FILE_HEAD);
        assertTrue(FILE_NAME.equals(inode.getName()));
        assertEquals(InodeBlock.TYPE_FILE, inode.getType());

        ByteArray buf = new ByteArray(new byte[128]);
        buf.put(InodeBlock.OFFSET_TYPE, InodeBlock.TYPE_FILE);
        buf.putString(InodeBlock.OFFSET_NAME, FILE_NAME);
        buf.putInt(InodeBlock.OFFSET_PARENT_BLOCK_ADDRESS, 65);
        inode = new InodeBlock(new Block(buf.getBytes()));
        assertEquals(65, inode.getParentBlockAddress());
    }

    @Test
    public void testInodeBlockTwoArgs() throws InvalidNameException
    {
        String filename = "inode.txt";
        InodeBlock inode = initName(FILE_HEAD, filename);
        assertEquals(filename, inode.getName());
        assertEquals(InodeBlock.TYPE_FILE, inode.getType());

        ByteArray buf = new ByteArray(new byte[128]);
        buf.put(InodeBlock.OFFSET_TYPE, InodeBlock.TYPE_FILE);
        buf.putString(InodeBlock.OFFSET_NAME, FILE_NAME);
        buf.putInt(InodeBlock.OFFSET_PARENT_BLOCK_ADDRESS, 65);
        inode = new InodeBlock(new Block(buf.getBytes()));
        assertEquals(65, inode.getParentBlockAddress());
    }

    @Test(expected = InvalidNameException.class)
    public void testInodeBlockTwoArgsInvalidName() throws InvalidNameException
    {
        String filename = null;
        InodeBlock inode = initName(FILE_HEAD, filename);
    }

    @Test
    public void testAddBlock( ) throws BlockFullException
    {
        Integer blockAddress = 1234;
        InodeBlock inode = init(FILE_HEAD);
        assertEquals(0, inode.getBlockAddressList().size());
        inode.add(new Block(blockAddress));
        assertEquals(1, inode.getBlockAddressList().size());
        assertEquals(blockAddress, inode.getBlockAddressList().get(0));
        InodeBlock expected = initAddressList(FILE_HEAD, ByteBuffer.allocate(4).putInt(blockAddress).array());
        assertTrue(Arrays.equals(expected.getBytes(), inode.getBytes()));
    }

    @Test(expected = BlockFullException.class)
    public void testAddBlockFull( ) throws BlockFullException
    {
        Integer blockAddress = 1234;
        InodeBlock inode = initFull(FILE_HEAD);
        inode.add(new Block(blockAddress));
    }

    @Test
    public void testAddAddress( ) throws BlockFullException
    {
        Integer blockAddress = 1234;
        InodeBlock inode = init(FILE_HEAD);
        assertEquals(0, inode.getBlockAddressList().size());
        inode.add(blockAddress);
        assertEquals(1, inode.getBlockAddressList().size());
        assertEquals(blockAddress, inode.getBlockAddressList().get(0));
        InodeBlock expected = initAddressList(FILE_HEAD, ByteBuffer.allocate(4).putInt(blockAddress).array());
        assertTrue(Arrays.equals(expected.getBytes(), inode.getBytes()));
    }

    @Test (expected = BlockFullException.class)
    public void testAddAddressFull( ) throws BlockFullException
    {
        Integer blockAddress = 1234;
        InodeBlock inode = initFull(FILE_HEAD);
        inode.add(blockAddress);
    }

    @Test
    public void testRemoveBlock( )
    {
        Integer blockAddress = 1234;
        InodeBlock inode = initAddressList(FILE_HEAD, ByteBuffer.allocate(4).putInt(blockAddress).array());
        assertEquals(1, inode.getBlockAddressList().size());
        assertEquals(blockAddress, inode.getBlockAddressList().get(0));
        inode.remove(new Block(blockAddress));
        assertEquals(0, inode.getBlockAddressList().size());
        InodeBlock expected = init(FILE_HEAD);
        assertTrue(Arrays.equals(expected.getBytes(), inode.getBytes()));
    }

    @Test
    public void testRemoveAddress( )
    {
        Integer blockAddress = 1234;
        InodeBlock inode = initAddressList(FILE_HEAD, ByteBuffer.allocate(4).putInt(blockAddress).array());
        assertEquals(1, inode.getBlockAddressList().size());
        assertEquals(blockAddress, inode.getBlockAddressList().get(0));
        inode.remove(blockAddress);
        assertEquals(0, inode.getBlockAddressList().size());
        InodeBlock expected = init(FILE_HEAD);
        assertTrue(Arrays.equals(expected.getBytes(), inode.getBytes()));
    }

    @Test
    public void testSize( )
    {
        InodeBlock block = init(FILE_HEAD);
        assertEquals(0, block.size());
        block = initFull(FILE_HEAD);
        assertEquals(0, block.size());
    }

    @Test
    public void testLoadLinkedBlocks( )
    {
        throw new ToDoException();
    }

    @Test
    public void testStoreLinkedBlocks( )
    {
        throw new ToDoException();
    }

    @Test
    public void testSetName( ) throws InvalidNameException
    {
        String name = "inode.txt";
        InodeBlock inode = init(FILE_HEAD);
        InodeBlock expected = init(FILE_HEAD);

        ByteArray buf = new ByteArray(expected.bytes.getBytes());
        buf.put(InodeBlock.OFFSET_TYPE, InodeBlock.TYPE_FILE);
        buf.put(InodeBlock.OFFSET_NAME, name.getBytes());
        inode.setName(name);

        assertTrue(name.equals(inode.getName()));
        assertTrue(Arrays.equals(buf.getBytes(), inode.getBytes()));
    }

    @Test(expected = InvalidNameException.class)
    public void testSetNameInvalidName( ) throws InvalidNameException
    {
        String name = "realyextremlyinvalidlongunreadableandunusablefilenamewithmorethen64digits.txt";
        InodeBlock inode = init(FILE_HEAD);
        inode.setName(name);
    }

    @Test
    public void testIsBlockFull( )
    {
        InodeBlock inode = init(FILE_HEAD);
        assertFalse(inode.isBlockFull());
        inode = initFull(FILE_HEAD);
        assertTrue(inode.isBlockFull());
    }

    @Test
    public void testIsEmpty( )
    {
        InodeBlock inode = init(FILE_HEAD);
        assertTrue(inode.isEmpty());
        inode = initFull(FILE_HEAD);
        assertFalse(inode.isEmpty());
    }

    @Test
    public void testIsFile( )
    {
        InodeBlock inode = init(DIRECTORY_HEAD);
        assertFalse(inode.isFile());
        inode = initFull(FILE_HEAD);
        assertTrue(inode.isFile());
    }

    @Test
    public void testIsDirectory( )
    {
        InodeBlock inode = init(FILE_HEAD);
        assertFalse(inode.isDirectory());
        inode = initFull(DIRECTORY_HEAD);
        assertTrue(inode.isDirectory());
    }

    @Test
    public void testSetParentBlockAddress( )
    {
        ByteArray buf = new ByteArray(new byte[128]);
        buf.put(InodeBlock.OFFSET_TYPE, InodeBlock.TYPE_FILE);
        buf.putString(InodeBlock.OFFSET_NAME, FILE_NAME);
        buf.putInt(InodeBlock.OFFSET_PARENT_BLOCK_ADDRESS, 65);
        InodeBlock inode = new InodeBlock(new Block(buf.getBytes()));
        assertEquals(65, inode.getParentBlockAddress());
        inode.setParentBlockAddress(127);
        buf.putInt(InodeBlock.OFFSET_PARENT_BLOCK_ADDRESS, 127);
        assertEquals(127, inode.getParentBlockAddress());
        assertTrue(Arrays.equals(buf.getBytes(), inode.getBytes()));
    }
}
