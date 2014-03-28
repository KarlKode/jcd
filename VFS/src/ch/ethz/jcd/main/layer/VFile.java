package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.DataBlock;
import ch.ethz.jcd.main.blocks.FileBlock;
import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.InvalidDataBlockOffsetException;
import ch.ethz.jcd.main.exceptions.FileTooSmallException;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.IOException;
import java.util.Arrays;

public class VFile extends VObject
{
    private long offset;

    public VFile(FileBlock block, VDirectory parent)
    {
        super(block, parent);
    }

    public void seek(long offset) throws IOException, FileTooSmallException
    {
        if (offset < 0 || offset > getFileBlock().getSize()) {
            throw new FileTooSmallException();
        }

        this.offset = offset;
    }

    // TODO fix exceptions
    // TODO: Check whole method for off by 1 errors!
    public void write(byte[] bytes) throws IOException, InvalidDataBlockOffsetException, BlockFullException
    {
        // Add new blocks to the end of the file if needed
        for (long remainingBytes = offset + bytes.length - getFileBlock().getSize(); remainingBytes > 0; remainingBytes = offset + bytes.length - getFileBlock().getSize()) {
            int usedBytes = remainingBytes > VUtil.BLOCK_SIZE ? VUtil.BLOCK_SIZE : (int) remainingBytes;

            // TODO: Create new DataBlock
            getFileBlock().addDataBlock(null, usedBytes);
        }

        int currentByte = 0;

        // Write first block content
        int dataInFirstBlock = (int) (offset % VUtil.BLOCK_SIZE);
        if (dataInFirstBlock > 0) {
            DataBlock dataBlock = getFileBlock().getDataBlock(getDataBlockIndex(offset));
            byte[] content = dataBlock.getContent();
            int contentOffset = VUtil.BLOCK_SIZE - dataInFirstBlock;
            while (currentByte < dataInFirstBlock) {
                content[contentOffset + currentByte] = bytes[currentByte];
                currentByte++;
                offset++;
            }
        }

        // Write all full blocks
        while (currentByte < bytes.length - VUtil.BLOCK_SIZE) {
            DataBlock dataBlock = getFileBlock().getDataBlock(getDataBlockIndex(offset));
            dataBlock.setContent(Arrays.copyOfRange(bytes, currentByte, currentByte + VUtil.BLOCK_SIZE));
            currentByte += VUtil.BLOCK_SIZE;
            offset += VUtil.BLOCK_SIZE;
        }

        // Write last block content
        if (currentByte < bytes.length)
        {
            DataBlock dataBlock = getFileBlock().getDataBlock(getDataBlockIndex(offset));
            dataBlock.setContent(Arrays.copyOf(bytes, VUtil.BLOCK_SIZE));
            currentByte += bytes.length - currentByte;
            offset += bytes.length - currentByte;
        }
    }

    public void read(byte[] bytes) throws IOException, FileTooSmallException, InvalidDataBlockOffsetException
    {
        if (offset + bytes.length > getFileBlock().getSize()) {
            throw new FileTooSmallException();
        }

        int currentByte = 0;

        // Read bytes from first block
        int dataInFirstBlock = (int) (offset % VUtil.BLOCK_SIZE);
        if (dataInFirstBlock > 0) {
            DataBlock dataBlock = getFileBlock().getDataBlock(getDataBlockIndex(offset));
            byte[] content = dataBlock.getContent();
            int contentOffset = VUtil.BLOCK_SIZE - dataInFirstBlock;
            while (currentByte < dataInFirstBlock) {
                bytes[currentByte] = content[contentOffset + currentByte];
                currentByte++;
                offset++;
            }
        }

        // Read all full bocks
        while (currentByte < bytes.length - VUtil.BLOCK_SIZE) {
            DataBlock dataBlock = getFileBlock().getDataBlock(getDataBlockIndex(offset));
            byte[] content = dataBlock.getContent();
            for (byte aContent : content)
            {
                bytes[currentByte] = aContent;
                currentByte++;
                offset++;
            }
        }

        // Read data from last block
        if (currentByte < bytes.length)
        {
            DataBlock dataBlock = getFileBlock().getDataBlock(getDataBlockIndex(offset));
            byte[] content = dataBlock.getContent();
            for (byte aContent : content)
            {
                // Don't read too much!
                if (currentByte >= bytes.length) {
                    break;
                }

                bytes[currentByte] = aContent;
                currentByte++;
                offset++;
            }
        }
    }

    private FileBlock getFileBlock()
    {
        return (FileBlock) block;
    }

    private int getDataBlockIndex(long offset) {
        int dataBlockIndex = (int) (offset / VUtil.BLOCK_SIZE);
        if (offset % VUtil.BLOCK_SIZE > 0) {
            dataBlockIndex++;
        }
        return dataBlockIndex;
    }
}
