package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.exceptions.DiskFullException;
import ch.ethz.jcd.main.exceptions.InvalidBlockSizeException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import org.junit.Before;
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
    private static final int VDISK_SIZE = 1024;
    private static final int VDISK_BLOCK_SIZE = 16;
    private static final int VDISK_BLOCK_COUNT = VDISK_SIZE / VDISK_BLOCK_SIZE;

    private VUtil vUtil;

    @Before
    public void removeOldVDisk()
    {
        File f = new File(VDISK_FILE);
        if (f.exists())
        {
            f.delete();
        }
    }

    @Before
    public void setUp()
    {
        try
        {
            vUtil = new VUtil(VDISK_FILE, VDISK_SIZE, VDISK_BLOCK_SIZE);

        }
        catch (InvalidSizeException invalidSize)
        {
            invalidSize.printStackTrace();
        }
        catch (InvalidBlockSizeException invalidBlockSize)
        {
            invalidBlockSize.printStackTrace();
        }
        catch (VDiskCreationException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testEmptyDisk( )
    {
        Allocator a = new Allocator(vUtil);

        for(int i = vUtil.getSuperBlock().getFirstDataBlock(); i < VDISK_BLOCK_COUNT; i++)
        {
            assertTrue(a.isFree(new Block(i)));
        }
    }

    @Test
    public void testAllocateBlock( ) throws DiskFullException
    {
        Allocator a = new Allocator(vUtil);
        a.format();
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