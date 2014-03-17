package ch.ethz.jcd.main.utils;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ByteArrayTest
{
    /**
     * General constants
     */
    public static final int BLOCK_SIZE = 32;
    public static final byte[] INIT_BLOCK = new byte[BLOCK_SIZE];
    public static final String SOME_STRING = "ABCD";
    //TODO SOME_BYTES wird us irgend eme grund falsch initialisiert ... suscht wäred d teschts eigentli alli richtig
    public static final byte[] SOME_BYTES = new byte[]{'A', 'B', 'C', 'D'};
    public static final byte SINGLE_BYTE = 'Q';
    public static final int SOME_INT = 584930;
    public static final byte[] INT_AS_BYTE = ByteBuffer.allocate(4).putInt(SOME_INT).array();
    public static final String HELLO_WORLD_STRING = "hello world!";
    public static final byte[] FANCY_BYTES = new byte[]{0, SINGLE_BYTE, 0, 'A', 'B', 'C', 'D', 0, 0};


    @Test
    public void testConstructor()
    {
        ByteArray b = new ByteArray(INIT_BLOCK);
        assertTrue(Arrays.equals(INIT_BLOCK, b.getBytes()));
    }

    @Test
    public void testSize()
    {
        ByteArray b = new ByteArray(INIT_BLOCK);
        assertEquals(BLOCK_SIZE, b.size());
    }

    @Test
    public void testClearAll()
    {
        ByteArray b = new ByteArray(SOME_BYTES);
        b.clearAll();
        assertTrue(Arrays.equals(new byte[SOME_BYTES.length], b.getBytes()));
    }

    @Test
    public void testClearAt()
    {
        ByteArray b = new ByteArray(SOME_BYTES);
        b.clearAt(2);
        assertTrue(Arrays.equals(new byte[]{'A', 'B', 0, 'D'}, b.getBytes()));
    }

    @Test
    public void testClear()
    {
        ByteArray b = new ByteArray(SOME_BYTES);
        b.clear(1);
        assertTrue(Arrays.equals(new byte[]{'A', 0, 0, 0}, b.getBytes()));
    }

    @Test
    public void testClearRange()
    {
        ByteArray b = new ByteArray(SOME_BYTES);
        b.clear(1, 2);
        assertTrue(Arrays.equals(new byte[]{'A', 0, 0, 'D'}, b.getBytes()));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testClearAtIndexOutOfBounds()
    {
        ByteArray b = new ByteArray(SOME_BYTES);
        b.clearAt(5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testClearRangeIndexOutOfBounds()
    {
        ByteArray b = new ByteArray(SOME_BYTES);
        b.clear(1, 7);
    }

    @Test
    public void testPut()
    {
        ByteArray b = new ByteArray(SOME_BYTES);
        b.put(1, SINGLE_BYTE);
        assertTrue(Arrays.equals(new byte[]{'A', 'Q', 'C', 'D'}, b.getBytes()));
    }

    @Test
    public void testPutArray()
    {
        ByteArray b = new ByteArray(SOME_BYTES);
        b.put(2, new byte[2]);
        assertTrue(Arrays.equals(new byte[]{'A', 'B', 0, 0}, b.getBytes()));
    }

    @Test
    public void testPutInt()
    {
        ByteArray b = new ByteArray(SOME_BYTES);
        b.putInt(0, SOME_INT);
        assertTrue(Arrays.equals(INT_AS_BYTE, b.getBytes()));
    }

    @Test
    public void testPutString()
    {
        ByteArray b = new ByteArray(new byte[HELLO_WORLD_STRING.length()]);
        b.putString(0, HELLO_WORLD_STRING);
        assertTrue(Arrays.equals(HELLO_WORLD_STRING.getBytes(), b.getBytes()));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testPutIndexOutOfBounds()
    {
        ByteArray b = new ByteArray(SOME_BYTES);
        b.put(8, SINGLE_BYTE);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testPutArrayIndexOutOfBounds()
    {
        ByteArray b = new ByteArray(SOME_BYTES);
        b.put(3, new byte[2]);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testPutIntIndexOutOfBounds()
    {
        ByteArray b = new ByteArray(SOME_BYTES);
        b.putInt(3, SOME_INT);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testPutStringIndexOutOfBounds()
    {
        ByteArray b = new ByteArray(new byte[HELLO_WORLD_STRING.length()]);
        b.putString(1, HELLO_WORLD_STRING);
    }

    @Test
    public void testGet()
    {
        ByteArray b = new ByteArray(FANCY_BYTES);
        assertEquals(SINGLE_BYTE, b.get(1));
    }

    @Test
    public void testGetArray()
    {
        ByteArray b = new ByteArray(FANCY_BYTES);
        assertTrue(Arrays.equals(SOME_BYTES, b.get(3, 4)));
    }

    @Test
    public void testGetInt()
    {
        ByteArray b = new ByteArray(INT_AS_BYTE);
        assertEquals(SOME_INT, b.getInt(0));
    }

    @Test
    public void testGetString()
    {
        ByteArray b = new ByteArray(FANCY_BYTES);
        assertTrue(SOME_STRING.equals(b.getString(3, 4)));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetIndexOutOfBounds()
    {
        ByteArray b = new ByteArray(FANCY_BYTES);
        b.get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetArrayIndexOutOfBounds()
    {
        ByteArray b = new ByteArray(FANCY_BYTES);
        b.get(3, 14);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetIntIndexOutOfBounds()
    {
        ByteArray b = new ByteArray(INT_AS_BYTE);
        b.getInt(3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetStringIndexOutOfBounds()
    {
        ByteArray b = new ByteArray(FANCY_BYTES);
        b.getString(3, 27);
    }

    @Test
    public void testGetBytes()
    {
        ByteArray b = new ByteArray(FANCY_BYTES);
        assertTrue(Arrays.equals(FANCY_BYTES, b.getBytes()));
    }

    @Test
    public void testSetBytes()
    {
        ByteArray b = new ByteArray(FANCY_BYTES);
        b.setBytes(SOME_BYTES);
        assertTrue(Arrays.equals(SOME_BYTES, b.getBytes()));
    }
}