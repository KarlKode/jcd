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

public class FileBlock extends ObjectBlock {
    public static final int SIZE_FILE_SIZE = 8;
    public static final int SIZE_DATA_BLOCK_LIST = 4;
    public static final int SIZE_DATA_BLOCK_LIST_ENTRY = 4;
    public static final int OFFSET_FILE_SIZE = OFFSET_CONTENT;
    public static final int OFFSET_DATA_BLOCK_LIST = OFFSET_FILE_SIZE + SIZE_FILE_SIZE;
    public static final int OFFSET_FIRST_DATA_BLOCK_LIST_ENTRY = OFFSET_DATA_BLOCK_LIST + SIZE_DATA_BLOCK_LIST;

    public FileBlock(FileManager fileManager, int blockAddress) throws IllegalArgumentException, IOException {
        super(fileManager, blockAddress);
        setType(ObjectBlock.TYPE_FILE);
    }

    /**
     * This method adds the given DataBlock to the FileBlock and links it in
     * the FileBlocks metadata
     *
     * @param dataBlock to add
     * @throws BlockFullException
     * @throws IOException
     */
    public void addDataBlock(DataBlock dataBlock, int usedBlockBytes) throws BlockFullException, IOException {
        // Check for valid arguments
        if (dataBlock == null || usedBlockBytes <= 0) {
            throw new IllegalArgumentException();
        }

        // Calculate the new size
        long oldSize = size();
        if (oldSize != 0 && oldSize % VUtil.BLOCK_SIZE != 0) {
            oldSize += VUtil.BLOCK_SIZE - (oldSize % VUtil.BLOCK_SIZE);
        }
        long newSize = oldSize + usedBlockBytes;

        // Is there a dataBlockListBlock for the new file size? No -> Add a new dataBlockListBlock
        int usedDataBlocks = getUsedDataBlocks(oldSize);
        if (usedDataBlocks >= getMaxDataBlocks()) {
            throw new BlockFullException();
        }

        // Get the correct dataBlockListBlock
        int dataBlockListBlockIndex = usedDataBlocks / DataBlockListBlock.getCapacity();
        DataBlockListBlock dataBlockListBlock = getDataBlockListBlock(dataBlockListBlockIndex);
        if (dataBlockListBlock.getUsedBlocks() == DataBlockListBlock.getCapacity()) {
            throw new BlockFullException();
        }

        // Write block address of the added data block into the correct dataBlockListBlock
        int dataBlockIndex = usedDataBlocks % DataBlockListBlock.getCapacity();
        dataBlockListBlock.setDataBlockAddress(dataBlockIndex, dataBlock.getBlockAddress());
        dataBlockListBlock.setUsedBlocks(dataBlockListBlock.getUsedBlocks() + 1);

        // Write new file size
        setSize(newSize);
    }

    public DataBlockListBlock getDataBlockListBlock(int dataBlockListBlockIndex) throws IOException {
        // check if the block address of the dataBlockListBlock is valid
        if (dataBlockListBlockIndex < 0 || dataBlockListBlockIndex >= getDataBlockListBlocks()) {
            throw new IllegalArgumentException();
        }

        int offset = OFFSET_FIRST_DATA_BLOCK_LIST_ENTRY + dataBlockListBlockIndex * SIZE_DATA_BLOCK_LIST_ENTRY;
        int dataBlockListBlockAddress = fileManager.readInt(getBlockOffset(), offset);

        return new DataBlockListBlock(fileManager, dataBlockListBlockAddress);
    }

    public int getDataBlockListBlocks() throws IOException {
        return fileManager.readInt(getBlockOffset(), OFFSET_DATA_BLOCK_LIST);
    }

    private void setDataBlockListBlocks(int dataBlockListBlocks) throws IOException {
        fileManager.writeInt(getBlockOffset(), OFFSET_DATA_BLOCK_LIST, dataBlockListBlocks);
    }

    /**
     * This method removes and unlink the last DataBlock from the FileBlock.
     *
     * @return DataBlock to remove
     * @throws IOException
     */
    public DataBlock removeLastDataBlock() throws IOException, BlockEmptyException {
        long currentSize = size();

        if (currentSize == 0) {
            throw new BlockEmptyException();
        }

        // Get the dataBlockListBlock that contains the last dataBlock
        DataBlockListBlock dataBlockListBlock = getDataBlockListBlock(getDataBlockListBlocks() - 1);

        // Get the block address of the last data block
        if (dataBlockListBlock.getUsedBlocks() == 0) {
            throw new BlockEmptyException();
        }
        int dataBlockAddress = dataBlockListBlock.getDataBlockAddress(dataBlockListBlock.getUsedBlocks() - 1);
        dataBlockListBlock.setUsedBlocks(dataBlockListBlock.getUsedBlocks() - 1);

        // Remove the dataBlockListBlock from the fileBlock if the dataBlockListBlock is empty
        if (dataBlockListBlock.getUsedBlocks() == 0) {
            removeLastDataBlockListBlock();
        }

        // Update file size
        long oldSize = size();
        if (oldSize % VUtil.BLOCK_SIZE == 0) {
            // The block that will be removed was full -> new size is exactly BLOCK_SIZE smaller
            setSize(oldSize - VUtil.BLOCK_SIZE);
        } else {
            // The block that will be removed was not full -> new new size is exactly (SIZE OF BLOCK TO REMOVE) bytes smaller
            setSize(oldSize - (VUtil.BLOCK_SIZE - (oldSize % VUtil.BLOCK_SIZE)));
        }

        return new DataBlock(fileManager, dataBlockAddress);
    }

    private void removeLastDataBlockListBlock() {
        // Not needed for the moment as we call removeLastDataBlock only just before adding a new block again.
        //throw new ToDoException();
    }

    /**
     * This method reads the FileBlock to load the linked DataBlocks
     *
     * @return list of all DataBlocks
     */
    public List<DataBlock> getDataBlockList() throws IOException {
        int dataBlockCount = getUsedDataBlocks(size());
        List<DataBlock> list = new ArrayList<>(dataBlockCount);

        for (int i = 0; i < dataBlockCount; i++) {
            list.add(this.getDataBlock(i));
        }

        return list;
    }

    public List<DataBlockListBlock> getDataBlockListBlockList() throws IOException {
        int dataBlockListBlockCount = getDataBlockListBlocks();
        List<DataBlockListBlock> dataBlockListBlocks = new ArrayList<>(dataBlockListBlockCount);

        for (int i = 0; i < dataBlockListBlockCount; i++) {
            dataBlockListBlocks.add(getDataBlockListBlock(i));
        }

        return dataBlockListBlocks;
    }

    /**
     * This method computes the size of the by reading the files
     * metadata
     *
     * @return the size of the file
     * @throws IOException
     */
    public long size() throws IOException {
        return fileManager.readLong(getBlockOffset(), OFFSET_FILE_SIZE);
    }

    /**
     * Writes the size of the file into its metadata.
     */
    public void setSize(long size)
            throws IOException {
        fileManager.writeLong(getBlockOffset(), OFFSET_FILE_SIZE, size);
    }

    /**
     * This method computes the number of DataBlocks attached to this file
     *
     * @return the number of DataBlocks
     * @throws IOException
     */
    public int count() throws IOException {
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
    public DataBlock getDataBlock(int dataBlockIndex) throws IOException, IllegalArgumentException {
        if (!isValidDataBlockIndex(dataBlockIndex)) {
            throw new IllegalArgumentException();
        }

        // Create a new DataBlock instance that wraps the part of the file that corresponds to dataBlockIndex
        DataBlockListBlock dataBlockListBlock = getDataBlockListBlock(dataBlockIndex / DataBlockListBlock.getCapacity());

        int dataBlockListIndex = dataBlockIndex % DataBlockListBlock.getCapacity();
        if (dataBlockListIndex >= dataBlockListBlock.getUsedBlocks()) {
            throw new IllegalArgumentException();
        }

        int dataBlockAddress = dataBlockListBlock.getDataBlockAddress(dataBlockListIndex);

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
            throws IOException {
        if (!isValidDataBlockIndex(dataBlockIndex)) {
            throw new IllegalArgumentException();
        }
        return dataBlockIndex == count() - 1;
    }

    /**
     * @param dataBlockIndex to check
     * @return true if the given dataBlockIndex is valid
     */
    public boolean isValidDataBlockIndex(int dataBlockIndex)
            throws IOException {
        return !(dataBlockIndex < 0 || dataBlockIndex >= getMaxDataBlocks() || dataBlockIndex > count());
    }

    /**
     * @return whether the file is empty or not
     */
    public boolean isEmpty() throws IOException {
        return !(size() > 0);
    }

    /**
     * This method is used to port this FileBlock into a VFile.
     *
     * @param parent of the VFile
     * @return this FileBlock ported to a VFile
     */
    @Override
    public VObject toVObject(VDirectory parent) {
        return new VFile(this, parent);
    }

    /**
     * @return the maximum number of DataBlocks fitting into a FileBlock
     */
    private int getMaxDataBlocks() throws IOException {

        return getDataBlockListBlocks() * DataBlockListBlock.getCapacity();
    }

    /**
     * This method computes the theoretically number of used blocks according
     * to a given file size.
     *
     * @param size actual disk size
     * @return number of used blocks
     */
    private int getUsedDataBlocks(long size) {
        return (int) Math.ceil((double) size / (double) VUtil.BLOCK_SIZE);
    }

    public void addDataBlockListBlock(DataBlockListBlock dataBlockListBlock) throws IOException, BlockFullException {
        // Check if there is space for an additional dataBlockListBlock
        if ((VUtil.BLOCK_SIZE - OFFSET_FIRST_DATA_BLOCK_LIST_ENTRY) / SIZE_DATA_BLOCK_LIST_ENTRY <= getDataBlockListBlocks()) {
            throw new BlockFullException();
        }

        int offset = OFFSET_FIRST_DATA_BLOCK_LIST_ENTRY + (getDataBlockListBlocks() * SIZE_DATA_BLOCK_LIST_ENTRY);
        fileManager.writeInt(getBlockOffset(), offset, dataBlockListBlock.getBlockAddress());

        // Update count of the dataBlockListBlocks
        setDataBlockListBlocks(getDataBlockListBlocks() + 1);
    }

    public boolean equals(Object other) {
        return other instanceof Block && ((Block) other).blockAddress == blockAddress;
    }
}


