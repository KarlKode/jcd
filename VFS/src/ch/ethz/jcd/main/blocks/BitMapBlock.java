package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockAddressOutOfBoundException;
import ch.ethz.jcd.main.exceptions.DiskFullException;
import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;
import ch.ethz.jcd.main.visitor.BlockVisitor;
import sun.jvm.hotspot.utilities.BitMap;

import java.io.IOException;
import java.util.BitSet;

/**
 * A BitMapBlock block is a kind of freelist. It behave in a relatively simple
 * way. A single bit is used to differ whether a blockAddress is used or not.
 * Each time a Block is allocated or freed, the bit is changed according to the
 * performed action. Changes are written immediately to disk.
 */
public class BitMapBlock extends Block
{
    private final byte ZERO_BYTE = 0x00;
    private final byte FULL_BYTE = (byte)0xFF;

    private final byte USED_SUPERBLOCK_MASK = (byte)0b10000000;
    private final byte USED_BITMAPBLOCK_MASK = (byte)0b01000000;
    private final byte USED_ROOTBLOCK_MASK = (byte)0b00100000;

    private final byte USED_MASK = (byte)0b10000000;

    private BitSet bitMap;
    private int usedBlocks;

    public BitMapBlock(FileManager fileManager, int blockAddress) throws InvalidBlockAddressException
    {
       super(fileManager, blockAddress);
    }

    /**
     * Allocate a new Block out of the unused Blocks this BitMapBlock controls
     *
     * @return block blockAddress of the newly allocated Block
     */
    public int allocateBlock() throws BlockAddressOutOfBoundException, IOException, DiskFullException {
        int pos = 0;

        byte val;
        do {
            val = fileManager.readByte(VUtil.getBlockOffset(this.blockAddress), pos);
            pos++;

            if(pos >= VUtil.BLOCK_SIZE){
                throw new DiskFullException();
            }
        }while(val == FULL_BYTE);

        final BitSet freeBlocks = new BitSet(val);

        int freeBitInByte = freeBlocks.nextClearBit(0);
        int freeBlockAddress = pos * 8 + freeBitInByte;

        //awesome solution (but not sure if correct)
        byte newByte = (byte) (val | (USED_MASK >> freeBitInByte));
        fileManager.writeByte(VUtil.getBlockOffset(this.blockAddress), pos, newByte);

        //readable solution
        //freeBlocks.set(freeBitInByte, true);
        //fileManager.writeByte(VUtil.getBlockOffset(this.blockAddress), pos, freeBlocks.toByteArray()[0]);

        return freeBlockAddress;
    }


    public void initialize() throws IOException {
        byte[] freeVDisk = new byte[VUtil.BLOCK_SIZE];
        byte firstBlock = ZERO_BYTE;

        //initialize superblock
        firstBlock |= USED_SUPERBLOCK_MASK;
        //initialize bitmapblock
        firstBlock |= USED_BITMAPBLOCK_MASK;
        //initialize root directoryblock
        firstBlock |= USED_ROOTBLOCK_MASK;

        freeVDisk[0] = firstBlock;

        fileManager.writeBytes(VUtil.getBlockOffset(this.blockAddress), 0, freeVDisk);
    }

    /**
     * Set a Block as unused
     *
     * @param blockAddress block blockAddress of the Block that should be set as unused
     */
    public void setUnused(int blockAddress) throws BlockAddressOutOfBoundException, IOException {
        if (!isValidBlockAddress(blockAddress)) {
            throw new BlockAddressOutOfBoundException();
        }

        int pos = blockAddress / 8;
        int bit = blockAddress % 8;

        final byte value = fileManager.readByte(VUtil.getBlockOffset(this.blockAddress), pos);
        final BitSet set = new BitSet(value);
        set.clear(bit);

        fileManager.writeByte(VUtil.getBlockOffset(this.blockAddress), pos, set.toByteArray()[0]);
    }

    /**
     * Set all Blocks as unused
     */
    public void reset() throws IOException {
        this.initialize();
    }

    /**
     * Check if a Block is unused
     *
     * @param blockAddress block blockAddress of the Block that should be checked
     * @return true if the Block is not used
     */
    public boolean isUnused(int blockAddress) throws BlockAddressOutOfBoundException, IOException {
        if (!isValidBlockAddress(blockAddress)) {
            throw new BlockAddressOutOfBoundException();
        }

        int pos = blockAddress/8;
        int bit = blockAddress % 8;

        byte value = fileManager.readByte(VUtil.getBlockOffset(this.blockAddress), pos);

        return new BitSet(value).get(bit);
    }
}
