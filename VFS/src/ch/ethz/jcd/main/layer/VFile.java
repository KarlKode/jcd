package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.DataBlock;
import ch.ethz.jcd.main.blocks.FileBlock;
import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.InvalidDataBlockOffsetException;
import ch.ethz.jcd.main.exceptions.FileTooSmallException;
import ch.ethz.jcd.main.utils.ByteArray;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class VFile extends VObject<FileBlock>
{
    public VFile(FileBlock block, VDirectory parent)
    {
        super(block, parent);
    }

    // TODO fix exceptions
    // TODO: Check whole method for off by 1 errors!
    public void write(byte[] bytes, long startPosition) throws IOException, InvalidDataBlockOffsetException, BlockFullException
    {
        // Add new blocks to the end of the file if needed
        for (long remainingBytes = startPosition + bytes.length - getFileBlock().getSize();
             remainingBytes > 0;
             remainingBytes = startPosition + bytes.length - getFileBlock().getSize()) {
            int usedBytes = remainingBytes > VUtil.BLOCK_SIZE ? VUtil.BLOCK_SIZE : (int) remainingBytes;

            // TODO: Create new DataBlock
            getFileBlock().addDataBlock(null, usedBytes);
        }

        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        int firstDataBlockIndex = (int) (startPosition / VUtil.BLOCK_SIZE);
        int lastDataBlockIndex = (int) ((startPosition + bytes.length + (VUtil.BLOCK_SIZE - 1)) / VUtil.BLOCK_SIZE);
        byte[] buffer;

        // Write first block
        int firstDataBlockOffset = (int) (VUtil.BLOCK_SIZE - (startPosition % VUtil.BLOCK_SIZE));
        int firstDataBlockLength = Math.min(bytes.length, VUtil.BLOCK_SIZE);
        buffer = new byte[firstDataBlockLength];
        if (in.read(buffer) != buffer.length) {
            throw new IOException();
        }
        block.getDataBlock(firstDataBlockIndex).setContent(buffer, firstDataBlockOffset);

        // Write all block between the first and the last block
        for (int currentBlockIndex = firstDataBlockIndex + 1; currentBlockIndex < lastDataBlockIndex; currentBlockIndex++)
        {
            buffer = new byte[VUtil.BLOCK_SIZE];
            if (in.read(buffer) != buffer.length) {
                throw new IOException();
            }
            block.getDataBlock(currentBlockIndex).setContent(buffer);
        }

        // If necessary write the last block
        if (firstDataBlockIndex != lastDataBlockIndex)
        {
            int lastDataBlockLength = (int) ((startPosition + bytes.length) % VUtil.BLOCK_SIZE);
            buffer = new byte[lastDataBlockIndex];
            if (in.read(buffer) != buffer.length) {
                throw new IOException();
            }
            block.getDataBlock(lastDataBlockIndex).setContent(buffer);
        }
    }

    public byte[] read(long startPosition, int length) throws IOException, FileTooSmallException, InvalidDataBlockOffsetException
    {
        if (startPosition + length > getFileBlock().getSize()) {
            throw new FileTooSmallException();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int firstDataBlockIndex = (int) (startPosition / VUtil.BLOCK_SIZE);
        int lastDataBlockIndex = (int) ((startPosition + length + (VUtil.BLOCK_SIZE - 1)) / VUtil.BLOCK_SIZE);

        // Read first block
        int firstDataBlockOffset = (int) (VUtil.BLOCK_SIZE - (startPosition % VUtil.BLOCK_SIZE));
        int firstDataBlockLength = Math.min(length, VUtil.BLOCK_SIZE);
        out.write(block.getDataBlock(firstDataBlockIndex).getContent(firstDataBlockOffset, firstDataBlockLength));

        // Read all block between the first and the last block
        for (int currentBlock = firstDataBlockIndex + 1; currentBlock < lastDataBlockIndex; currentBlock++)
        {
            out.write(block.getDataBlock(currentBlock).getContent());
        }

        // If necessary read the last block
        if (firstDataBlockIndex != lastDataBlockIndex)
        {
            int lastDataBlockLength = (int) ((startPosition + length) % VUtil.BLOCK_SIZE);
            out.write(block.getDataBlock(lastDataBlockIndex).getContent(0, lastDataBlockLength));
        }

        return out.toByteArray();
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
