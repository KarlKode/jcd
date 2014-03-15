package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.exceptions.*;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class VDiskTest
{
    private static final String VDISK_FILE= "test/vutilOne.vdisk";
    private static final int VDISK_BLOCK_SIZE = 1024;
    private static final int VDISK_BLOCK_COUNT = 256;


    private static final String vDiskFile = "/tmp/test.vdisk";

    private void deleteFile(String filename)
    {
        File f = new File(filename);
        if (f.exists())
        {
            f.delete();
        }
    }

    private void setUp( )
    {
        this.deleteFile(VDISK_FILE);
        FDisk.fdisk(VDISK_FILE, VDISK_BLOCK_SIZE, VDISK_BLOCK_COUNT);
    }

    @Before
    public void removeOldVDisk()
    {
        deleteFile(vDiskFile);
    }

    @Test
    public void testValidCreation0() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        long size = 1024L;
        int blockSize = 1024; // 1 kB
        VDisk vd = new VDisk(vDiskFile, size * blockSize, blockSize);
    }

    // Size is checked before block size -> throws InvalidSizeException
    @Test(expected = InvalidSizeException.class)
    public void testInvalidBlockSize0() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        long size = 1024L;
        int blockSize = 0;
        VDisk vd = new VDisk(vDiskFile, size * blockSize, blockSize);
    }

    @Test(expected = InvalidBlockSizeException.class)
    public void testInvalidBlockSize1() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        long size = -1L; // -1 to make size % blockSize == 0
        int blockSize = -1;
        VDisk vd = new VDisk(vDiskFile, size * blockSize, blockSize);
    }

    @Test(expected = InvalidSizeException.class)
    public void testInvalidSize0() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        long size = 0L;
        int blockSize = 1024;
        VDisk vd = new VDisk(vDiskFile, size, blockSize);
    }

    @Test(expected = InvalidSizeException.class)
    public void testInvalidSize1() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        long size = -1L;
        int blockSize = 1024;
        VDisk vd = new VDisk(vDiskFile, size * blockSize, blockSize);
    }

    @Test(expected = InvalidSizeException.class)
    public void testInvalidSize2() throws FileNotFoundException, InvalidBlockSizeException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        long size = 1L;
        int blockSize = 1024;
        VDisk vd = new VDisk(vDiskFile, size * blockSize, blockSize);
    }

    @Test
    public void testCreate( ) throws DiskFullException, NoSuchFileOrDirectoryException, BlockFullException, InvalidNameException, FileNotFoundException
    {
        this.setUp();
        VDisk vDisk = new VDisk(VDISK_FILE);
        vDisk.create(new VFile("new.txt"), new VDirectory("/"));
        //TODO
    }

    @Test(expected = DiskFullException.class)
    public void testCreateDiskFull( ) throws DiskFullException, NoSuchFileOrDirectoryException, BlockFullException, InvalidNameException, FileNotFoundException
    {
        this.setUp();
        VDisk vDisk = new VDisk(VDISK_FILE);
        vDisk.create(new VFile("new.txt"), new VDirectory("/"));
        //TODO
    }

    @Test(expected = NoSuchFileOrDirectoryException.class)
    public void testCreateNoSuchFileOrDirectory( ) throws DiskFullException, NoSuchFileOrDirectoryException, BlockFullException, InvalidNameException, FileNotFoundException
    {
        this.setUp();
        VDisk vDisk = new VDisk(VDISK_FILE);
        vDisk.create(new VFile("new.txt"), new VDirectory("/home"));
    }

    @Test(expected = BlockFullException.class)
    public void testCreateBlockFull( ) throws DiskFullException, NoSuchFileOrDirectoryException, BlockFullException, InvalidNameException, FileNotFoundException
    {
        this.setUp();
        VDisk vDisk = new VDisk(VDISK_FILE);
        for(int i = 0; i < VDISK_BLOCK_COUNT; i++)
        {
            vDisk.create(new VFile(i+".txt"), new VDirectory("/"));
        }
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