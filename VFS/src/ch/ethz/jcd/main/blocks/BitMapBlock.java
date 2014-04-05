package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.exceptions.BlockAddressOutOfBoundException;
import ch.ethz.jcd.main.exceptions.DiskFullException;
import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;

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
    private static final byte ZERO_BYTE = 0x00;
    private static final byte FULL_BYTE = (byte) 0xFF;

    private static final byte USED_SUPERBLOCK_MASK = (byte) 0b00000001;
    private static final byte USED_BITMAPBLOCK_MASK = (byte) 0b00000010;
    private static final byte USED_ROOTBLOCK_MASK = (byte) 0b00000100;

    private static final byte USED_MASK = (byte) 0b00000001;

    private Object sync = new Object();
    private int usedBlocks;

    private int lastSmallestFreeBlockPosition;


    public BitMapBlock(FileManager fileManager, int blockAddress) throws InvalidBlockAddressException
    {
        super(fileManager, blockAddress);

        //init usedBlocks and lastSmallestFreeBlockPosition
        try {
            final byte[] content = fileManager.readBytes(VUtil.getBlockOffset(this.blockAddress), 0, VUtil.BLOCK_SIZE);
            final BitSet set = BitSet.valueOf(content);

            this.usedBlocks = set.cardinality();
            this.lastSmallestFreeBlockPosition = set.nextClearBit(0);
        } catch (IOException e) {
            //shouldn't be thrown
            e.printStackTrace();
        }
    }

    /**
     * Allocate a new Block out of the unused Blocks this BitMapBlock controls
     *
     * @return block blockAddress of the newly allocated Block
     */
    public int allocateBlock() throws BlockAddressOutOfBoundException, IOException, DiskFullException
    {
        // -1 because we have a pos++ at first argument in the do-while-loop
        int pos = (this.lastSmallestFreeBlockPosition / 8)-1;

        byte[] val = new byte[1];
        do
        {
            pos++;
            val[0] = fileManager.readByte(VUtil.getBlockOffset(this.blockAddress), pos);

            if (pos >= VUtil.BLOCK_SIZE)
            {
                throw new DiskFullException();
            }
        } while (val[0] == FULL_BYTE);

        final BitSet freeBlocks = BitSet.valueOf(val);

        int freeBitInByte = freeBlocks.nextClearBit(0);
        int freeBlockAddress = pos * 8 + freeBitInByte;

        this.lastSmallestFreeBlockPosition = freeBlockAddress;

        freeBlocks.set(freeBitInByte);
        fileManager.writeByte(VUtil.getBlockOffset(this.blockAddress), pos, freeBlocks.toByteArray()[0]);
        this.usedBlocks++;

        //awesome solution (but not sure if correct)
        //byte newByte = (byte) (val[0] | (freeBitInByte << USED_MASK));
        //fileManager.writeByte(VUtil.getBlockOffset(this.blockAddress), pos, newByte);

        return freeBlockAddress;
    }


    public void initialize() throws IOException
    {
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
        usedBlocks = 3;
        lastSmallestFreeBlockPosition = 3;
    }

    /**
     * Set a Block as unused
     *
     * @param blockAddress block blockAddress of the Block that should be set as unused
     */
    public void setUnused(int blockAddress) throws BlockAddressOutOfBoundException, IOException
    {
        if (isInvalidBlockAddress(blockAddress))
        {
            throw new BlockAddressOutOfBoundException();
        }

        int pos = blockAddress / 8;
        int bit = blockAddress % 8;

        final byte value = fileManager.readByte(VUtil.getBlockOffset(this.blockAddress), pos);
        final BitSet set = BitSet.valueOf(new byte[]{value});
        set.clear(bit);

        //since the bitset is empty, if the byte 0x00 is in it, we need this workaround
        byte newValue = 0;
        if(set.isEmpty()){
            newValue = 0;
        }else{
            newValue = set.toByteArray()[0];
        }

        if(blockAddress < lastSmallestFreeBlockPosition){
            this.lastSmallestFreeBlockPosition = blockAddress;
        }

        fileManager.writeByte(VUtil.getBlockOffset(this.blockAddress), pos, newValue);
        this.usedBlocks--;
    }

    /**
     * Set all Blocks as unused
     */
    public void reset() throws IOException
    {
        this.initialize();
    }

    /**
     * Check if a Block is unused
     *
     * @param blockAddress block blockAddress of the Block that should be checked
     * @return true if the Block is not used
     */
    public boolean isUnused(int blockAddress) throws BlockAddressOutOfBoundException, IOException
    {
        if (isInvalidBlockAddress(blockAddress))
        {
            throw new BlockAddressOutOfBoundException();
        }

        int pos = blockAddress / 8;
        int bit = blockAddress % 8;

        byte value = fileManager.readByte(VUtil.getBlockOffset(this.blockAddress), pos);

        return !BitSet.valueOf(new byte[]{value}).get(bit);
    }

    public int getFreeBlocks(){
        return (VUtil.BLOCK_SIZE*8)-usedBlocks;
    }

    public int getUsedBlocks(){
        return this.usedBlocks;
    }
}
