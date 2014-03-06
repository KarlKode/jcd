package ethz.jcd.main.blocks;

import ethz.jcd.main.visitor.BlockVisitor;

/**
 * Created by phgamper on 3/6/14.
 */
public class Block
{
    protected int address;

    public Block( ){}

    public Block(int blockAddress)
    {
        address = blockAddress;
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
}
