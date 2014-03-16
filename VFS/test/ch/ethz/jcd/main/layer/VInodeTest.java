package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.DirectoryBlock;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class VInodeTest
{
    private static final String PATH = "/usr/src/linux/.config";
    private static final String[] PATH_ARRAY = new String[] {"", "usr", "src", "linux", ".config" };
    private static final String FILE_NAME = PATH_ARRAY[PATH_ARRAY.length-1];
    private static final String DIR_PATH = "/etc/conf.d/net/";
    private static final String[] DIR_PATH_ARRAY = new String[] {"", "etc", "conf.d", "net" };
    private static final String DIR_NAME = DIR_PATH_ARRAY[DIR_PATH_ARRAY.length-1];
    private static final String PATH_BAD = "/usr/share/junit/lib/";
    private static final String[] PATH_BAD_ARRAY = new String[] {"", "usr", "share", "junit", "lib" };
    private static final String DIR_NAME_BAD = PATH_BAD_ARRAY[PATH_BAD_ARRAY.length-1];

    @Test
    public void testVInode()
    {
        VInode vInode = new VFile(PATH);
        assertEquals(PATH_ARRAY.length, vInode.getPathQueue().size());
        assertTrue(FILE_NAME.equals(vInode.getName()));
        assertTrue(Arrays.equals(PATH_ARRAY, vInode.getPathQueue().toArray()));

        vInode = new VDirectory(DirectoryBlock.ROOT_DIRECTORY_BLOCK_PATH);
        assertEquals(DirectoryBlock.ROOT_DIRECTORY_BLOCK_PATH.length(), vInode.getPathQueue().size());
        assertTrue(DirectoryBlock.ROOT_DIRECTORY_BLOCK_NAME.equals(vInode.getName()));
    }

    @Test
    public void testSetName( )
    {
        String name = ".config.bak";
        String[] path = PATH_ARRAY;

        VInode vInode = new VFile(PATH);
        assertTrue(FILE_NAME.equals(vInode.getName()));
        vInode.setName(name);
        path[path.length-1] = name;
        assertTrue(name.equals(vInode.getName()));
        assertTrue(Arrays.equals(PATH_ARRAY, vInode.getPathQueue().toArray()));
    }

    @Test
    public void testSetPath( )
    {
        VInode vInode = new VDirectory(DirectoryBlock.ROOT_DIRECTORY_BLOCK_PATH);
        assertEquals(DirectoryBlock.ROOT_DIRECTORY_BLOCK_PATH.length(), vInode.getPathQueue().size());
        assertTrue(DirectoryBlock.ROOT_DIRECTORY_BLOCK_NAME.equals(vInode.getName()));

        vInode.setPath(DIR_PATH);
        assertTrue(Arrays.equals(DIR_PATH_ARRAY, vInode.getPathQueue().toArray()));
        assertTrue(DIR_NAME.equals(vInode.getName()));

        vInode.setPath(PATH_BAD);
        assertTrue(Arrays.equals(PATH_BAD_ARRAY, vInode.getPathQueue().toArray()));
        assertTrue(DIR_NAME_BAD.equals(vInode.getName()));
    }

    @Test
    public void testSetPathAsList( )
    {
        VInode vInode = new VDirectory(DirectoryBlock.ROOT_DIRECTORY_BLOCK_PATH);
        assertEquals(DirectoryBlock.ROOT_DIRECTORY_BLOCK_PATH.length(), vInode.getPathQueue().size());
        assertTrue(DirectoryBlock.ROOT_DIRECTORY_BLOCK_NAME.equals(vInode.getName()));

        VDirectory dir = new VDirectory(DIR_PATH);

        vInode.setPath(dir.getPath());
        assertTrue(Arrays.equals(DIR_PATH_ARRAY, vInode.getPathQueue().toArray()));
        assertTrue(DIR_NAME.equals(vInode.getName()));
    }
}
