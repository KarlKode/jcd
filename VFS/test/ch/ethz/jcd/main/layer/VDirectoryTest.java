package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.DirectoryBlock;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VDirectoryTest
{
    private static final String DIR_PATH = "/etc/conf.d/net/";
    private static final String[] DIR_PATH_ARRAY = new String[] {"", "etc", "conf.d", "net" };
    private static final String DIR_NAME = DIR_PATH_ARRAY[DIR_PATH_ARRAY.length-1];
    private static final String PATH_BAD = "/usr/share/junit/lib/";
    private static final String[] PATH_BAD_ARRAY = new String[] {"", "usr", "share", "junit", "lib" };
    private static final String DIR_NAME_BAD = PATH_BAD_ARRAY[PATH_BAD_ARRAY.length-1];

    @Test
    public void testVInode()
    {
        VDirectory vInode = new VDirectory(DIR_PATH);
        assertEquals(DIR_PATH_ARRAY.length, vInode.getPathQueue().size());
        assertTrue(DIR_NAME.equals(vInode.getName()));
        assertTrue(Arrays.equals(DIR_PATH_ARRAY, vInode.getPathQueue().toArray()));
        assertTrue(vInode.list().isEmpty());

        vInode = new VDirectory(DirectoryBlock.ROOT_DIRECTORY_BLOCK_PATH);
        assertEquals(DirectoryBlock.ROOT_DIRECTORY_BLOCK_PATH.length(), vInode.getPathQueue().size());
        assertTrue(DirectoryBlock.ROOT_DIRECTORY_BLOCK_NAME.equals(vInode.getName()));
        assertTrue(vInode.list().isEmpty());

        vInode = new VDirectory(PATH_BAD);
        assertEquals(PATH_BAD_ARRAY.length, vInode.getPathQueue().size());
        assertTrue(DIR_NAME_BAD.equals(vInode.getName()));
        assertTrue(Arrays.equals(PATH_BAD_ARRAY, vInode.getPathQueue().toArray()));
        assertTrue(vInode.list().isEmpty());
    }

    @Test
    public void testAdd( )
    {
        VDirectory vInode = new VDirectory(DIR_PATH);
        assertEquals(DIR_PATH_ARRAY.length, vInode.getPathQueue().size());
        assertTrue(DIR_NAME.equals(vInode.getName()));
        assertTrue(Arrays.equals(DIR_PATH_ARRAY, vInode.getPathQueue().toArray()));
        assertTrue(vInode.list().isEmpty());

        VDirectory dir = new VDirectory(PATH_BAD);
        assertEquals(PATH_BAD_ARRAY.length, dir.getPathQueue().size());
        assertTrue(DIR_NAME_BAD.equals(dir.getName()));
        assertTrue(Arrays.equals(PATH_BAD_ARRAY, dir.getPathQueue().toArray()));
        assertTrue(dir.list().isEmpty());

        vInode.add(new VFile("rc.log"));
        assertFalse(vInode.list().isEmpty());
        vInode.add(dir);
        assertEquals(2, vInode.list().size());

        LinkedList<String> newPath = dir.getPath( );
        newPath.removeLast();

        assertEquals(DIR_PATH_ARRAY.length, newPath.size());

        for(int i = 0; i < DIR_PATH_ARRAY.length; i++)
        {
            assertTrue(DIR_PATH_ARRAY[i].equals(newPath.get(i)));
        }
    }
}
