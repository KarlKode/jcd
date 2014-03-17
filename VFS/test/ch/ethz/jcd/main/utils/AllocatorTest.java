package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.exceptions.*;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.*;

public class AllocatorTest
{
    /**
     * General constants
     */
    private static final String VDISK_FILE = "/tmp/allocatorTest.vdisk";
    private static final int VDISK_SIZE = 1024000;
    private static final int VDISK_BLOCK_SIZE = 256;
    private static final int VDISK_BLOCK_COUNT = VDISK_SIZE / VDISK_BLOCK_SIZE;

    private VUtil vUtil;

    private void setUp() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
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
    public void testConstructor() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        this.setUp();
        Allocator a = new Allocator(vUtil);
        assertEquals(3, a.getUsedBlocks());
    }

    @Test
    public void testAllocateBlock() throws DiskFullException, FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        this.setUp();
        Allocator a = new Allocator(vUtil);
        Block b = a.allocate();
        assertFalse(a.isFree(b));
    }

    @Test(expected = DiskFullException.class)
    public void testAllocateOnFullDisk() throws DiskFullException, FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        this.setUp();
        Allocator a = new Allocator(vUtil);
        for (int i = 0; i < VDISK_BLOCK_COUNT; i++)
        {
            a.allocate();
        }
    }

    @Test
    public void testFreeBlock() throws DiskFullException, FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        this.setUp();
        Allocator a = new Allocator(vUtil);
        Block b = a.allocate();
        assertFalse(a.isFree(b));
        a.free(b);
        assertTrue(a.isFree(b));
    }

    @Test
    public void testFormat() throws DiskFullException, FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        this.setUp();
        Allocator a = new Allocator(vUtil);
        for (int i = 0; i < 5; i++)
        {
            a.allocate();
        }
        assertFalse(a.isFree(new Block(5)));
        a.format();
        assertFalse(a.isFree(new Block(0)));
        assertFalse(a.isFree(new Block(1)));
        assertTrue(a.isFree(new Block(2)));
    }
}