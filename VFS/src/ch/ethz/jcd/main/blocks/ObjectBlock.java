package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.utils.FileManager;

import java.io.IOException;

/**
 * Generic container that offers access to properties, that both files and directories have
 */
public class ObjectBlock extends Block
{
    public static final int LENGTH_TYPE = 1;
    public static final int LENGTH_NAME = 63;
    public static final int LENGTH_PARENT_BLOCK_ADDRESS = 4;
    public static final int OFFSET_TYPE = 0;
    public static final int OFFSET_NAME = OFFSET_TYPE + LENGTH_TYPE;
    public static final int OFFSET_PARENT_BLOCK_ADDRESS = OFFSET_NAME + LENGTH_NAME;
    public static final int OFFSET_CONTENT = OFFSET_PARENT_BLOCK_ADDRESS + LENGTH_PARENT_BLOCK_ADDRESS;
    public static final byte TYPE_DIRECTORY = 0x00;
    public static final byte TYPE_FILE = 0x01;

    public ObjectBlock(FileManager fileManager, int blockAddress) throws InvalidBlockAddressException
    {
        super(fileManager, blockAddress);
    }

    /**
     * @return TYPE_DIRECTORY if this block contains a directory, TYPE_FILE otherwise
     * @throws IOException
     */
    public byte getType() throws IOException
    {
        return fileManager.readByte(getBlockOffset(), OFFSET_TYPE);
    }

    /**
     * @param type new type
     * @throws IOException
     * @throws IllegalArgumentException if the new type is not in [TYPE_DIRECTORY, TYPE_FILE]
     */
    public void setType(byte type) throws IOException, IllegalArgumentException
    {
        // Check type for validity
        if (type != TYPE_DIRECTORY && type != TYPE_FILE)
        {
            throw new IllegalArgumentException();
        }
        fileManager.writeByte(getBlockOffset(), OFFSET_TYPE, type);
    }

    /**
     * @return the name of the object that is stored on the disk
     * @throws IOException
     */
    public String getName() throws IOException
    {
        // TODO: Check if length is correct
        return fileManager.readString(getBlockOffset(), OFFSET_NAME, LENGTH_NAME);
    }

    /**
     * @param name new name
     * @throws IOException
     * @throws IllegalArgumentException if new name is too long or otherwise invalid
     */
    public void setName(String name) throws IOException, IllegalArgumentException
    {
        // TODO: Ugly length check!
        if (name == null || name.getBytes("UTF-8").length > LENGTH_NAME)
        {
            throw new IllegalArgumentException();
        }

        fileManager.writeString(getBlockOffset(), OFFSET_NAME, name);
    }
}
