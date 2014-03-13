package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.exceptions.InvalidBlockSizeException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class VDiskTest
{
    private String vDiskFile = "/tmp/test.vdisk";

    @Before
    public void removeOldVDisk() throws Exception
    {
        File f = new File(vDiskFile);
        if (f.exists())
        {
            f.delete();
        }
    }

    @Test
    public void testValidCreation0() throws Exception
    {
        long size = 1024L;
        int blockSize = 1024; // 1 kB
        VDisk vd = new VDisk(vDiskFile, size * blockSize, blockSize);
    }

    // Size is checked before block size -> throws InvalidSizeException
    @Test(expected = InvalidSizeException.class)
    public void testInvalidBlockSize0() throws Exception
    {
        long size = 1024L;
        int blockSize = 0;
        VDisk vd = new VDisk(vDiskFile, size * blockSize, blockSize);
    }

    @Test(expected = InvalidBlockSizeException.class)
    public void testInvalidBlockSize1() throws Exception
    {
        long size = -1L; // -1 to make size % blockSize == 0
        int blockSize = -1;
        VDisk vd = new VDisk(vDiskFile, size * blockSize, blockSize);
    }

    @Test(expected = InvalidSizeException.class)
    public void testInvalidSize0() throws Exception
    {
        long size = 0L;
        int blockSize = 1024;
        VDisk vd = new VDisk(vDiskFile, size, blockSize);
    }

    @Test(expected = InvalidSizeException.class)
    public void testInvalidSize1() throws Exception
    {
        long size = -1L;
        int blockSize = 1024;
        VDisk vd = new VDisk(vDiskFile, size * blockSize, blockSize);
    }

    @Test(expected = InvalidSizeException.class)
    public void testInvalidSize2() throws Exception
    {
        long size = 1L;
        int blockSize = 1024;
        VDisk vd = new VDisk(vDiskFile, size * blockSize, blockSize);
    }
}