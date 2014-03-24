package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidNameException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    public ObjectBlock(int blockAddress, byte[] bytes)
    {
        super(blockAddress, bytes);
    }

    public byte getType()
    {
        // TODO
        throw new NotImplementedException();
    }

    public void setType(byte type)
    {
        // TODO
        throw new NotImplementedException();
    }

    public String getName()
    {
        return bytes.getString(OFFSET_NAME, LENGTH_NAME);
    }

    public void setName(String name) throws InvalidNameException
    {
        if (name == null || name.getBytes().length > LENGTH_NAME) {
            throw new InvalidNameException();
        }

        bytes.putString(OFFSET_NAME, name);
    }

    public void setParent(DirectoryBlock parent)
    {
        // TODO Remove block from old parent and add as child to new parent

        bytes.putInt(OFFSET_PARENT_BLOCK_ADDRESS, parent.getAddress());
    }

    public abstract long getSize();

    public abstract List<ObjectBlock> getChildren();

    public abstract void addChild(Block block);

    public abstract void removeChild(Block block);
}
