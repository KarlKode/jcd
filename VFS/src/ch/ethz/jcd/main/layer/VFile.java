package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.DataBlock;
import ch.ethz.jcd.main.blocks.FileBlock;
import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.FileTooSmallException;
import ch.ethz.jcd.main.exceptions.InvalidDataBlockOffsetException;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * VFile is a concrete implementation of VObject coming with additional features such as
 * read(), write(), copy(), delete()
 */
public class VFile extends VObject<FileBlock>
{
    /**
     * Instantiation of a new VFile.
     *
     * @param block containing the byte structure of the VFile
     * @param parent of the VFile
     */
    public VFile(FileBlock block, VDirectory parent)
    {
        super(block, parent);
    }

    /**
     * This Method copies the VFile to the given destination
     *
     * @param destination where to put the copied VObject
     */
    @Override
    public void copy(VUtil vUtil, VDirectory destination) throws BlockFullException, IOException
    {
        FileBlock fileBlock = vUtil.allocateBlock();
        VFile copy = new VFile(, destination);
        destination.addEntry(copy);
    }

    /**
     * This Method deletes the VFile
     */
    @Override
    public void delete(VUtil vUtil) throws IOException
    {
        for(DataBlock b: block.getDataBlockList( ))
        {
            vUtil.free(b);
        }

        vUtil.free(block);
    }

    // TODO fix exceptions
    // TODO: Check whole method for off by 1 errors!
    public void write(byte[] bytes, long startPosition) throws IOException, InvalidDataBlockOffsetException, BlockFullException
    {
        // Add new blocks to the end of the file if needed
        for (long remainingBytes = startPosition + bytes.length - block.getSize();
             remainingBytes > 0;
             remainingBytes = startPosition + bytes.length - block.getSize())
        {
            int usedBytes = remainingBytes > VUtil.BLOCK_SIZE ? VUtil.BLOCK_SIZE : (int) remainingBytes;

            // TODO: Create new DataBlock
            block.addDataBlock(null, usedBytes);
        }

        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        int firstDataBlockIndex = (int) (startPosition / VUtil.BLOCK_SIZE);
        int lastDataBlockIndex = (int) ((startPosition + bytes.length) / VUtil.BLOCK_SIZE);
        byte[] buffer;

        // Write first block
        int firstDataBlockOffset = (int) (startPosition % VUtil.BLOCK_SIZE);
        int firstDataBlockLength = Math.min(bytes.length, VUtil.BLOCK_SIZE - firstDataBlockOffset);
        buffer = new byte[firstDataBlockLength];
        // TODO: read might not fill the whole buffer
        if (in.read(buffer) != buffer.length)
        {
            throw new IOException();
        }
        block.getDataBlock(firstDataBlockIndex).setContent(buffer, firstDataBlockOffset);

        // Write all block between the first and the last block
        for (int currentBlockIndex = firstDataBlockIndex + 1; currentBlockIndex < lastDataBlockIndex; currentBlockIndex++)
        {
            buffer = new byte[VUtil.BLOCK_SIZE];
            // TODO: read might not fill the whole buffer
            if (in.read(buffer) != buffer.length)
            {
                throw new IOException();
            }
            block.getDataBlock(currentBlockIndex).setContent(buffer);
        }

        // If necessary write the last block
        if (firstDataBlockIndex != lastDataBlockIndex)
        {
            int lastDataBlockLength = (int) ((startPosition + bytes.length) % VUtil.BLOCK_SIZE);
            buffer = new byte[lastDataBlockLength];
            // TODO: read might not fill the whole buffer
            if (in.read(buffer) != buffer.length)
            {
                throw new IOException();
            }
            block.getDataBlock(lastDataBlockIndex).setContent(buffer);
        }
    }

    public byte[] read(long startPosition, int length) throws IOException, FileTooSmallException, InvalidDataBlockOffsetException
    {
        if (startPosition + length > block.getSize())
        {
            throw new FileTooSmallException();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int firstDataBlockIndex = (int) (startPosition / VUtil.BLOCK_SIZE);

        //(VUtil.BLOCK_SIZE - 1)) / VUtil.BLOCK_SIZE) is equivalent to Math.ceil()
        int lastDataBlockIndex = (int) ((startPosition + length) / VUtil.BLOCK_SIZE);

        // Read first block
        int firstDataBlockOffset = (int) (startPosition % VUtil.BLOCK_SIZE);
        int firstDataBlockLength = Math.min(length, VUtil.BLOCK_SIZE - firstDataBlockOffset);
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
}
