package ch.ethz.jcd.main.utils;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;

/**
 * Created by leo on 24/03/14.
 */
public class FileManager {
    private File vdiskFile;

    public FileManager(File file){
        this.vdiskFile = file;
    }

    private int readInt(int address, int offset) throws IOException {
        int retValue;

        try(RandomAccessFile rand = new RandomAccessFile(vdiskFile, "r")){
            rand.seek(address + offset);
            retValue = rand.readInt();
        }
        return retValue;
    }

    private byte[] readBytes(int address, int offset, int length) throws IOException {
        final byte[] retValue = new byte[length];

        try(RandomAccessFile rand = new RandomAccessFile(vdiskFile, "r")){
            rand.seek(address + offset);
            rand.readFully(retValue);
        }
        return retValue;
    }

    private String readString(int address, int offset, int length) throws IOException {
        final byte[] retValue = new byte[length];

        try(RandomAccessFile rand = new RandomAccessFile(vdiskFile, "r")){
            rand.seek(address + offset);
            rand.readFully(retValue);
        }
        return new String(retValue);
    }


    private void writeInt(int address, int offset, int value) throws IOException {
        int retValue;

        try(RandomAccessFile rand = new RandomAccessFile(vdiskFile, "w")){
            rand.seek(address + offset);
            rand.writeInt(value);
        }
    }

    private void writeBytes(int address, int offset, final byte[] value) throws IOException {
        try(RandomAccessFile rand = new RandomAccessFile(vdiskFile, "w")){
            rand.seek(address + offset);
            rand.write(value);
        }
    }

    private void writeString(int address, int offset, String value) throws IOException {
        try(RandomAccessFile rand = new RandomAccessFile(vdiskFile, "w")){
            rand.seek(address + offset);
            rand.writeChars(value);
        }
    }

    private long readLong(int address, int offset){
        throw new NotImplementedException();
    }

    public void writeLong(int address, int offset){
        throw new NotImplementedException();
    }

    public static void main(String[] args) throws IOException {
        FileManager dd = new FileManager(new File("TestVDisk.txt"));

        dd.writeBytes(0, 112, new String("fooo").getBytes());
    }
}
