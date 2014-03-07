package ethz.jcd.test;

import ethz.jcd.main.allocator.Allocator;
import ethz.jcd.main.blocks.Block;
import ethz.jcd.main.blocks.File;
import ethz.jcd.main.blocks.Inode;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by phgamper on 3/7/14.
 */
public class AllocatorTest
{
    @BeforeClass
    public static void setUp( )
    {

    }

    @Test
    public void freeTest( )
    {
        List<Integer> freelist = new LinkedList<Integer>();

        for(int i = 0; i < 1024; i ++)
        {
            freelist.add(i);
        }

        Inode i = new File();
        Allocator<List<Integer>> allocator = new Allocator<List<Integer>>(freelist);

        //int blockAddresses = allocator.allocate(i);

        assertFalse(allocator.isFree(i));
        allocator.free(i);
        assertTrue(allocator.isFree(i));
    }

    @Test
    public void allocateTest( )
    {
        List<Integer> freelist = new LinkedList<Integer>();

        for(int i = 0; i < 1024; i ++)
        {
            freelist.add(i);
        }

        int blockAddress = 50;
        Block b = new Block(blockAddress);
        Allocator<List<Integer>> allocator = new Allocator<List<Integer>>(freelist);

        assertTrue(allocator.isFree(b));
        allocator.allocate();
        assertFalse(allocator.isFree(b));
    }
}
