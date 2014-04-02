package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockEmptyException;
import ch.ethz.jcd.main.exceptions.BlockFullException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileBlock extends ObjectBlock
{
    public static final int SIZE_FILE_SIZE = 8;
    public static final int SIZE_ENTRY = 4;
    public static final int OFFSET_FILE_SIZE = OFFSET_CONTENT;
    public static final int OFFSET_FIRST_ENTRY = OFFSET_FILE_SIZE + SIZE_FILE_SIZE;

    public FileBlock(FileManager fileManager, int blockAddress) throws IllegalArgumentException
    {
        super(fileManager, blockAddress);
    }

    /**
     * This method adds the given DataBlock to the FileBlock and links it in
     * the FileBlocks metadata
     *
     * TODO füegt jo en ganze Block hinzue und tuet nid uffülle falls de letschti no halbe leer isch
     *
     * @param dataBlock to add
     * @param usedBytes of the DataBlock to add
     * @throws BlockFullException
     * @throws IOException
     */
    public void addDataBlock(DataBlock dataBlock, int usedBytes) throws BlockFullException, IOException
    {
        if (usedBytes < 0)
        {
            throw new IllegalArgumentException();
        }

        long newSize = getSize();
        // Round to next block border if necessary
        if (newSize % VUtil.BLOCK_SIZE != 0)
        {
            newSize += VUtil.BLOCK_SIZE - (newSize % VUtil.BLOCK_SIZE);
        }
        newSize += usedBytes;

        if (getUsedDataBlocks(newSize) > getMaxDataBlocks())
        {
            throw new BlockFullException();
        }

        // Write the block address of the added data block
        fileManager.writeInt(getBlockOffset(), OFFSET_FIRST_ENTRY + (getUsedDataBlocks(newSize) * SIZE_ENTRY), dataBlock.getBlockAddress());
        // Write the new file size
        fileManager.writeLong(getBlockOffset(), OFFSET_FILE_SIZE, newSize);
    }

    /**
     * This method removes and unlink the last DataBlock from the FileBlock.
     *
     * @return DataBlock to remove
     * @throws IOException
     */
    public DataBlock removeLastDataBlock() throws IOException, BlockEmptyException
    {
        int blockAddress = -1;
        long currentSize = getSize();

        if (currentSize <= 0)
        {
            throw new BlockEmptyException();
        }
        int offset = OFFSET_FIRST_ENTRY + ((int) (currentSize / VUtil.BLOCK_SIZE) * SIZE_ENTRY);
        long newSize = currentSize - VUtil.BLOCK_SIZE;

        if (currentSize % VUtil.BLOCK_SIZE != 0)
        {
            newSize = currentSize - (currentSize % VUtil.BLOCK_SIZE);
        }

        // read the address of the linked Block
        blockAddress = fileManager.readInt(getBlockOffset(), offset);
        // clear the block address of the unlinked DataBlock
        fileManager.writeInt(getBlockOffset(), offset, 0);
        // update the file size
        fileManager.writeLong(getBlockOffset(), OFFSET_FILE_SIZE, newSize);

        return new DataBlock(fileManager, blockAddress);
    }

    /**
     * This method reads the FileBlock to load the linked DataBlocks
     *
     * @return list of all DataBlocks
     */
    public List<DataBlock> getDataBlockList( ) throws IOException
    {
        int dataBlockCount = getUsedDataBlocks(getSize());
        List<DataBlock> list = new ArrayList<>(dataBlockCount);

        for(int i = 0; i < dataBlockCount; i++)
        {
            list.add(this.getDataBlock(i));
        }

        return list;
    }

    /**
     *
     * @return whether the file is empty or not
     */
    public boolean isEmpty( ) throws IOException
    {
        return !(getSize() > 0);
    }

    /**
     * This method computes the size of the by reading the files
     * metadata
     *
     * @return the size of the file
     * @throws IOException
     */
    public long getSize() throws IOException
    {
        return fileManager.readLong(getBlockOffset(), OFFSET_FILE_SIZE);
    }

    /**
     * This method reads the DataBlock to a given DataBlock index linked in the FileBlock.
     *
     * @param dataBlockIndex of DataBlock to read
     * @return the read DataBlock
     * @throws IOException
     * @throws IllegalArgumentException if the dataBlockIndex is out of Bounds
     */
    public DataBlock getDataBlock(int dataBlockIndex) throws IOException, IllegalArgumentException
    {
        if (dataBlockIndex < 0 || dataBlockIndex >= getMaxDataBlocks())
        {
            throw new IllegalArgumentException();
        }

        if (dataBlockIndex > getUsedDataBlocks(getSize()))
        {
            throw new IllegalArgumentException();
        }

        // Create a new DataBlock instance that wraps the part of the file that corresponds to dataBlockIndex
        int dataBlockAddress = fileManager.readInt(getBlockOffset(), OFFSET_FIRST_ENTRY + (dataBlockIndex * SIZE_ENTRY));
        return new DataBlock(fileManager, dataBlockAddress);
    }


    /**
     *
     * @return the maximum number of DataBlocks fitting into a FileBlock
     */
    private int getMaxDataBlocks()
    {
        return (VUtil.BLOCK_SIZE - OFFSET_FIRST_ENTRY) / SIZE_ENTRY - 1;
    }

    private int getUsedDataBlocks(long size)
    {
        return 0;
    }

    /**
     * This method is used to port this FileBlock into a VFile.
     *
     * @param parent of the VFile
     * @return this FileBlock ported to a VFile
     */
    @Override
    public VObject toVObject(VDirectory parent)
    {
        return new VFile(this, parent);
    }
}

