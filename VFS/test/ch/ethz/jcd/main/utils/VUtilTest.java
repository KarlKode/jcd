package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.BitMapBlock;
import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.blocks.SuperBlock;
import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.exceptions.InvalidBlockSizeException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VUtilTest
{
    /**
     * General constants
     */
    private static final String VDISK_FILE = "/home/phgamper/.cache/vutilTest.vdisk";
    private static final int VDISK_SIZE = 1024;
    private static final int VDISK_BLOCK_SIZE = 64;
    /**
     * Test specific constants
     */
    private static final String NO_DISK_TEST_VDISK_FILE = "/home/test/.cache/filenotfound.vdisk";
    private static final int READ_EMPTY_TEST_BLOCK_ADDRESS = 7;
    private static final int READ_TEST_BLOCK_ADDRESS = 31;
    private static final int WRITE_TEST_BLOCK_ADDRESS = 47;

    private static final byte[] SAMPLE_BLOCK = new byte[]
            {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'n', 'm', 'o', 'p'
                    , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                    , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                    , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    /**
     * constants to test if a disk is created correctly
     */
    private static final String CREATE_TEST_VDISK_FILE = "test/vutilTwo.vdisk";
    private static final int CREATE_TEST_VDISK_BLOCK_SIZE = 1024;
    private static final int CREATE_TEST_VDISK_BLOCK_COUNT = 256;
    private static final long CREATE_TEST_VDISK_SIZE = CREATE_TEST_VDISK_BLOCK_COUNT * CREATE_TEST_VDISK_BLOCK_SIZE;

    /**
     * constants to test if a existing disk is loaded correctly
     */
    private static final String LOAD_TEST_VDISK_FILE = "test/vutilOne.vdisk";
    private static final int LOAD_TEST_VDISK_BLOCK_SIZE = 1024;
    private static final int LOAD_TEST_VDISK_BLOCK_COUNT = 256;

    private VUtil vUtil;

    private void deleteFile(String filename)
    {
        File f = new File(filename);
        if (f.exists())
        {
            f.delete();
        }
    }

    private void setUp() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        this.deleteFile(VDISK_FILE);
        vUtil = new VUtil(VDISK_FILE, VDISK_SIZE, VDISK_BLOCK_SIZE);
    }

    @Test(expected = FileNotFoundException.class)
    public void testVUtilFileNotFound() throws FileNotFoundException
    {
        this.deleteFile(NO_DISK_TEST_VDISK_FILE);
        new VUtil(NO_DISK_TEST_VDISK_FILE);
    }

    @Test
    public void testVUtilOneArgument() throws FileNotFoundException
    {
        this.deleteFile(LOAD_TEST_VDISK_FILE);
        FDisk.fdisk(LOAD_TEST_VDISK_FILE, LOAD_TEST_VDISK_BLOCK_SIZE, LOAD_TEST_VDISK_BLOCK_COUNT);

        VUtil vUtil = new VUtil(LOAD_TEST_VDISK_FILE);
        SuperBlock superBlock = vUtil.getSuperBlock();
        BitMapBlock bitMapBlock = vUtil.getBitMapBlock();
        DirectoryBlock rootBlock = vUtil.getRootDirectoryBlock();

        assertEquals(LOAD_TEST_VDISK_BLOCK_SIZE, superBlock.getBlockSize());
        assertEquals(LOAD_TEST_VDISK_BLOCK_COUNT, superBlock.getBlockCount());

        assertEquals(3, bitMapBlock.getUsedBlocks());
        assertTrue(rootBlock.isDirectory());
        assertTrue(rootBlock.isEmpty());
    }

    @Test(expected = InvalidSizeException.class)
    public void testVUtilTwoArgsInvalidSize() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        this.deleteFile(CREATE_TEST_VDISK_FILE);
        new VUtil(CREATE_TEST_VDISK_FILE, 0, 1024);
    }

    @Test(expected = InvalidBlockSizeException.class)
    public void testVUtilTwoArgsInvalidBlockSize() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        this.deleteFile(CREATE_TEST_VDISK_FILE);
        new VUtil(CREATE_TEST_VDISK_FILE, 1024, 8);
    }

    @Test
    public void testVUtilTwoArgs() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        this.deleteFile(CREATE_TEST_VDISK_FILE);
        VUtil vUtil = new VUtil(CREATE_TEST_VDISK_FILE, CREATE_TEST_VDISK_SIZE, CREATE_TEST_VDISK_BLOCK_SIZE);
        SuperBlock superBlock = vUtil.getSuperBlock();
        BitMapBlock bitMapBlock = vUtil.getBitMapBlock();
        DirectoryBlock rootBlock = vUtil.getRootDirectoryBlock();

        assertEquals(CREATE_TEST_VDISK_BLOCK_SIZE, superBlock.getBlockSize());
        assertEquals(CREATE_TEST_VDISK_BLOCK_COUNT, superBlock.getBlockCount());

        assertEquals(3, bitMapBlock.getUsedBlocks());
        assertTrue(rootBlock.isDirectory());
        assertTrue(rootBlock.isEmpty());
    }


    @Test
    public void testVFSinitialization() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        this.deleteFile(CREATE_TEST_VDISK_FILE);
        VUtil vUtil = new VUtil(CREATE_TEST_VDISK_FILE, CREATE_TEST_VDISK_SIZE, CREATE_TEST_VDISK_BLOCK_SIZE);

        SuperBlock superBlock = vUtil.getSuperBlock();
        assertTrue(superBlock.equals(vUtil.read(SuperBlock.SUPER_BLOCK_ADDRESS)));

        BitMapBlock bitMapBlock = vUtil.getBitMapBlock();
        assertTrue(bitMapBlock.equals(vUtil.read(superBlock.getFirstBitMapBlock())));

        DirectoryBlock directoryBlock = vUtil.getRootDirectoryBlock();
        assertTrue(directoryBlock.equals(vUtil.read(superBlock.getFirstDataBlock())));
    }

    @Test
    public void readEmptyBlockTest() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        this.setUp();
        Block expected = new Block(READ_EMPTY_TEST_BLOCK_ADDRESS, new byte[VDISK_BLOCK_SIZE]);
        Block actual = vUtil.read(READ_EMPTY_TEST_BLOCK_ADDRESS);
        assertTrue(expected.equals(actual));
    }

    @Test
    public void readBlockTest() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        this.setUp();
        Block expected = new Block(READ_TEST_BLOCK_ADDRESS, SAMPLE_BLOCK);
        vUtil.write(expected);
        Block actual = vUtil.read(READ_TEST_BLOCK_ADDRESS);
        assertTrue(expected.equals(actual));
    }


    @Test
    public void writeTest() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        this.setUp();
        Block expected = new Block(WRITE_TEST_BLOCK_ADDRESS, SAMPLE_BLOCK);
        vUtil.write(expected);
        Block actual = vUtil.read(WRITE_TEST_BLOCK_ADDRESS);
        assertTrue(expected.equals(actual));
    }
}