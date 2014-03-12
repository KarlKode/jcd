package ch.ethz.jcd.main.blocks;

import ch.ethz.jcd.main.visitor.BlockVisitor;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Block
{
    protected byte[] bytes;
    protected ByteBuffer block;
    protected int address;

    public Block()
    {
    }

    public Block(int blockAddress)
    {
        address = blockAddress;
    }

    public Block(byte[] bytes)
    {
        this.bytes = bytes;

        block = ByteBuffer.wrap(bytes);
    }

    public Block(int blockAddress, byte[] bytes)
    {
        address = blockAddress;

        this.bytes = bytes;

        //TODO bytes == null

        block = ByteBuffer.wrap(bytes);
    }

    public <R, A> R accept(BlockVisitor<R, A> visitor, A arg)
    {
        return visitor.visit(this, arg);
    }

    public int getAddress()
    {
        return address;
    }

    public void setAddress(int address)
    {
        this.address = address;
    }

    public byte[] getBytes()
    {
        return bytes;
    }

    @Override
    public boolean equals( Object o )
    {
        return (o instanceof Block) ? Arrays.equals(bytes, ((Block) o).bytes) && address == ((Block) o).address : false;
    }
}
