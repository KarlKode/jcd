package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockAddressOutOfBoundException;
import ch.ethz.jcd.main.exceptions.DiskFullException;
import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static org.junit.Assert.*;

public class MutliBitMapBlockTest
{
    private static final int BLOCK_ADDRESS = 1;
    private FileManager fileManager;
    private BitMapBlock block;
    private int blockCount = 2*8*VUtil.BLOCK_SIZE;

    private int SUPERBLOCK_AND_ROOTBLOCK = 2;

    @Before
    public void setUp() throws Exception
    {
        File tmpFile = File.createTempFile("test", "vfs");
        tmpFile.deleteOnExit();
        RandomAccessFile d = new RandomAccessFile(tmpFile.getAbsoluteFile(), "rw");
        d.setLength(blockCount);
        fileManager = new FileManager(tmpFile);
        block = new BitMapBlock(fileManager, BLOCK_ADDRESS, blockCount);
    }

    @Test
    public void testConstructor() throws Exception
    {
        new BitMapBlock(fileManager, 0, blockCount);
        new Block(fileManager, BLOCK_ADDRESS);
        new Block(fileManager, Integer.MAX_VALUE);

        try
        {
            new Block(null, 0);
            fail("Exception was expected for invalid file manager");
        } catch (IllegalArgumentException e)
        {
        }
        try
        {
            new Block(fileManager, -1);
            fail("Exception was expected for invalid block address");
        } catch (IllegalArgumentException e)
        {
        }
    }

    @Test
    public void testEquals()
    {
        assertFalse(block.equals(null));
        assertFalse(block.equals(new Object()));
        assertFalse(block.equals(new Block(fileManager, BLOCK_ADDRESS - 1)));
        assertTrue(block.equals(block));
        assertTrue(block.equals(new Block(fileManager, BLOCK_ADDRESS)));
    }

    @Test
    public void testInit()
    {
        try
        {
            this.block.initialize();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        assertTrue(this.block.getUsedBlocks() == block.getBitMapBlockCount() + SUPERBLOCK_AND_ROOTBLOCK);
        assertTrue(this.block.getFreeBlocks() == (block.getBitMapBlockCount()*VUtil.BLOCK_SIZE * 8) - (block.getBitMapBlockCount() + SUPERBLOCK_AND_ROOTBLOCK));
    }

    @Test
    public void testAllocateBlocks() throws Exception
    {
        for (int i = 0; i < (block.getBitMapBlockCount()*VUtil.BLOCK_SIZE * 8); i++)
        {
            assertTrue(block.getFreeBlocks() == (block.getBitMapBlockCount()*VUtil.BLOCK_SIZE * 8) - (i));
            assertTrue(block.getUsedBlocks() == (i));
            assertTrue(block.allocateBlock() == i);
            assertTrue(block.getFreeBlocks() == (block.getBitMapBlockCount()*VUtil.BLOCK_SIZE * 8)  - (i + 1));
            assertTrue(block.getUsedBlocks() == (i + 1));
        }

        try
        {
            System.out.println(block.allocateBlock());
            fail("Exception was expected since disk is full");
        } catch (DiskFullException e)
        {
        }
    }

    @Test
    public void testFreeBlocks() throws Exception
    {
        for (int i = 0; i < (block.getBitMapBlockCount()*VUtil.BLOCK_SIZE * 8); i++)
        {
            assertTrue(block.getFreeBlocks() == (block.getBitMapBlockCount()*VUtil.BLOCK_SIZE * 8)  - (i));
            assertTrue(block.getUsedBlocks() == (i));
            assertTrue(block.allocateBlock() == i);
            assertTrue(block.getFreeBlocks() == (block.getBitMapBlockCount()*VUtil.BLOCK_SIZE * 8)  - (i + 1));
            assertTrue(block.getUsedBlocks() == (i + 1));
        }

        for (int i = (block.getBitMapBlockCount()*VUtil.BLOCK_SIZE * 8)  - 1; i >= 0; i--)
        {
            block.setUnused(i);
            assertTrue(block.isUnused(i));
            assertTrue(block.getFreeBlocks() == (block.getBitMapBlockCount()*VUtil.BLOCK_SIZE * 8)  - (i));
            assertTrue(block.getUsedBlocks() == i);
        }

        try
        {
            block.setUnused(-1);
            fail("Exception was expected for invalid block address.");
        } catch (BlockAddressOutOfBoundException e)
        {
        }

        try
        {
            block.isUnused(-1);
            fail("Exception was expected for invalid block address.");
        } catch (BlockAddressOutOfBoundException e)
        {
        }
    }


    @Test
    public void testIsInvalidBlockAddress() throws Exception
    {
        assertEquals(false, block.isInvalidBlockAddress(0));
        assertEquals(false, block.isInvalidBlockAddress(BLOCK_ADDRESS));
        assertEquals(false, block.isInvalidBlockAddress(Integer.MAX_VALUE));
        assertEquals(true, block.isInvalidBlockAddress(-1));
        assertEquals(true, block.isInvalidBlockAddress(Integer.MIN_VALUE));
    }

    @Test
    public void testGetBlockOffset() throws Exception
    {
        assertEquals((long) BLOCK_ADDRESS * VUtil.BLOCK_SIZE, block.getBlockOffset());
    }
}
