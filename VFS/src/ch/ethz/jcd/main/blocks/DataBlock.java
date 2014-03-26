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

    public byte[] getContent() throws IOException
    {
        return fileManager.readBytes(getBlockOffset(), 0, VUtil.BLOCK_SIZE);
    }

    public void setContent(byte[] content) throws IOException
    {
        fileManager.writeBytes(getBlockOffset(), 0, content);
    }
}
