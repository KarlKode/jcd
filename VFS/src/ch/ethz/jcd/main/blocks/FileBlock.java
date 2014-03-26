package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.*;
import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;

public class FileBlock extends ObjectBlock
{
    public static final int SIZE_FILE_SIZE = 8;
    public static final int SIZE_ENTRY = 4;
    public static final int OFFSET_FILE_SIZE = OFFSET_CONTENT;
    public static final int OFFSET_FIRST_ENTRY = OFFSET_FILE_SIZE + SIZE_FILE_SIZE;

    public FileBlock(FileManager fileManager, int blockAddress) throws InvalidBlockAddressException
    {
        super(fileManager, blockAddress);
    }

    public long getSize() throws IOException
    {
        return fileManager.readLong(getBlockOffset(), OFFSET_FILE_SIZE);
    }

    public DataBlock getDataBlock(int dataBlockOffset) throws IOException, InvalidDataBlockOffsetException
    {
        if (dataBlockOffset < 0 || dataBlockOffset >= getMaxDataBlocks()) {
            throw new InvalidDataBlockOffsetException();
        }

        // Create a new DataBlock instance that wraps the part of the file that corresponds to dataBlockOffset
        int dataBlockAddress = fileManager.readInt(getBlockOffset(), OFFSET_FIRST_ENTRY + (dataBlockOffset * SIZE_ENTRY));
        try
        {
            return new DataBlock(fileManager, dataBlockAddress);
        } catch (InvalidBlockAddressException e)
        {
            // TODO: Throw correct exception
            e.printStackTrace();
            throw new NotImplementedException();
        }
    }

    public void addDataBlock(DataBlock dataBlock, int usedBytes) throws BlockFullException, IOException
    {
        long currentSize = getSize();
        long newSize = currentSize + (currentSize % VUtil.BLOCK_SIZE) + usedBytes;
        int dataBlockCount = (int) (currentSize / VUtil.BLOCK_SIZE);
        if (dataBlockCount >= getMaxDataBlocks()) {
            throw new BlockFullException();
        }

        // Write the block address of the added data block
        fileManager.writeInt(getBlockOffset(), OFFSET_FIRST_ENTRY + (dataBlockCount * SIZE_ENTRY), dataBlock.getBlockAddress());
        // Write the new file size
        fileManager.writeLong(getBlockOffset(), OFFSET_FILE_SIZE, newSize);
    }

    public void removeLastDataBlock() throws IOException
    {
        long currentSize = getSize();
        if (currentSize > 0) {
            long newSize;
            if (currentSize % VUtil.BLOCK_SIZE == 0) {
                newSize = currentSize - VUtil.BLOCK_SIZE;
            } else {
                newSize = currentSize - (currentSize % VUtil.BLOCK_SIZE);
            }
            fileManager.writeLong(getBlockOffset(), OFFSET_FILE_SIZE, newSize);
        }
    }

    private int getMaxDataBlocks() {
        return (VUtil.BLOCK_SIZE - OFFSET_FIRST_ENTRY) / SIZE_ENTRY;
    }
}

