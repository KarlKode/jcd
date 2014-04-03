package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidBlockSizeException;
import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.IOException;

/**
 * Block that offers access to it's whole content.
 */
public class DataBlock extends Block
{
    /**
     * The max size of a DataBlock is the size of an Integer shorter then the
     * VUtil.BLOCK_SIZE since the first 4 bytes are holding the number of used
     * bytes
     */
    public static int DATA_BLOCK_CONTENT_OFFSET = Integer.SIZE / 8;
    public static int MAX_DATA_BLOCK_SIZE = VUtil.BLOCK_SIZE - DATA_BLOCK_CONTENT_OFFSET;

    public DataBlock(FileManager fileManager, int blockAddress) throws IllegalArgumentException
    {
        super(fileManager, blockAddress);
    }

    /**
     * Reads length bytes beginning at the offset-th byte.
     * Something like content[contentOffset:contentOffset + length]
     *
     * @param contentOffset offset of first byte
     * @param length        number of bytes in total
     * @return new byte array that contains the read content
     * @throws IOException
     */
    public byte[] getContent(int contentOffset, int length) throws IOException
    {
        return fileManager.readBytes(getBlockOffset(), contentOffset, length);
    }

    /**
     * Reads the whole content.
     *
     * @return new byte array that contains the read content
     * @throws IOException
     */
    public byte[] getContent() throws IOException
    {
        return getContent(DATA_BLOCK_CONTENT_OFFSET, size());
    }

    /**
     * Writes the whole content.
     *
     * @param content new content
     * @throws IOException
     * @throws InvalidBlockSizeException
     */
    public void setContent(byte[] content) throws IOException, InvalidBlockSizeException
    {
        setContent(content, 0);
    }

    /**
     * Writes content bytes beginning at the offset-th byte.
     *
     * @param content new content
     * @param offset  offset of first byte
     * @throws IOException
     * @throws InvalidBlockSizeException
     */
    public void setContent(byte[] content, int offset) throws IOException, InvalidBlockSizeException
    {
        if(content.length > MAX_DATA_BLOCK_SIZE - offset)
        {
            throw new InvalidBlockSizeException();
        }
        setSize(content.length);
        fileManager.writeBytes(getBlockOffset(), DATA_BLOCK_CONTENT_OFFSET + offset, content);
    }

    /**
     *
     * @return the block size in bytes
     */
    public int size() throws IOException
    {
       return fileManager.readInt(getBlockOffset(), 0);
    }

    /**
     * Sets the number of used bytes and writes it into the DataBlock's metadata.
     */
    protected void setSize(int usedBytes) throws IOException
    {
        fileManager.writeInt(getBlockOffset(), 0, usedBytes);
    }
}
