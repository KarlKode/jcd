package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.Config;
import ch.ethz.jcd.main.blocks.BitMapBlock;
import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.blocks.SuperBlock;
import ch.ethz.jcd.main.exceptions.BlockAddressOutOfBoundException;
import ch.ethz.jcd.main.exceptions.InvalidBlockSizeException;
import ch.ethz.jcd.main.exceptions.InvalidNameException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class FDisk
{
    public static void fdisk(String vDiskFile, int blockSize, int blockCount)
    {
        try
        {
            RandomAccessFile raf = new RandomAccessFile(vDiskFile, "rws");
            int freeListSize = (int) Math.ceil(blockCount / (blockSize * 8)) * blockSize;
            int disksize = Config.VFS_SUPER_BLOCK_SIZE + freeListSize + blockCount * blockSize;
            raf.setLength(disksize);

            ByteArray bytes = new ByteArray(new byte[blockSize]);
            bytes.putInt(SuperBlock.OFFSET_BLOCK_SIZE, blockSize);
            bytes.putInt(SuperBlock.OFFSET_BLOCK_COUNT, blockCount);
            bytes.putInt(SuperBlock.OFFSET_ROOT_DIRECTORY_BLOCK, 2);
            SuperBlock superBlock = new SuperBlock(bytes.getBytes());

            BitMapBlock bitMapBlock = new BitMapBlock(superBlock.getFirstBitMapBlock(), new byte[blockSize]);
            DirectoryBlock rootBlock = new DirectoryBlock(new Block(superBlock.getRootDirectoryBlock(), new byte[blockSize]), DirectoryBlock.ROOT_DIRECTORY_BLOCK_NAME);
            // Set the SuperBlock, the BitMapBlock and the rootBlock as used
            bitMapBlock.setUsed(superBlock.getAddress());
            bitMapBlock.setUsed(bitMapBlock.getAddress());
            bitMapBlock.setUsed(rootBlock.getAddress());

            raf.seek(getBlockOffset(superBlock.getAddress(), blockSize));
            raf.write(superBlock.getBytes());
            raf.seek(getBlockOffset(bitMapBlock.getAddress(), blockSize));
            raf.write(bitMapBlock.getBytes());
            raf.seek(getBlockOffset(rootBlock.getAddress(), blockSize));
            raf.write(rootBlock.getBytes());
            raf.close();
        }
        catch (BlockAddressOutOfBoundException | InvalidBlockSizeException | InvalidNameException | IOException e)
        {
            e.printStackTrace();
        }

    }

    public static void main(String[] args)
    {
        FDisk.fdisk("/tmp/initTest.vdisk", 1024, 512);

        FDisk.fdisk("/tmp/test.vdisk", 1024, 512);
    }

    private static long getBlockOffset(int blockAddress, int blockSize)
    {
        return ((long) blockAddress) * ((long) blockSize);
    }
}
