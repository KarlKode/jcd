package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SuperBlockTest {
    private static final int BLOCK_ADDRESS = 0;
    private static final int BLOCK_COUNT = 2;
    private static final int ROOT_DIRECTORY = 1234;
    private FileManager fileManager;
    private SuperBlock block;

    @Before
    public void setUp() throws Exception {
        File tmpFile = File.createTempFile("test", "vfs");
        tmpFile.deleteOnExit();
        fileManager = new FileManager(tmpFile);
        // Block count
        fileManager.writeInt(0, SuperBlock.OFFSET_BLOCK_COUNT, BLOCK_COUNT);
        // Root directory
        fileManager.writeInt(0, SuperBlock.OFFSET_ROOT_DIRECTORY_BLOCK, ROOT_DIRECTORY);
        block = new SuperBlock(fileManager, BLOCK_ADDRESS);
    }

    @Test
    public void testConstructor() throws Exception {
        new SuperBlock(fileManager, 0);
        try {
            new SuperBlock(null, 0);
            fail("Exception was expected for invalid file manager");
        } catch (IllegalArgumentException e) {
        }
        try {
            new SuperBlock(fileManager, -1);
            fail("Exception was expected for invalid block address");
        } catch (IllegalArgumentException e) {
        }
        try {
            new SuperBlock(fileManager, 1);
            fail("Exception was expected for invalid super block address");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testGetBlockCount() throws Exception {
        assertEquals(BLOCK_COUNT, block.getBlockCount());
    }

    @Test
    public void testSetBlockCount() throws Exception {
        int newBlockCount = BLOCK_COUNT + 1;
        block.setBlockCount(newBlockCount);
        assertEquals(newBlockCount, block.getBlockCount());
        try {
            block.setBlockCount(-1);
            fail("Exception was expected for invalid block count");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testGetRootDirectoryBlock() throws Exception {
        assertEquals(ROOT_DIRECTORY, block.getRootDirectoryBlock());
    }

    @Test
    public void testSetRootDirectoryBlock() throws Exception {
        int newRootDirectory = ROOT_DIRECTORY + 1;
        block.setRootDirectoryBlock(newRootDirectory);
        assertEquals(newRootDirectory, block.getRootDirectoryBlock());
    }

    @Test
    public void testGetFirstBitMapBlock() throws Exception {
        assertEquals(SuperBlock.BIT_MAP_BLOCK_ADDRESS, block.getFirstBitMapBlock());
    }

    @Test
    public void testGetLastBitMapBlock() throws Exception {
        assertEquals(SuperBlock.BIT_MAP_BLOCK_ADDRESS, block.getLastBitMapBlock());
    }

    @Test
    public void testGetFirstDataBlock() throws Exception {
        assertEquals(SuperBlock.BIT_MAP_BLOCK_ADDRESS + (int) (Math.ceil((double) block.getBlockCount() / VUtil.BLOCK_SIZE * 8)), block.getFirstDataBlock());
        //assertEquals(SuperBlock.DATA_BLOCK_BEGIN_ADDRESS == block.getFirstDataBlock(), block.getBlockCount() < VUtil.BLOCK_SIZE);
    }

    @Test
    public void testSetState() throws Exception {
        block.setVDiskFlags(0);
        assertEquals(0, fileManager.readInt(0, SuperBlock.OFFSET_VDISK_FLAGS));
        block.setVDiskFlags(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, fileManager.readInt(0, SuperBlock.OFFSET_VDISK_FLAGS));

        try {
            block.setVDiskFlags(-1);
            fail("Exception was expected for invalid VDisk state");
        } catch (IllegalArgumentException e) {
        }
    }
}
