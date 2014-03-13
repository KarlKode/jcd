package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.exceptions.DiskFullException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class AllocatorTest
{
    /**
     * General constants
     */
    private static final String VDISK_FILE = "/tmp/allocatorTest.vdisk";
    private static final int VDISK_SIZE = 1024;
    private static final int VDISK_BLOCK_SIZE = 16;
    private static final int VDISK_BLOCK_COUNT = VDISK_SIZE / VDISK_BLOCK_SIZE;

    private VUtil vUtil;

    @Before
    public void setUp() throws Exception
    {
        // Remove old disk if it exists
        File f = new File(VDISK_FILE);
        if (f.exists())
        {
            f.delete();
        }

        vUtil = new VUtil(VDISK_FILE, VDISK_SIZE, VDISK_BLOCK_SIZE);
    }

    @Test
    public void testConstructor( ) throws Exception
    {
        Allocator a = new Allocator(vUtil);
        assertEquals(2, a.getUsedBlocks());
    }

    @Test
    public void testAllocateBlock( ) throws DiskFullException
    {
        Allocator a = new Allocator(vUtil);
        Block b = a.allocate();
        assertFalse(a.isFree(b));
    }

    @Test(expected = DiskFullException.class)
    public void testAllocateOnFullDisk( ) throws DiskFullException
    {
        Allocator a = new Allocator(vUtil);
        for(int i = 0; i < VDISK_BLOCK_COUNT; i++)
        {
            a.allocate();
        }
    }

    @Test
    public void testFreeBlock( ) throws DiskFullException
    {
        Allocator a = new Allocator(vUtil);
        a.format();
        Block b = a.allocate();
        assertFalse(a.isFree(b));
        a.free(b);
        assertFalse(a.isFree(b));
    }
}