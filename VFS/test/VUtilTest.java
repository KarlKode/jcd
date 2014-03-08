import ch.ethz.jcd.main.Config;
import ch.ethz.jcd.main.VUtil;
import ch.ethz.jcd.main.blocks.SuperBlock;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

public class VUtilTest
{

    @BeforeClass
    public static void setUp()
    {
    }

    @Test
    public void testVFSinitialization() throws FileNotFoundException
    {
        VUtil vutil = new VUtil("/tmp/initTest.vdisk");

        int blockCount = 512;
        int blockSize = 1024;
        int freeListSize = (int) Math.ceil(blockCount / (blockSize * 8)) * blockSize;
        int disksize = Config.VFS_SUPER_BLOCK_SIZE + freeListSize + blockCount * blockSize;

        SuperBlock root = vutil.getSuperBlock();

        assertEquals(blockSize, root.getBlockSize());

        assertEquals(blockCount, root.getBlockCount());

        assertEquals(Config.VFS_SUPER_BLOCK_SIZE, root.startOfFreeList());

        assertEquals(Config.VFS_SUPER_BLOCK_SIZE + freeListSize, root.startOfBlocks());

        byte[] freelist = new byte[freeListSize];

        //assertEquals(freelist, vutil.);

        //assertEquals(disksize, vutil.);
    }

    @Test
    public void readTest()
    {

    }

    @Test
    public void writeTest()
    {

    }

    @Test
    public void seekTest()
    {

    }
}