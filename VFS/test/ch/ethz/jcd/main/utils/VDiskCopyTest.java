package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.exceptions.InvalidBlockSize;
import ch.ethz.jcd.main.exceptions.InvalidSize;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

public class VDiskCopyTest
{
    /**
     * General constants
     */
    private static final String VDISK_FILE = "/tmp/copy.vdisk";
    private static final int VDISK_SIZE = 1024;
    private static final int VDISK_BLOCK_SIZE = 16;

    /**
     * Test specific constants
     */


    private VUtil vUtil;

    @Before
    public void setUp()
    {
        try
        {
            vUtil = new VUtil(VDISK_FILE, VDISK_SIZE, VDISK_BLOCK_SIZE);
        }
        catch (InvalidSize invalidSize)
        {
            invalidSize.printStackTrace();
        }
        catch (InvalidBlockSize invalidBlockSize)
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
    public void testCopyEmptyFile()
    {

    }

    @Test
    public void testCopyNonEmptyFile()
    {

    }

    @Test
    public void testCopyEmptyDirectory()
    {

    }

    @Test
    public void testCopyOnlyFilesDirectory()
    {

    }

    @Test
    public void testCopyDirectory()
    {

    }
}