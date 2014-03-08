package ethz.jcd.test;

import ethz.jcd.main.VDisk;
import ethz.jcd.main.exceptions.InvalidBlockSize;
import ethz.jcd.main.exceptions.InvalidSize;
import ethz.jcd.main.exceptions.VDiskCreationException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class VDiskCreate
{
    private String vDiskFile = "/tmp/test.vdisk";

    @Before
    public void removeOldVDisk()
    {
        File f = new File(vDiskFile);
        if (f.exists())
        {
            f.delete();
        }
    }

    @Test
    public void testValidCreation0() throws VDiskCreationException, InvalidSize, InvalidBlockSize
    {
        long size = 1024L;
        int blockSize = 1024; // 1 kB
        VDisk vd = new VDisk(vDiskFile, size * blockSize, blockSize);
    }

    // Size is checked before block size -> throws InvalidSize
    @Test(expected = InvalidSize.class)
    public void testInvalidBlockSize0() throws VDiskCreationException, InvalidSize, InvalidBlockSize
    {
        long size = 1024L;
        int blockSize = 0;
        VDisk vd = new VDisk(vDiskFile, size * blockSize, blockSize);
    }

    @Test(expected = InvalidBlockSize.class)
    public void testInvalidBlockSize1() throws VDiskCreationException, InvalidSize, InvalidBlockSize
    {
        long size = -1L; // -1 to make size % blockSize == 0
        int blockSize = -1;
        VDisk vd = new VDisk(vDiskFile, size * blockSize, blockSize);
    }

    @Test(expected = InvalidSize.class)
    public void testInvalidSize0() throws VDiskCreationException, InvalidSize, InvalidBlockSize
    {
        long size = 0L;
        int blockSize = 1024;
        VDisk vd = new VDisk(vDiskFile, size, blockSize);
    }

    @Test(expected = InvalidSize.class)
    public void testInvalidSize1() throws VDiskCreationException, InvalidSize, InvalidBlockSize
    {
        long size = -1L;
        int blockSize = 1024;
        VDisk vd = new VDisk(vDiskFile, size * blockSize, blockSize);
    }

    @Test(expected = InvalidSize.class)
    public void testInvalidSize2() throws VDiskCreationException, InvalidSize, InvalidBlockSize
    {
        long size = 1L;
        int blockSize = 1024;
        VDisk vd = new VDisk(vDiskFile, size * blockSize, blockSize);
    }
}