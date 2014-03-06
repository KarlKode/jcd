package ethz.jcd.test;

import ethz.jcd.main.VDisk;
import ethz.jcd.main.exceptions.InvalidBlockSize;
import ethz.jcd.main.exceptions.InvalidSize;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class VDiskOpenAndCreate
{
    private String vDiskFile = "/tmp/test.vdisk";

    @Before
    public void removeOldVDisk()
    {
        File f = new File(vDiskFile);
        if (f.exists()) {
            f.delete();
        }
    }

	@Test
	public void testValidCreation0()
    {
        long blockSize = 1024L; // 1 kB
        VDisk vd = new VDisk(vDiskFile, 1024L * blockSize, blockSize);
	}

    @Test(expected = InvalidBlockSize.class)
    public void testInvalidBlockSize0()
    {
        long blockSize = 0L;
        VDisk vd = new VDisk(vDiskFile, 1024L * blockSize, blockSize);
    }

    @Test(expected = InvalidBlockSize.class)
    public void testInvalidBlockSize1()
    {
        long blockSize = -1L;
        VDisk vd = new VDisk(vDiskFile, 1024L * blockSize, blockSize);
    }

    @Test(expected = InvalidSize.class)
    public void testInvalidSize0()
    {
        long blockSize = 1024L;
        VDisk vd = new VDisk(vDiskFile, 0L, blockSize);
    }

    @Test(expected = InvalidSize.class)
    public void testInvalidSize1()
    {
        long blockSize = 1024L;
        VDisk vd = new VDisk(vDiskFile, -1L * blockSize, blockSize);
    }

    @Test(expected = InvalidSize.class)
    public void testInvalidSize2()
    {
        long blockSize = 1024L;
        VDisk vd = new VDisk(vDiskFile, 1 * blockSize, blockSize);
    }
}