package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.utils.ByteArray;
import ch.ethz.jcd.main.visitor.BlockVisitor;

import java.util.Arrays;

/**
 * This class represents the general Block. It also holds the byte
 * structure that it could be easily written to or read from disk.
 */
public class Block
{
    protected ByteArray bytes;
    protected int address;

    /**
     * Instantiate a new Block with the given block address
     *
     * @param blockAddress the block address of the new Block
     */
    public Block(int blockAddress)
    {
        address = blockAddress;
    }

    /**
     * Instantiate a new Block with the given content
     *
     * @param b  the content of the new Block
     */
    public Block(byte[] b)
    {
        this.setBytes(b);
    }

    /**
     * Instantiate a new Block with the given block address and content
     *
     * @param blockAddress the block address of the new Block
     * @param bytes        the content of the new Block
     */
    public Block(int blockAddress, byte[] bytes)
    {
        address = blockAddress;
        this.setBytes(bytes);
    }

    public Block(Block block)
    {
        this.address = block.address;
        this.setByteArray(block.bytes);
    }

    /**
     * TODO: Detailed description
     *
     * @param visitor
     * @param arg
     * @param <R>
     * @param <A>
     * @return
     */
    public <R, A> R accept(BlockVisitor<R, A> visitor, A arg)
    {
        return visitor.visit(this, arg);
    }

    /**
     * Get the block address of the Block
     *
     * @return block address of the Block
     */
    public int getAddress()
    {
        return address;
    }

    /**
     * Set the block address of the Block
     *
     * @param address new block address of the block
     */
    public void setAddress(int address)
    {
        this.address = address;
    }

    /**
     * Get the content of the Block
     *
     * @return content of the Block
     */
    public byte[] getBytes()
    {
        return bytes.getBytes();
    }

    /**
     * Get the content of the Block
     *
     * @return content of the Block
     */
    public ByteArray getByteArray()
    {
        return bytes;
    }

    /**
     * Set the content of the Block
     *
     * @param b new content of the Block
     */
    public void setBytes(byte[] b)
    {
        this.bytes = new ByteArray(b);
    }

    /**
     * Set the content of the Block
     *
     * @param array new content of the Block
     */
    public void setByteArray(ByteArray array)
    {
        this.bytes = array;
    }

    /**
     * Tests for equality: other is a Block object, content and address of other Block are equal to this Block
     *
     * @param other object to test equality against
     * @return true if tests hold
     */
    @Override
    public boolean equals(Object other)
    {
        return (other instanceof Block) && Arrays.equals(bytes.getBytes(), ((Block) other).bytes.getBytes()) && address == ((Block) other).address;
    }
}
