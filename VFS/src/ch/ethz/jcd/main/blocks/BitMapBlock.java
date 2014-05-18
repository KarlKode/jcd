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
    private static final byte FULL_BYTE = (byte) 0xFF;

    private int usedBlocks;

    private int lastSmallestFreeBlockPosition;

    private int bitMapBlockCount;

    public BitMapBlock(FileManager fileManager, int blockAddress, long blockCount) throws InvalidBlockAddressException, IOException
    {
        super(fileManager, blockAddress);

        bitMapBlockCount = (int)Math.ceil((double)blockCount/(VUtil.BLOCK_SIZE*8));

        //init usedBlocks and lastSmallestFreeBlockPosition
        final byte[] content = fileManager.readBytes(VUtil.getBlockOffset(this.blockAddress), 0, bitMapBlockCount*VUtil.BLOCK_SIZE);
        final BitSet set = BitSet.valueOf(content);

        this.usedBlocks = set.cardinality();
        this.lastSmallestFreeBlockPosition = set.nextClearBit(0);
    }

    /**
     * Allocate a new Block out of the unused Blocks this BitMapBlock controls
     *
     * @return block blockAddress of the newly allocated Block
     */
    public int allocateBlock() throws BlockAddressOutOfBoundException, IOException, DiskFullException
    {
        // -1 because we have a pos++ at first argument in the do-while-loop
        int pos = (this.lastSmallestFreeBlockPosition / 8) - 1;

        byte[] val = new byte[1];
        do
        {
            pos++;
            val[0] = fileManager.readByte(VUtil.getBlockOffset(this.blockAddress), pos);

            if (pos >= bitMapBlockCount*VUtil.BLOCK_SIZE)
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

        BitSet firstBlockSet = new BitSet(VUtil.BLOCK_SIZE * bitMapBlockCount);
        int i = 0;

        //set Superblock as used
        firstBlockSet.set(i);
        i++;

        //set BitmapBlocks as used
        for(int j=0;j<bitMapBlockCount;i++,j++){
            firstBlockSet.set(i);
        }

        //set Rootblock
        firstBlockSet.set(i);
        i++;

        fileManager.writeBytes(VUtil.getBlockOffset(this.blockAddress), 0, firstBlockSet.toByteArray());
        usedBlocks = i;
        lastSmallestFreeBlockPosition = i;
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
        if (set.isEmpty())
        {
            newValue = 0;
        } else
        {
            newValue = set.toByteArray()[0];
        }

        if (blockAddress < lastSmallestFreeBlockPosition)
        {
            this.lastSmallestFreeBlockPosition = blockAddress;
        }

        fileManager.writeByte(VUtil.getBlockOffset(this.blockAddress), pos, newValue);
        this.usedBlocks--;
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

    public int getFreeBlocks()
    {
        return (bitMapBlockCount * VUtil.BLOCK_SIZE * 8) - usedBlocks;
    }

    public int getUsedBlocks()
    {
        return this.usedBlocks;
    }

    public int getBitMapBlockCount(){
        return bitMapBlockCount;
    }
}
