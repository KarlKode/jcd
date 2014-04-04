package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.DataBlock;
import ch.ethz.jcd.main.blocks.FileBlock;
import ch.ethz.jcd.main.exceptions.*;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

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
     * @param vUtil used to allocate Blocks
     * @param destination where to put the copied VObject
     *
     * @throws BlockFullException
     * @throws IOException
     * @throws InvalidBlockAddressException
     * @throws DiskFullException
     */
    @Override
    public void copy(VUtil vUtil, VDirectory destination) throws BlockFullException, IOException, InvalidBlockAddressException, DiskFullException, InvalidBlockSizeException
    {
        FileBlock fileBlock = vUtil.allocateFileBlock();

        for(DataBlock src: this.block.getDataBlockList())
        {
            DataBlock dest = vUtil.allocateDataBlock();
            dest.setContent(src.getContent());
            fileBlock.addDataBlock(dest, src.size());
        }

        VFile copy = new VFile(fileBlock, destination);
        destination.addEntry(copy);
    }

    /**
     *  This Method deletes the VFile
     *
     * @param vUtil used to free the corresponding Blocks
     * @throws IOException
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
    // TODO: fix usedBytes
    public void write(byte[] bytes, long startPosition) throws IOException, InvalidDataBlockOffsetException, BlockFullException, InvalidBlockSizeException
    {
        // Add new blocks to the end of the file if needed
        for (long remainingBytes = startPosition + bytes.length - block.size();
             remainingBytes > 0;
             remainingBytes = startPosition + bytes.length - block.size())
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
        if (startPosition + length > block.size())
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

    /**
     * This method instantiate an input stream to buffered write into this file.
     *
     * @return the input stream
     */
    public VFileImputStream inputStream(VUtil vUtil) throws IOException
    {
        return new VFileImputStream(vUtil, this);
    }

    /**
     * The VFileOutputStream is used to buffered write into a file stored in
     * the virtual file system. Useful when doing imports to avoid loading big
     * files completely into memory.
     */
    public class VFileImputStream
    {
        private VUtil vUtil;
        private VFile vFile;

        /**
         * Visibility is set to private to ensure correct instantiation
         *
         * @param vUtil used to allocate Blocks
         */
        private VFileImputStream(VUtil vUtil, VFile vFile) throws IOException
        {
            this.vUtil = vUtil;
            this.vFile = vFile;

            for(DataBlock b: block.getDataBlockList( ))
            {
                this.vUtil.free(b);
            }
        }

        public void put(byte[] bytes) throws DiskFullException, IOException, InvalidBlockAddressException, InvalidBlockSizeException, BlockFullException
        {
            DataBlock dataBlock = vUtil.allocateDataBlock();
            dataBlock.setContent(bytes);
            vFile.block.addDataBlock(dataBlock, bytes.length);
        }
    }

    /**
     * This method instantiate an iterator to buffered read this file.
     *
     * @return the iterator
     */
    public VFileOutputStream iterator()
    {
        return new VFileOutputStream(this);
    }

    /**
     * The VFileOutputStream is used to buffered read a file stored in the virtual
     * file system. Useful when doing exports to avoid loading big files completely
     * into memory.
     */
    public class VFileOutputStream implements Iterator<ByteBuffer>
    {
        private VFile vFile;
        private int next = 0;

        /**
         * Visibility is set to private to ensure the iterator is instantiated
         * correctly
         *
         * @param vFile
         */
        private VFileOutputStream(VFile vFile)
        {
            this.vFile = vFile;
        }

        /**
         *
         *
         * @return true if there is a next element, false otherwise
         */
        @Override
        public boolean hasNext()
        {
            try
            {
                return next < vFile.block.count();
            }
            catch (IOException e)
            {
                return false;
            }
        }

        /**
         * This method read the next DataBlock and extract the bytes stored in
         * it.
         *
         * @return the read bytes
         */
        @Override
        public ByteBuffer next()
        {
            try
            {
                ByteBuffer buf = ByteBuffer.wrap(block.getDataBlock(next).getContent());
                next++;
                return buf;
            }
            catch (IOException e)
            {
                return ByteBuffer.wrap(null);
            }
        }

        /**
         * This method is not supported thus calling it will throw a runtime
         * exception.
         */
        @Override
        public void remove( )
        {
            throw new UnsupportedOperationException();
        }
    }
}
