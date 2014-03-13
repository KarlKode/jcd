package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockFullException;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class is an abstraction of FileBlock and DirectoryBlock called InodeBlock.
 * An InodeBlock has the following structure:
 *
 *      1 byte |    63 bytes    |   4 bytes         |   4 bytes
 * [    type   |    name        |   BlockAddress1   |   BlockAddress2   |   ... ]
 */
public abstract class InodeBlock extends Block
{
    public static final int MAX_NAME_SIZE = 63;
    public static final int OFFSET_TYPE = 0;
    public static final int OFFSET_NAME = 1;
    public static final int OFFSET_FILE_BLOCKS = OFFSET_NAME + MAX_NAME_SIZE;

    protected String name;
    protected byte type;

    protected LinkedList<Integer> blockAddressList = new LinkedList<Integer>();

    public InodeBlock( )
    {

    }

    public InodeBlock(Block b)
    {
        super(b);
        this.init( );
    }

    public InodeBlock(int blockAddress)
    {
        super(blockAddress);
    }

    public InodeBlock(byte[] bytes)
    {
        super(bytes);
        this.init();
    }

    public InodeBlock(int blockAddress, byte[] bytes)
    {
        super(blockAddress, bytes);
        this.init( );
    }

    public void add(Block block) throws BlockFullException
    {
        if(isBlockFull())
        {
            throw new BlockFullException();
        }
        blockAddressList.add(block.getAddress());
        this.storeLinkedBlocks( );
    }

    public void add(int blockAddress) throws BlockFullException
    {
        if(isBlockFull())
        {
            throw new BlockFullException();
        }
        blockAddressList.add(blockAddress);
        this.storeLinkedBlocks( );
    }

    public void remove(Block block)
    {
        blockAddressList.remove(block.getAddress());
        this.storeLinkedBlocks( );
    }

    public void remove(int blockAddress)
    {
        blockAddressList.remove(blockAddress);
        this.storeLinkedBlocks( );
    }

    public int size()
    {
        //TODO richtig berechne
        return blockAddressList.size();
    }

    public void init( )
    {
        type = block.get(OFFSET_TYPE);
        byte[] buf = new byte[MAX_NAME_SIZE];
        block.get(buf, OFFSET_NAME, MAX_NAME_SIZE);
        name = buf.toString();
        this.loadLinkedBlocks();
    }

    public void loadLinkedBlocks( )
    {
        blockAddressList.clear();

        for(int i = OFFSET_FILE_BLOCKS; i < bytes.length; i++)
        {
            blockAddressList.add(block.getInt(i));
        }
    }

    public void storeLinkedBlocks( )
    {
        int i = OFFSET_FILE_BLOCKS;
        Iterator<Integer> it = blockAddressList.iterator();
        while(i < bytes.length && it.hasNext())
        {
            block.putInt(i, blockAddressList.get(it.next()));
            i++;
        }
    }

    public boolean isBlockFull( )
    {
        return (blockAddressList.size()) >= (bytes.length - OFFSET_FILE_BLOCKS) / 4;
    }

    public boolean isFile( )
    {
        return type == 00;
    }

    public boolean isDirectory( )
    {
        return type == 01;
    }

    public LinkedList<Integer> getBlockAddressList()
    {
        return blockAddressList;
    }

    public byte getType( )
    {
        return type;
    }

    public String getName( )
    {
        return name;
    }
}
