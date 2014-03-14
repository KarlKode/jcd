package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.exceptions.ToDoException;
import org.junit.Test;

import java.nio.ByteBuffer;

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
        ByteBuffer buf = ByteBuffer.wrap(new byte[BLOCK_SIZE]);
        buf.put(head);
        return new InodeBlock(new Block(buf.array()));
    }

    private InodeBlock initAddressList(byte[] head, byte[] list)
    {
        ByteBuffer buf = ByteBuffer.wrap(new byte[BLOCK_SIZE]);
        buf.put(head);
        byte[] filler = new byte[InodeBlock.OFFSET_BLOCKS - head.length];
        buf.put(filler);
        buf.put(list);
        return new InodeBlock(new Block(buf.array()));
    }


    private InodeBlock initFull(byte[] head)
    {
        ByteBuffer buf = ByteBuffer.wrap(new byte[BLOCK_SIZE]);
        buf.put(head);
        for(int i = InodeBlock.OFFSET_BLOCKS; i < BLOCK_SIZE; i++)
        {
            buf.put(i, Byte.MAX_VALUE);
        }
        return new InodeBlock(new Block(buf.array()));
    }


    @Test
    public void testInodeBlockOneArg()
    {
        InodeBlock inode = init(FILE_HEAD);
        assertTrue(FILE_NAME.equals(inode.getName()));
        assertEquals(InodeBlock.TYPE_FILE, inode.getType());
    }

    @Test
    public void testInodeBlockTwoArgs() throws InvalidNameException
    {
        String filename = "inode.txt";
        ByteBuffer buf = ByteBuffer.wrap(new byte[BLOCK_SIZE]);
        buf.put(FILE_HEAD);

        InodeBlock inode = new InodeBlock(new Block(buf.array()), filename);

        assertEquals(filename, inode.getName());
        assertEquals(InodeBlock.TYPE_FILE, inode.getType());
    }

    @Test(expected = InvalidNameException.class)
    public void testInodeBlockTwoArgsInvalidName() throws InvalidNameException
    {
        String filename = null;
        ByteBuffer buf = ByteBuffer.wrap(new byte[BLOCK_SIZE]);
        buf.put(FILE_HEAD);

        InodeBlock inode = new InodeBlock(new Block(buf.array()), filename);
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
        assertEquals(expected.getBytes(), inode.getBytes());
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
        assertEquals(expected.getBytes(), inode.getBytes());
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
        assertEquals(expected.getBytes(), inode.getBytes());
    }

    @Test
    public void testRemoveAddress( )
    {
        Integer blockAddress = 1234;
        InodeBlock inode = initAddressList(FILE_HEAD, ByteBuffer.allocate(4).putInt(blockAddress).array());
        assertEquals(1, inode.getBlockAddressList().size());
        inode.remove(blockAddress);
        assertEquals(0, inode.getBlockAddressList().size());
        assertEquals(blockAddress, inode.getBlockAddressList().get(0));
        InodeBlock expected = init(FILE_HEAD);
        assertEquals(expected.getBytes(), inode.getBytes());
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
        ByteBuffer buf = ByteBuffer.wrap(expected.bytes.getBytes());
        buf.put(InodeBlock.TYPE_FILE);
        buf.put(name.getBytes(), 0, name.getBytes().length);
        inode.setName(name);
        assertTrue(name.equals(inode.getName()));
        assertEquals(buf.array(), inode.getBytes());
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
}
