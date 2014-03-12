package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.visitor.BlockVisitor;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Block
{
    protected byte[] bytes;
    protected ByteBuffer block;
    protected int address;


    /**
     * Instantiate a new Block
     */
    public Block()
    {
    }

    /**
     * Instantiate a new Block with the given block address
     *
     * @param blockAddress the block address of the new block
     */
    public Block(int blockAddress)
    {
        address = blockAddress;
    }

    /**
     * Instantiate a new Block with the given content
     *
     * @param bytes the content of the new block
     */
    public Block(byte[] bytes)
    {
        this.bytes = bytes;

        block = ByteBuffer.wrap(bytes);
    }

    /**
     * Instantiate a new Block with the given block address and content
     *
     * @param blockAddress the block address of the new block
     * @param bytes        the content of the new block
     */
    public Block(int blockAddress, byte[] bytes)
    {
        address = blockAddress;

        setBytes(bytes);
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
     * Get the block address of the block
     *
     * @return block address of the block
     */
    public int getAddress()
    {
        return address;
    }

    /**
     * Set the block address of the block
     *
     * @param address new block address of the block
     */
    public void setAddress(int address)
    {
        this.address = address;
    }

    /**
     * Get the content of the block
     *
     * @return content of the block
     */
    public byte[] getBytes()
    {
        return bytes;
    }

    /**
     * Set the content of the block
     *
     * @param bytes new content of the block
     */
    public void setBytes(byte[] bytes)
    {
        this.bytes = bytes;
        if (bytes != null)
        {
            block = ByteBuffer.wrap(bytes);
        }
    }

    /**
     * Tests for equality: other is a Block object, content and address of other block are equal to this block
     *
     * @param other object to test equality against
     * @return true if tests hold
     */
    @Override
    public boolean equals(Object other)
    {
        return (other instanceof Block) && Arrays.equals(bytes, ((Block) other).bytes) && address == ((Block) other).address;
    }
}
