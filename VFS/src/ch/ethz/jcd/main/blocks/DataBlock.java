package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.IOException;

/**
 * Block that offers access to it's whole content.
 */
public class DataBlock extends Block
{
    public DataBlock(FileManager fileManager, int blockAddress)
            throws IllegalArgumentException
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
    public byte[] getContent(int contentOffset, int length)
            throws IOException
    {
        return fileManager.readBytes(getBlockOffset(), contentOffset, length);
    }

    /**
     * Reads the whole content.
     *
     * @return new byte array that contains the read content
     * @throws IOException
     */
    public byte[] getContent()
            throws IOException
    {
        return getContent(0, VUtil.BLOCK_SIZE);
    }

    /**
     * Writes the whole content.
     *
     * @param content new content
     * @throws IOException
     */
    public void setContent(byte[] content)
            throws IOException, BlockFullException
    {
        setContent(content, 0);
    }

    /**
     * Writes content bytes beginning at the offset-th byte.
     *
     * @param content new content
     * @param offset  offset of first byte
     * @throws IOException
     */
    public void setContent(byte[] content, int offset)
            throws IOException, BlockFullException
    {
        if (content == null || offset < 0 || offset >= VUtil.BLOCK_SIZE)
        {
            throw new IllegalArgumentException();
        }

        if (content.length > VUtil.BLOCK_SIZE - offset)
        {
            throw new BlockFullException();
        }

        fileManager.writeBytes(getBlockOffset(), offset, content);
    }
}
