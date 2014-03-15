package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.BitMapBlock;
import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.SuperBlock;
import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.exceptions.InvalidBlockSizeException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class VUtilTest
{
    /**
     * General constants
     */
    private static final String VDISK_FILE = "/home/phgamper/.cache/vutilTest.vdisk";
    private static final int VDISK_SIZE = 1024;
    private static final int VDISK_BLOCK_SIZE = 16;
    /**
     * Test specific constants
     */
    private static final String NO_DISK_TEST_VDISK_FILE = "/home/test/.cache/filenotfound.vdisk";
    private static final String LOAD_TEST_VDISK_FILE= "/home/phgamper/.cache/vutilOne.vdisk";
    private static final String CREATE_TEST_VDISK_FILE= "/home/phgamper/.cache/vutilTwo.vdisk";
    private static final int READ_EMPTY_TEST_BLOCK_ADDRESS = 7;
    private static final int READ_TEST_BLOCK_ADDRESS = 31;
    private static final int WRITE_TEST_BLOCK_ADDRESS = 47;

    private static final byte[] SAMPE_BLOCK = new byte[] {'a', 'b', 'c', 'd',
                                                          'e', 'f', 'g', 'h',
                                                          'i', 'j', 'k', 'l',
                                                          'n', 'm', 'o', 'p'};

    private VUtil vUtil;

    @Before
    public void removeOldVDisk()
    {
        File f = new File(VDISK_FILE);
        if (f.exists())
        {
            f.delete();
        }

        f = new File(NO_DISK_TEST_VDISK_FILE);
        if (f.exists())
        {
            f.delete();
        }

        f = new File(CREATE_TEST_VDISK_FILE);
        if (f.exists())
        {
            f.delete();
        }
    }

    @Before
    public void setUp() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        vUtil = new VUtil(VDISK_FILE, VDISK_SIZE, VDISK_BLOCK_SIZE);
    }

    @Test(expected = FileNotFoundException.class)
    public void testVUtilFileNotFound( ) throws FileNotFoundException
    {
        new VUtil(NO_DISK_TEST_VDISK_FILE);
    }

    @Test
    public void testVUtilOneArgument( ) throws FileNotFoundException
    {
        VUtil vUtil1 = new VUtil(LOAD_TEST_VDISK_FILE);

        assertEquals(vUtil.getSuperBlock().getBlockSize(), vUtil1.getSuperBlock().getBlockSize());
        assertEquals(vUtil.getSuperBlock().getBlockCount(), vUtil1.getSuperBlock().getBlockCount());
        assertEquals(vUtil.getSuperBlock().getFirstBitMapBlock(), vUtil1.getSuperBlock().getFirstBitMapBlock());
        assertEquals(vUtil.getSuperBlock().getFirstDataBlock(), vUtil1.getSuperBlock().getFirstDataBlock());
        assertEquals(vUtil.getSuperBlock().getLastBitMapBlock(), vUtil1.getSuperBlock().getLastBitMapBlock());
        assertEquals(vUtil.getSuperBlock().getRootDirectoryBlock(), vUtil1.getSuperBlock().getRootDirectoryBlock());
        assertEquals(vUtil.getSuperBlock().getAddress(), vUtil1.getSuperBlock().getAddress());
        assertTrue(Arrays.equals(vUtil.getSuperBlock().getBytes(), vUtil1.getSuperBlock().getBytes()));
    }

    @Test(expected = InvalidSizeException.class)
    public void testVUtilTwoArgsInvalidSize( ) throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        VUtil vUtil1 = new VUtil(CREATE_TEST_VDISK_FILE, 0, 1024);
    }

    @Test(expected = InvalidBlockSizeException.class)
    public void testVUtilTwoArgsInvalidBlockSize( ) throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        VUtil vUtil1 = new VUtil(CREATE_TEST_VDISK_FILE, 1024, 8);
    }

    @Test
    public void testVUtilTwoArgs( ) throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        VUtil vUtil1 = new VUtil(CREATE_TEST_VDISK_FILE, 4096, 64);
        SuperBlock superBlock = vUtil1.getSuperBlock();
        assertTrue(superBlock.equals(vUtil1.read(0)));

        // TODO check BitMapBlock
    }


    @Test
    public void testVFSinitialization()
    {
        SuperBlock superBlock = vUtil.getSuperBlock();
        assertTrue(superBlock.equals(vUtil.read(0)));
        BitMapBlock bitMapBlock = vUtil.getBitMapBlock();
        assertTrue(bitMapBlock.equals(vUtil.read(superBlock.getFirstBitMapBlock())));
    }

    @Test
    public void readEmptyBlockTest()
    {
        Block expected = new Block(READ_EMPTY_TEST_BLOCK_ADDRESS, new byte[VDISK_BLOCK_SIZE]);
        Block actual = vUtil.read(READ_EMPTY_TEST_BLOCK_ADDRESS);
        assertTrue(expected.equals(actual));
    }

    @Test
    public void readBlockTest()
    {
        Block expected = new Block(READ_TEST_BLOCK_ADDRESS, SAMPE_BLOCK);
        vUtil.write(expected);
        Block actual = vUtil.read(READ_TEST_BLOCK_ADDRESS);
        assertTrue(expected.equals(actual));
    }


    @Test
    public void writeTest()
    {
        Block expected = new Block(WRITE_TEST_BLOCK_ADDRESS, SAMPE_BLOCK);
        vUtil.write(expected);
        Block actual = vUtil.read(WRITE_TEST_BLOCK_ADDRESS);
        assertTrue(expected.equals(actual));
    }
}