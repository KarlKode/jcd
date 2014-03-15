package ch.ethz.jcd.main.utils;

import java.nio.ByteBuffer;

/**
 * Created by phgamper on 3/14/14.
 *
 * This class provides an interface to operate on a byte array in a simple way.
 */
public class ByteArray
{
    byte[] bytes;

    /**
     * This constructor builds a new ByteArray containing the given byte[].
     * The size is according to the number of bytes passed in the first argument.
     *
     * @param bytes either an initialized byte array or
     */
    public ByteArray(byte[] bytes)
    {
        this.bytes = bytes;
    }

    /**
     * This method returns the capacity of containing byte[] bytes
     *
     * @return size of the ByteArray
     */
    public int size()
    {
        return bytes.length;
    }

    /**
     * This method initializes bytes
     */
    public void clearAll( )
    {
        bytes = new byte[bytes.length];
    }

    /**
     * This method initializes a single byte of the byte array at given index.
     *
     * @param index of byte to initialize
     */
    public void clearAt(int index)
    {
        bytes[index] = 0;
    }

    /**
     * This method initializes all bytes after a given index as well as the byte
     * at the index
     *
     * @param index of first byte to initialize
     */
    public void clear(int index)
    {
        this.clear(index, bytes.length - index);
    }

    /**
     * This method initializes a specified number of bytes from a given index.
     *
     * @param index first byte to initialize
     * @param number of bytes to initialize
     */
    public void clear(int index, int number)
    {
        for(int i = index; i < index + number; i++)
        {
            bytes[i] = 0;
        }
    }

    /**
     * This method puts a byte at a given index.
     *
     * @param index where to put
     * @param b byte to put
     */
    public void put(int index, byte b)
    {
        bytes[index] = b;
    }

    /**
     * This method puts a byte array starting at given index.
     *
     * @param index where to put
     * @param b bytes to put
     */
    public void put(int index, byte[] b)
    {
        for(int i = 0; i < b.length; i++)
        {
            bytes[i+index] = b[i];
        }
    }

    /**
     * This method puts an Integer starting at given index.
     *
     * @param index where to put
     * @param i integer to put
     */
    public void putInt(int index, int i)
    {
        byte[] buf = ByteBuffer.allocate(4).putInt(i).array();
        this.put(index, buf);
    }

    /**
     * This method puts a String starting at given index.
     *
     * @param index where to put
     * @param s String to put
     */
    public void putString(int index, String s)
    {
        this.put(index, s.getBytes());
    }

    /**
     * This method returns the byte at the given index
     *
     * @param index of byte to return
     * @return byte at given index
     */
    public byte get(int index)
    {
        return bytes[index];
    }

    /**
     * This method returns a byte array of length len starting at given index.
     *
     * @param index of first byte to return
     * @param len number of bytes to return
     * @return byte array of length len
     */
    public byte[] get(int index, int len)
    {
        byte[] buf = new byte[len];

        for(int i = 0; i < len; i++)
        {
            buf[i] = bytes[i+index];
        }

        return buf;
    }

    /**
     * This method returns an Integer at given index.
     *
     * @param index of where to get
     * @return Integer at given index
     */
    public int getInt(int index)
    {
        byte[] buf = this.get(index, 4);

        return ByteBuffer.wrap(buf).getInt();
    }

    /**
     * This method returns a String of length len starting at given index.
     *
     * @param index of where the String starts
     * @param len of the String to return
     * @return String at given index of length len
     */
    public String getString(int index, int len)
    {
        return new String(get(index,len));
    }

    /**
     * This method returns the whole byte array.
     *
     * @return the whole byte array
     */
    public byte[] getBytes( )
    {
        return bytes;
    }

    /**
     * This method sets the byte array.
     *
     * @param bytes to set
     */
    public void setBytes(byte[] bytes)
    {
        this.bytes = bytes;
    }
}