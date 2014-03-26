package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.exceptions.InvalidTypeException;
import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.List;

public abstract class ObjectBlock extends Block
{
    public static final int LENGTH_TYPE = 1;
    public static final int LENGTH_NAME = 63;
    public static final int LENGTH_PARENT_BLOCK_ADDRESS = 4;
    public static final int LENGTH_CONTENT_SIZE = 8;
    public static final int OFFSET_TYPE = 0;
    public static final int OFFSET_NAME = OFFSET_TYPE + LENGTH_TYPE;
    public static final int OFFSET_PARENT_BLOCK_ADDRESS = OFFSET_NAME + LENGTH_NAME;
    public static final int OFFSET_CONTENT_SIZE = OFFSET_PARENT_BLOCK_ADDRESS + LENGTH_CONTENT_SIZE;
    public static final int OFFSET_CHILDREN = OFFSET_CONTENT_SIZE + LENGTH_PARENT_BLOCK_ADDRESS;
    public static final byte TYPE_DIRECTORY = 0x00;
    public static final byte TYPE_FILE = 0x01;

    public ObjectBlock(FileManager fileManager, int blockAddress) throws InvalidBlockAddressException
    {
        super(fileManager, blockAddress);
    }

    public byte getType() throws IOException
    {
        return fileManager.readByte(getBlockOffset(), OFFSET_TYPE);
    }

    public void setType(byte type) throws InvalidTypeException
    {
        // Check type for validity
        if (type != TYPE_DIRECTORY && type != TYPE_FILE) {
            throw new InvalidTypeException();
        }
    }

    public String getName() throws IOException
    {
        // TODO: Check if length is correct
        return fileManager.readString(getBlockOffset(), OFFSET_NAME, LENGTH_NAME);
    }

    public void setName(String name) throws InvalidNameException, IOException
    {
        // TODO: Ugly length check!
        if (name == null || name.getBytes().length > LENGTH_NAME) {
            throw new InvalidNameException();
        }

        fileManager.writeString(getBlockOffset(), OFFSET_NAME, name);
    }

    public void setParent(DirectoryBlock parent) throws IOException
    {
        // TODO Remove block from old parent and add as child to new parent

        fileManager.writeInt(getBlockOffset(), OFFSET_PARENT_BLOCK_ADDRESS, parent.getBlockAddress());
    }

    public abstract long getSize();

    public abstract List<ObjectBlock> getChildren();

    public abstract void addChild(Block block);

    public abstract void removeChild(Block block);
}
