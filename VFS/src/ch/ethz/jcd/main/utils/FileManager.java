package ch.ethz.jcd.main.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileManager
{
    protected final RandomAccessFile rand;

    public FileManager(File file) throws FileNotFoundException
    {
        this.rand = new RandomAccessFile(file, "rw");
    }

    public static void main(String[] args) throws IOException
    {
        File a = new File("/Users/leo/TestVDisk.txt");
        if (!a.exists())
        {
            a.createNewFile();
        }

        FileManager dd = new FileManager(a);

        int i1 = 5;
        String foo = "foo";


        dd.writeBytes(0, 4, foo.getBytes());
        dd.writeInt(0, 0, i1);

    }

    public int readInt(long address, int offset) throws IOException
    {
        rand.seek(address + offset);

        return rand.readInt();
    }

    public byte[] readBytes(long address, int offset, int length) throws IOException
    {
        final byte[] retValue = new byte[length];

        rand.seek(address + offset);
        rand.readFully(retValue);

        return retValue;
    }

    public String readString(long address, int offset, int length) throws IOException
    {
        rand.seek(address + offset);
        return rand.readUTF();
    }

    public void writeInt(long address, int offset, int value) throws IOException
    {
        rand.seek(address + offset);
        rand.writeInt(value);
    }

    public void writeBytes(long address, int offset, final byte[] value) throws IOException
    {
        rand.seek(address + offset);
        rand.write(value);
    }

    public void writeString(long address, int offset, String value) throws IOException
    {
        rand.seek(address + offset);
        //rand.writeChars(value);
        rand.writeUTF(value);
    }

    public void writeLong(long address, int offset, long value) throws IOException
    {
        rand.seek(address + offset);
        rand.writeLong(value);
    }

    public void writeByte(long address, int offset, byte value) throws IOException
    {
        rand.seek(address + offset);
        rand.writeByte(value);
    }

    public byte readByte(long address, int offset) throws IOException
    {
        rand.seek(address + offset);

        return rand.readByte();
    }

    public long readLong(long address, int offset) throws IOException
    {
        rand.seek(address + offset);

        return rand.readLong();
    }

    public void close() throws IOException
    {
        rand.close();
    }
}
