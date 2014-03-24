package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.InvalidNameException;

public class ObjectBlock extends Block
{
    public static final int TYPE_SIZE = 1;
    public static final int NAME_SIZE = 63;
    public static final int PARENT_BLOCK_ADDRESS_SIZE = 4;
    public static final int OFFSET_TYPE = 0;
    public static final int OFFSET_NAME = OFFSET_TYPE + TYPE_SIZE;
    public static final int OFFSET_PARENT_BLOCK_ADDRESS = OFFSET_NAME + NAME_SIZE;
    public static final int OFFSET_CHILDREN = OFFSET_PARENT_BLOCK_ADDRESS + PARENT_BLOCK_ADDRESS_SIZE;
    public static final byte TYPE_DIRECTORY = 0x00;
    public static final byte TYPE_FILE = 0x01;

    public ObjectBlock(int blockAddress, byte[] bytes)
    {
        super(blockAddress, bytes);
    }

    public String getName()
    {
        return bytes.getString(OFFSET_NAME, NAME_SIZE);
    }

    public void setName(String name) throws InvalidNameException
    {
        if (name == null || name.getBytes().length > NAME_SIZE) {
            throw new InvalidNameException();
        }

        bytes.putString(OFFSET_NAME, name);
    }

    public void setParent(DirectoryBlock parent)
    {
        // TODO Remove block from old parent and add as child to new parent

        bytes.putInt(OFFSET_PARENT_BLOCK_ADDRESS, parent.getAddress());
    }
}
