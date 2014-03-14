package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.exceptions.ToDoException;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class is an abstraction of FileBlock and DirectoryBlock called InodeBlock.
 * An InodeBlock has the following structure:
 *
 *      1 byte |    63 bytes    |   4 bytes         |   4 bytes
 * [    type   |    name        |   BlockAddress1   |   BlockAddress2   |   ... ]
 */
public class InodeBlock extends Block
{
    public static final int MAX_NAME_SIZE = 63;
    public static final int OFFSET_TYPE = 0;
    public static final int OFFSET_NAME = 1;
    public static final int OFFSET_BLOCKS = OFFSET_NAME + MAX_NAME_SIZE;
    public static final byte TYPE_DIRECTORY = Byte.parseByte("00");
    public static final byte TYPE_FILE = Byte.parseByte("01");

    protected String name;
    protected byte type;
    protected LinkedList<Integer> blockAddressList = new LinkedList<Integer>();

    /**
     * This constructor is used to specialize a general Block
     *
     * @param b general Block
     * @param name of created InodeBlock
     */
    public InodeBlock(Block b, String name) throws InvalidNameException
    {
        super(b);
        this.init();
        this.setName(name);
    }

    /**
     * This constructor is used to specialize a general Block
     *
     * @param b general Block
     */
    public InodeBlock(Block b)
    {
        super(b);
        this.init();
    }

    /**
     * This method expands the file by adding a given Block
     *
     * @param b to add
     * @throws BlockFullException thrown if the maximum file size is reached
     */
    public void add(Block b) throws BlockFullException
    {
        if(isBlockFull())
        {
            throw new BlockFullException();
        }
        blockAddressList.add(b.getAddress());
        this.storeLinkedBlocks( );
    }

    /**
     * This method expands the file by adding a given Block
     *
     * @param blockAddress of the Block to add
     * @throws BlockFullException thrown if the maximum file size is reached
     */
    public void add(int blockAddress) throws BlockFullException
    {
        if(isBlockFull())
        {
            throw new BlockFullException();
        }
        blockAddressList.add(blockAddress);
        this.storeLinkedBlocks( );
    }

    /**
     * This method removes a given Block from the file
     *
     * @param b to remove
     */
    public void remove(Block b)
    {
        blockAddressList.removeFirstOccurrence(b.getAddress());
        this.storeLinkedBlocks( );
    }

    /**
     * This method removes a given Block from the file
     *
     * @param blockAddress of Block to remove
     */
    public void remove(int blockAddress)
    {
        blockAddressList.removeFirstOccurrence(blockAddress);
        this.storeLinkedBlocks( );
    }

    /**
     *
     * @return size of the Inodes specialized type
     */
    public int size()
    {
        throw new ToDoException();
    }

    /**
     * This method initializes the FileBlock. This includes detecting the
     * inode type, reading the name, loading the linked blocks
     */
    public void init( )
    {
        type = bytes.get(OFFSET_TYPE);
        name = bytes.getString(OFFSET_NAME,MAX_NAME_SIZE).trim();
        this.loadLinkedBlocks();
    }

    /**
     * This method loads the blockAddresses of all linked Blocks
     */
    public void loadLinkedBlocks( )
    {
        blockAddressList.clear();

        for(int i = OFFSET_BLOCKS; i < bytes.size(); i = i + 4)
        {
            int address = bytes.getInt(i);
            if(address > 0)
            {
                blockAddressList.add(address);
            }
            else
            {
                break;
            }
        }
    }

    /**
     * This method stors the blockAddressList
     */
    public void storeLinkedBlocks( )
    {
        int i = OFFSET_BLOCKS;
        Iterator<Integer> it = blockAddressList.iterator();
        while(i < bytes.size() && it.hasNext())
        {
            bytes.putInt(i, it.next());
            i = i + 4;
        }
        bytes.clear(i);
    }

    /**
     * This method sets the name of the Blockit
     *
     * @param name name to set
     */
    public void setName( String name ) throws InvalidNameException
    {
        if(name == null || name.length() > MAX_NAME_SIZE)
        {
            throw new InvalidNameException();
        }
        this.name = name;
        bytes.put(OFFSET_NAME, name.getBytes( ));
    }

    /**
     *
     * @return whether the InodeBlock is full or not
     */
    public boolean isBlockFull( )
    {
        return (blockAddressList.size()) >= (bytes.size() - OFFSET_BLOCKS) / 4;
    }

    /**
     *
     * @return whether the InodeBlock is a File or not
     */
    public boolean isFile( )
    {
        return type == TYPE_FILE;
    }

    /**
     *
     * @return whether the InodeBlock is a Directory or not
     */
    public boolean isDirectory( )
    {
        return type == TYPE_DIRECTORY;
    }

    /**
     *
     * @return the blockAddressList
     */
    public LinkedList<Integer> getBlockAddressList()
    {
        return blockAddressList;
    }

    /**
     *
     * @return the byte representation of the inode type
     */
    public byte getType( )
    {
        return type;
    }

    /**
     *
     * @return the name of the InodeBlock
     */
    public String getName( )
    {
        return name.trim();
    }
}
