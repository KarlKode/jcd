package ethz.jcd.main.blocks;

import ethz.jcd.main.visitor.BlockVisitor;

import java.nio.ByteBuffer;

public class Block
{
    protected byte[] bytes;

    protected ByteBuffer block;

    protected int address;

    public Block( ) { }


    public Block(int blockAddress)
    {
        address = blockAddress;
    }

    public Block(byte[] bytes)
    {
        this.bytes = bytes;

        block = ByteBuffer.wrap(bytes);
    }

    public Block(byte[] bytes, int blockAddress)
    {
        address = blockAddress;

        this.bytes = bytes;

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

    public byte[] getBytes( )
    {
        return bytes;
    }
}
