package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.BitMapBlock;
import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.SuperBlock;
import ch.ethz.jcd.main.exceptions.InvalidBlockSizeException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.Assert.*;

public class VUtilTest
{
    /**
     * General constants
     */
    private static final String VDISK_FILE = "/tmp/vutilTest.vdisk";
    private static final int VDISK_SIZE = 1024;
    private static final int VDISK_BLOCK_SIZE = 16;
    /**
     * Test specific constants
     */
    private static final String NO_DISK_TEST_VDISK_FILE = "/tmp/filenotfound.vdisk";
    private static final String CREATE_TEST_VDISK_FILE= "/tmp/vutilTwo.vdisk";
    private static final int READ_EMPTY_TEST_BLOCK_ADDRESS = 7;
    private static final int READ_TEST_BLOCK_ADDRESS = 31;
    private static final int WRITE_TEST_BLOCK_ADDRESS = 47;


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

    @Test(expected = FileNotFoundException.class)
    public void testVUtilFileNotFound( ) throws FileNotFoundException
    {
        new VUtil(NO_DISK_TEST_VDISK_FILE);
    }

    @Test
    public void testVUtilOneArgument( ) throws FileNotFoundException
    {
        VUtil vUtil1 = new VUtil("/tmp/vutilOne.vdisk");

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
    public void testVUtilTwoArgsInvalidSize( ) throws InvalidSizeException, InvalidBlockSizeException, VDiskCreationException, FileNotFoundException
    {
        VUtil vUtil1 = new VUtil("/tmp/vutilTwo.vdisk", 0, 1024);
    }

    @Test(expected = InvalidBlockSizeException.class)
    public void testVUtilTwoArgsInvalidBlockSize( ) throws InvalidSizeException, InvalidBlockSizeException, VDiskCreationException, FileNotFoundException
    {
        VUtil vUtil1 = new VUtil("/tmp/vutilTwo.vdisk", 1024, 8);
    }

    @Test
    public void testVUtilTwoArgs( ) throws InvalidSizeException, InvalidBlockSizeException, VDiskCreationException, FileNotFoundException
    {
        VUtil vUtil1 = new VUtil(CREATE_TEST_VDISK_FILE, 4096, 64);
        SuperBlock superBlock = vUtil1.getSuperBlock();
        assertTrue(superBlock.equals(vUtil1.read(0)));

        // TODO check BitMapBlock
    }


    @Test
    public void testVFSinitialization() throws FileNotFoundException
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
        assertEquals(expected, actual);
    }

    @Test
    public void readBlockTest()
    {
        ByteBuffer buf = ByteBuffer.allocate(VDISK_BLOCK_SIZE);
        buf.putInt(1);
        buf.putInt(2);
        buf.putInt(3);
        buf.putInt(4);
        Block expected = new Block(READ_TEST_BLOCK_ADDRESS, buf.array());
        vUtil.write(expected);
        Block actual = vUtil.read(READ_TEST_BLOCK_ADDRESS);
        assertEquals(expected, actual);
    }


    @Test
    public void writeTest()
    {
        ByteBuffer buf = ByteBuffer.allocate(VDISK_BLOCK_SIZE);
        buf.putChar('a');
        buf.putChar('b');
        buf.putChar('c');
        buf.putChar('d');
        Block expected = new Block(WRITE_TEST_BLOCK_ADDRESS, buf.array());
        vUtil.write(expected);
        Block actual = vUtil.read(WRITE_TEST_BLOCK_ADDRESS);
        assertEquals(expected, actual);
    }
}