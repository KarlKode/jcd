package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.exceptions.ToDoException;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VFileTest
{
    private static final String PATH = "/usr/src/linux/.config";
    private static final String[] PATH_ARRAY = new String[] {"", "usr", "src", "linux", ".config" };
    private static final String FILE_NAME = PATH_ARRAY[PATH_ARRAY.length-1];
    private static final String PATH_2 = "/etc/conf.d/net/hostname";
    private static final String[] PATH_ARRAY_2 = new String[] {"", "etc", "conf.d", "net", "hostname" };
    private static final String FILE_NAME_2 = PATH_ARRAY_2[PATH_ARRAY_2.length-1];

    @Test
    public void testVInode()
    {
        VFile vInode = new VFile(PATH);
        assertEquals(PATH_ARRAY.length, vInode.getPathQueue().size());
        assertTrue(FILE_NAME.equals(vInode.getName()));
        assertTrue(Arrays.equals(PATH_ARRAY, vInode.getPathQueue().toArray()));
    }

    @Test
    public void testGetSize( )
    {
        throw new ToDoException();
    }

    @Test
    public void testContent( )
    {
        throw new ToDoException();
    }
}
