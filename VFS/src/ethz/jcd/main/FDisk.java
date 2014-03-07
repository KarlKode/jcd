package ethz.jcd.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Created by phgamper on 3/7/14.
 */
public class FDisk
{
    public static void fdisk(String vDiskFile, int blockSize, int blockCount)
    {
        try
        {
            RandomAccessFile raf = new RandomAccessFile(vDiskFile, "rw");
            int freeListSize = (int) Math.ceil(blockCount/(blockSize * 8))*blockSize;
            int disksize = Config.VFS_SUPER_BLOCK_SIZE + freeListSize  + blockCount * blockSize;
            raf.setLength(disksize);

            /**
             * Create SuperBlock
             */
            byte[] bytes = new byte[Config.VFS_SUPER_BLOCK_SIZE];
            ByteBuffer buf = ByteBuffer.wrap(bytes);
            buf.putInt(0, blockSize);
            buf.putInt(4, blockCount);
            //SuperBlock root = SuperBlock.getInstance(bytes);
            raf.write(bytes); //, 0, Config.VFS_SUPER_BLOCK_SIZE);

            /**
             * Create FreeList
             */
            bytes = new byte[freeListSize];
            raf.write(bytes); //, Config.VFS_SUPER_BLOCK_SIZE, Config.VFS_SUPER_BLOCK_SIZE + freeListSize);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args )
    {
        FDisk.fdisk("/tmp/initTest.vdisk", 1024, 512);

        FDisk.fdisk("/tmp/test.vdisk", 1024, 512);
    }
}
