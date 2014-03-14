package ch.ethz.jcd.main.utils;

import java.nio.ByteBuffer;

/**
 * Created by phgamper on 3/14/14.
 */
public class ByteArray
{
    byte[] bytes;

    public ByteArray(byte[] bytes)
    {
        this.bytes = bytes;
    }

    public int size()
    {
        return bytes.length;
    }

    public void clearAll( )
    {
        bytes = new byte[bytes.length];
    }

    public void clearAt(int index)
    {
        bytes[index] = 0;
    }

    public void clear(int index)
    {
        this.clear(index, bytes.length - index);
    }

    public void clear(int index, int len)
    {
        for(int i = index; i < index + len; i++)
        {
            bytes[i] = 0;
        }
    }

    public void put(int index, byte b)
    {
        bytes[index] = b;
    }

    public void put(int index, byte[] b)
    {
        for(int i = 0; i < b.length; i++)
        {
            bytes[i+index] = b[i];
        }
    }

    public void putInt(int index, int i)
    {
        byte[] buf = ByteBuffer.allocate(4).putInt(i).array();
        this.put(index, buf);
    }

    public void putString(String s, int index) throws IndexOutOfBoundsException
    {
        this.put(index, s.getBytes());
    }

    public byte get(int index)
    {
        return bytes[index];
    }

    public byte[] get(int index, int len) throws IndexOutOfBoundsException
    {
        byte[] buf = new byte[len];

        for(int i = 0; i < len; i++)
        {
            buf[i] = bytes[i+index];
        }

        return buf;
    }

    public int getInt(int index)
    {
        byte[] buf = this.get(index, 4);

        return ByteBuffer.wrap(buf).getInt();
    }

    public String getString(int index, int len)
    {
        return new String(get(index,len));
    }

    public byte[] getBytes( )
    {
        return bytes;
    }

    public void setBytes(byte[] bytes)
    {
        this.bytes = bytes;
    }
}