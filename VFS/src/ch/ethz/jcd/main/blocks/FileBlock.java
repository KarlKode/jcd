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

    public FileBlock(FileManager fileManager, int blockAddress) throws IllegalArgumentException, IOException
    {
        super(fileManager, blockAddress);
        this.setType(ObjectBlock.TYPE_FILE);
    }

    /**
     * This method adds the given DataBlock to the FileBlock and links it in
     * the FileBlocks metadata
     *
     * @param dataBlock to add
     * @throws BlockFullException
     * @throws IOException
     */
    public void addDataBlock(DataBlock dataBlock, int dataBlockSize) throws BlockFullException, IOException
    {
        long oldFileSize = size();
        long newFileSize = oldFileSize;
        int usedDataBlocks = getUsedDataBlocks(oldFileSize);

        if (dataBlockSize < 0)
        {
            throw new IllegalArgumentException();
        }

        if (usedDataBlocks > getMaxDataBlocks())
        {
            throw new BlockFullException();
        }
        // Round to next block border if necessary
        newFileSize += (newFileSize % VUtil.BLOCK_SIZE != 0) ? VUtil.BLOCK_SIZE - (newFileSize % VUtil.BLOCK_SIZE) : dataBlockSize;
        // Write the block address of the added data block
        fileManager.writeInt(getBlockOffset(), OFFSET_FIRST_ENTRY + usedDataBlocks * SIZE_ENTRY, dataBlock.getBlockAddress());
        // Write the new file size
        this.setSize(newFileSize);
    }

    /**
     * This method removes and unlink the last DataBlock from the FileBlock.
     *
     * @return DataBlock to remove
     * @throws IOException
     */
    public DataBlock removeLastDataBlock() throws IOException, BlockEmptyException
    {
        long currentSize = size();

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
        int blockAddress = fileManager.readInt(getBlockOffset(), offset);
        // clear the block address of the unlinked DataBlock
        fileManager.writeInt(getBlockOffset(), offset, 0);
        // update the file size
        this.setSize(newSize);

        return new DataBlock(fileManager, blockAddress);
    }

    /**
     * This method reads the FileBlock to load the linked DataBlocks
     *
     * @return list of all DataBlocks
     */
    public List<DataBlock> getDataBlockList( ) throws IOException
    {
        int dataBlockCount = getUsedDataBlocks(size());
        List<DataBlock> list = new ArrayList<>(dataBlockCount);

        for(int i = 0; i < dataBlockCount; i++)
        {
            list.add(this.getDataBlock(i));
        }

        return list;
    }

    /**
     * This method computes the size of the by reading the files
     * metadata
     *
     * @return the size of the file
     * @throws IOException
     */
    public long size() throws IOException
    {
        return fileManager.readLong(getBlockOffset(), OFFSET_FILE_SIZE);
    }

    /**
     * Writes the size of the file into its metadata.
     */
    public void setSize(long size)
            throws IOException
    {
        fileManager.writeLong(getBlockOffset(), OFFSET_FILE_SIZE, size);
    }

    /**
     * This method computes the number of DataBlocks attached to this file
     *
     * @return the number of DataBlocks
     * @throws IOException
     */
    public int count() throws IOException
    {
        return getUsedDataBlocks(size());
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
        if (!isValidDataBlockIndex(dataBlockIndex))
        {
            throw new IllegalArgumentException();
        }
        // Create a new DataBlock instance that wraps the part of the file that corresponds to dataBlockIndex
        int dataBlockAddress = fileManager.readInt(getBlockOffset(), OFFSET_FIRST_ENTRY + (dataBlockIndex * SIZE_ENTRY));
        return new DataBlock(fileManager, dataBlockAddress);
    }

    /**
     * This method determines whether the DataBlock at given index is the last
     * linked DataBlock.
     *
     * @param dataBlockIndex to check
     * @return true if the DataBlock at given index is the last one, false otherwise
     */
    public boolean isLastDataBlock(int dataBlockIndex)
            throws IOException
    {
        if (!isValidDataBlockIndex(dataBlockIndex))
        {
            throw new IllegalArgumentException();
        }
        return dataBlockIndex == count() - 1;
    }

    /**
     *
     * @param dataBlockIndex to check
     * @return true if the given dataBlockIndex is valid
     */
    public boolean isValidDataBlockIndex(int dataBlockIndex)
            throws IOException
    {
        return !(dataBlockIndex < 0 || dataBlockIndex >= getMaxDataBlocks() || dataBlockIndex > count());
    }

    /**
     *
     * @return whether the file is empty or not
     */
    public boolean isEmpty( ) throws IOException
    {
        return !(size() > 0);
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

    /**
     *
     * @return the maximum number of DataBlocks fitting into a FileBlock
     */
    private int getMaxDataBlocks()
    {
        return (VUtil.BLOCK_SIZE - OFFSET_FIRST_ENTRY) / SIZE_ENTRY - 1;
    }

    /**
     * This method computes the theoretically number of used blocks according
     * to a given file size.
     *
     * @param size actual disk size
     * @return number of used blocks
     */
    private int getUsedDataBlocks(long size)
    {
        return (int) ((size + VUtil.BLOCK_SIZE - 1) / VUtil.BLOCK_SIZE);
    }
}

