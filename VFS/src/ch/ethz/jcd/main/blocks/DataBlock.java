package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.IOException;

public class DataBlock extends Block
{
    public DataBlock(FileManager fileManager, int blockAddress) throws InvalidBlockAddressException
    {
        super(fileManager, blockAddress);
    }

    public byte[] getContent(int contentOffset, int length) throws IOException
    {
        return fileManager.readBytes(getBlockOffset(), contentOffset, length);
    }

    /**
     * Reads the whole content of the DataBlock
     *
     * @return
     * @throws IOException
     */
    public byte[] getContent() throws IOException
    {
        return getContent(0, VUtil.BLOCK_SIZE);
    }

    public void setContent(byte[] content) throws IOException
    {
        setContent(content, 0);
    }

    public void setContent(byte[] content, int offset) throws IOException
    {
        fileManager.writeBytes(getBlockOffset(), offset, content);
    }
}
