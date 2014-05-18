package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.*;
import ch.ethz.jcd.main.exceptions.*;
import ch.ethz.jcd.main.layer.VDirectory;

import java.io.*;

public class VUtil {
    public static final int BLOCK_SIZE = 4096;
    public static final int FLAG_COMPRESSED = 0x1;
    public static final int FLAG_ENCRYPTED = 0x2;

    private final FileManager fileManager;
    private final SuperBlock superBlock;
    private BitMapBlock bitMapBlock;
    private DirectoryBlock rootBlock;
    private VDirectory rootDirectory;

    public VUtil(File vDiskFile)
            throws FileNotFoundException {
        fileManager = new FileManager(vDiskFile);
        // Load super block
        superBlock = new SuperBlock(fileManager, SuperBlock.SUPER_BLOCK_ADDRESS);
        // Load bitmap block
        try {
            bitMapBlock = new BitMapBlock(fileManager, superBlock.getFirstBitMapBlock(), superBlock.getBlockCount());
            rootBlock = new DirectoryBlock(fileManager, superBlock.getRootDirectoryBlock());

            //set name for rootblock - hope that's ok.. (leo)
            rootBlock.setName(VDisk.PATH_SEPARATOR);
            rootDirectory = new VDirectory(rootBlock, null);
        } catch (InvalidBlockAddressException e) {
            // This should never happen
            throw new InternalError();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public VUtil(File vDiskFile, int blockCount)
            throws FileNotFoundException {
        fileManager = new FileManager(vDiskFile);
        // Load super block
        superBlock = new SuperBlock(fileManager, SuperBlock.SUPER_BLOCK_ADDRESS);
        // Load bitmap block
        try {
            bitMapBlock = new BitMapBlock(fileManager, superBlock.getFirstBitMapBlock(), blockCount);
            rootBlock = new DirectoryBlock(fileManager, superBlock.getRootDirectoryBlock());

            //set name for rootblock - hope that's ok.. (leo)
            rootBlock.setName(VDisk.PATH_SEPARATOR);
            rootDirectory = new VDirectory(rootBlock, null);
        } catch (InvalidBlockAddressException e) {
            // This should never happen
            throw new InternalError();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long getBlockOffset(int blockAddress) {
        return ((long) blockAddress) * ((long) BLOCK_SIZE);
    }

    public static void format(File diskFile, long diskSize, int flags)
            throws InvalidSizeException, VDiskCreationException, IOException, InvalidBlockCountException, InvalidBlockAddressException {
        // Check disk size
        if (diskSize <= 0 || diskSize % BLOCK_SIZE != 0 || diskSize < 16 * BLOCK_SIZE) {
            throw new InvalidSizeException();
        }
        int blockCount = (int) (diskSize / BLOCK_SIZE);

        if (diskFile.exists()) {
            diskFile.delete();
        }
        try {
            if (!diskFile.createNewFile()) {
                throw new VDiskCreationException();
            }
        } catch (IOException e) {
            throw new VDiskCreationException();
        }

        // Allocate data
        RandomAccessFile file = new RandomAccessFile(diskFile, "rw");
        file.seek(diskSize - 1);
        file.write((byte) 0x00);
        file.close();

        VUtil vUtil = new VUtil(diskFile, blockCount);

        // Initialize super block
        vUtil.getSuperBlock().setBlockCount(blockCount);
        vUtil.getSuperBlock().setRootDirectoryBlock(vUtil.getSuperBlock().getFirstDataBlock());
        vUtil.getSuperBlock().setVDiskFlags(flags);

        // Initialize bitmap block
        vUtil.getBitMapBlock().initialize();

        // Initialize root directory
        vUtil.getRootDirectory().clear(vUtil);

        vUtil.close();
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024]; // Adjust if you want
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public void close()
            throws IOException {
        fileManager.close();
    }

    /**
     * Allocate a previously free block in the VFS
     *
     * @return Block instance that contains the data of the now used block
     * @throws DiskFullException
     */
    public Block allocateBlock()
            throws DiskFullException, IOException {
        // Get the next free block and set it to used
        try {
            return new Block(fileManager, bitMapBlock.allocateBlock());
        } catch (BlockAddressOutOfBoundException e) {
            throw new DiskFullException();
        }
    }

    public FileBlock allocateFileBlock()
            throws DiskFullException, IOException, InvalidBlockAddressException {
        try {
            return new FileBlock(fileManager, bitMapBlock.allocateBlock());
        } catch (BlockAddressOutOfBoundException e) {
            throw new DiskFullException();
        }
    }

    public DataBlock allocateDataBlock()
            throws DiskFullException, IOException, InvalidBlockAddressException {
        try {
            return new DataBlock(fileManager, bitMapBlock.allocateBlock());
        } catch (BlockAddressOutOfBoundException e) {
            throw new DiskFullException();
        }
    }

    public DirectoryBlock allocateDirectoryBlock()
            throws DiskFullException, IOException, InvalidBlockAddressException {
        try {
            return new DirectoryBlock(fileManager, bitMapBlock.allocateBlock());
        } catch (BlockAddressOutOfBoundException e) {
            throw new DiskFullException();
        }
    }

    /**
     * Set a previously used block as free in the VFS
     *
     * @param block Block instance that contains the data of the now free block
     */
    public void free(Block block)
            throws IOException {
        try {
            bitMapBlock.setUnused(block.getBlockAddress());
        } catch (BlockAddressOutOfBoundException e) {
            // This should never happen
            e.printStackTrace();
        }
    }

    public VDirectory getRootDirectory() {
        return rootDirectory;
    }

    /**
     * This method returns the super block of the loaded disks
     *
     * @return SuperBlock of loaded VDisk
     */
    public SuperBlock getSuperBlock() {
        return superBlock;
    }

    /**
     * This method returns the bit map block of the loaded disk
     *
     * @return BitMapBlock of loaded VDisk
     */
    public BitMapBlock getBitMapBlock() {
        return bitMapBlock;
    }

    /**
     * This method returns the root directory block of the loaded disk
     *
     * @return DirectoryBlock
     */
    public DirectoryBlock getRootDirectoryBlock() {
        return rootBlock;
    }

    /**
     * @return true if the loaded VDisk is compressed, false otherwise
     */
    public boolean isCompressed() {
        try {
            return (superBlock.getVDiskFlags() & FLAG_COMPRESSED) == FLAG_COMPRESSED;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean isEncrypted() {
        try {
            return (superBlock.getVDiskFlags() & FLAG_ENCRYPTED) == FLAG_ENCRYPTED;
        } catch (IOException e) {
            return false;
        }
    }

    public DataBlockListBlock allocateDataBlockListBlock() throws IOException, DiskFullException {
        try {
            DataBlockListBlock dataBlockListBlock = new DataBlockListBlock(fileManager, bitMapBlock.allocateBlock());
            dataBlockListBlock.setUsedBlocks(0);
            return dataBlockListBlock;
        } catch (BlockAddressOutOfBoundException e) {
            throw new DiskFullException();
        }
    }
}
