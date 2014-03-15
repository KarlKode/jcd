package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.exceptions.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class VDiskTest
{
    private static final String vDiskFile = "/tmp/test.vdisk";

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

    @Test
    public void testCreate( ) throws DiskFullException, NoSuchFileOrDirectoryException, BlockFullException, InvalidNameException
    {

    }

    @Test(expected = DiskFullException.class)
    public void testCreateDiskFull( ) throws DiskFullException, NoSuchFileOrDirectoryException, BlockFullException, InvalidNameException
    {

    }

    @Test(expected = NoSuchFileOrDirectoryException.class)
    public void testCreateNoSuchFileOrDirectory( ) throws DiskFullException, NoSuchFileOrDirectoryException, BlockFullException, InvalidNameException
    {

    }

    @Test(expected = BlockFullException.class)
    public void testCreateBlockFull( ) throws DiskFullException, NoSuchFileOrDirectoryException, BlockFullException, InvalidNameException
    {

    }

    @Test(expected = InvalidNameException.class)
    public void testCreateDiskFullInvalidName( ) throws DiskFullException, NoSuchFileOrDirectoryException, BlockFullException, InvalidNameException
    {

    }

    @Test
    public void testDelete( ) throws NoSuchFileOrDirectoryException
    {

    }

    @Test(expected = NoSuchFileOrDirectoryException.class)
    public void testDeleteNoSuchFileOrDirectory( ) throws NoSuchFileOrDirectoryException
    {

    }

    @Test
    public void testMove( ) throws NoSuchFileOrDirectoryException, InvalidNameException
    {

    }

    @Test(expected = NoSuchFileOrDirectoryException.class)
    public void testMoveNoSuchFileOrDirectory( ) throws NoSuchFileOrDirectoryException, InvalidNameException
    {

    }

    @Test(expected = InvalidNameException.class)
    public void testMoveInvalidName( ) throws NoSuchFileOrDirectoryException, InvalidNameException
    {

    }

    @Test
    public void testList( ) throws NoSuchFileOrDirectoryException
    {

    }

    @Test(expected = NoSuchFileOrDirectoryException.class)
    public void testListNoSuchFileOrDirectory( ) throws NoSuchFileOrDirectoryException
    {

    }

    @Test
    public void testCopy( ) throws BlockFullException, NoSuchFileOrDirectoryException
    {

    }

    @Test(expected = NoSuchFileOrDirectoryException.class)
    public void testCopyNoSuchFileOrDirectory( ) throws BlockFullException, NoSuchFileOrDirectoryException
    {

    }

    @Test(expected = BlockFullException.class)
    public void testCopyBlockFull( ) throws BlockFullException, NoSuchFileOrDirectoryException
    {

    }

    //TODO import/export tests
}