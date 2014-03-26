package ch.ethz.jcd.main.utils;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;

public class FileManager {
    private final RandomAccessFile rand;

    public FileManager(File file) throws FileNotFoundException {
        this.rand = new RandomAccessFile(file, "rw");
    }

    public int readInt(long address, int offset) throws IOException {
        rand.seek(address + offset);

        return rand.readInt();
    }

    public byte[] readBytes(long address, int offset, int length) throws IOException {
        final byte[] retValue = new byte[length];

        rand.seek(address + offset);
        rand.readFully(retValue);

        return retValue;
    }

    public String readString(long address, int offset, int length) throws IOException {
        final byte[] retValue = new byte[length];

        rand.seek(address + offset);
        rand.readFully(retValue);

        return new String(retValue);
    }


    public void writeInt(long address, int offset, int value) throws IOException {
        rand.seek(address + offset);
        rand.writeInt(value);
    }

    public void writeBytes(long address, int offset, final byte[] value) throws IOException {
        rand.seek(address + offset);
        rand.write(value);
    }

    private void writeString(long address, int offset, String value) throws IOException {
        rand.seek(address + offset);
        rand.writeChars(value);
    }

    public void writeLong(long address, int offset, long value) throws IOException {
        rand.seek(address + offset);
        rand.writeLong(value);
    }


    public long readLong(long address, int offset) throws IOException {
        rand.seek(address + offset);

        return rand.readLong();
    }


    public static void main(String[] args) throws IOException {
        File a = new File("/Users/leo/TestVDisk.txt");
        if(!a.exists()){
            a.createNewFile();
        }

        FileManager dd = new FileManager(a);

        int i1 = 5;
        String foo = "foo";


        dd.writeBytes(0, 4, foo.getBytes());
        dd.writeInt(0, 0, i1);

    }
}
