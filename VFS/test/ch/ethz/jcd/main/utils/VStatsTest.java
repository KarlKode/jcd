package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class VStatsTest
{
    public static long VDISK_BLOCK_COUNT = 2048;
    public final File vdiskFile = new File("data/vdisk.vdisk");

    @Before
    public void setUp()
            throws InvalidBlockAddressException, InvalidSizeException, InvalidBlockCountException, VDiskCreationException, IOException
    {
        VDisk.format(vdiskFile, VUtil.BLOCK_SIZE * VDISK_BLOCK_COUNT);
    }

    @Test
    public void testDiskSize()
            throws IOException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VStats vStats = vDisk.stats();
        assertEquals(VUtil.BLOCK_SIZE * VDISK_BLOCK_COUNT, vStats.diskSize());
    }

    @Test
    public void testFreeSpace( )
            throws IOException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VStats vStats = vDisk.stats();
        assertEquals(VUtil.BLOCK_SIZE * VDISK_BLOCK_COUNT, vStats.freeSpace());

        VDirectory home = vDisk.mkdir("home");
        VDirectory user = vDisk.mkdir(home, "user");
        VFile pdf = vDisk.touch(user, "test.pdf");
        assertEquals(VUtil.BLOCK_SIZE * (VDISK_BLOCK_COUNT - 3), vStats.freeSpace());

        vDisk.importFromHost(new File("data/simons_cat.jpg"), user);
        assertEquals(VUtil.BLOCK_SIZE * (VDISK_BLOCK_COUNT - 319), vStats.freeSpace());
    }

    @Test
    public void testUsedSpace( )
            throws IOException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VStats vStats = vDisk.stats();
        assertEquals(VUtil.BLOCK_SIZE * 0, vStats.usedSpace());

        VDirectory home = vDisk.mkdir("home");
        VDirectory user = vDisk.mkdir(home, "user");
        VFile pdf = vDisk.touch(user, "test.pdf");
        assertEquals(VUtil.BLOCK_SIZE * 3, vStats.usedBlocks());

        vDisk.importFromHost(new File("data/simons_cat.jpg"), user);
        assertEquals(VUtil.BLOCK_SIZE * 319, vStats.usedSpace());
    }

    @Test
    public void testFreeBlocks( )
            throws IOException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VStats vStats = vDisk.stats();
        assertEquals(VDISK_BLOCK_COUNT, vStats.freeBlocks());

        VDirectory home = vDisk.mkdir("home");
        VDirectory user = vDisk.mkdir(home, "user");
        VFile pdf = vDisk.touch(user, "test.pdf");
        assertEquals(VDISK_BLOCK_COUNT - 3, vStats.freeBlocks());

        vDisk.importFromHost(new File("data/simons_cat.jpg"), user);
        assertEquals(VDISK_BLOCK_COUNT - 319, vStats.freeBlocks());
    }

    @Test
    public void testUsedBlocks( )
            throws IOException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VStats vStats = vDisk.stats();
        assertEquals(VDISK_BLOCK_COUNT, vStats.usedBlocks());

        VDirectory home = vDisk.mkdir("home");
        VDirectory user = vDisk.mkdir(home, "user");
        VFile pdf = vDisk.touch(user, "test.pdf");
        assertEquals(3, vStats.usedBlocks());

        vDisk.importFromHost(new File("data/simons_cat.jpg"), user);
        assertEquals(319, vStats.usedBlocks());
    }
}

