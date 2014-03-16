package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.Block;
import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.blocks.InodeBlock;
import ch.ethz.jcd.main.exceptions.InvalidNameException;
import ch.ethz.jcd.main.visitor.VTypeVisitor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public abstract class VType
{
    protected String name;

    protected LinkedList<String> path = new LinkedList<>();

    protected InodeBlock inode;

    public VType(String path)
    {
        setPath(path);
    }

    public abstract <R, A> R accept(VTypeVisitor<R, A> visitor, A arg);

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPath(String path)
    {
        if(path.equals(DirectoryBlock.ROOT_DIRECTORY_BLOCK_NAME+"/"))
        {
            this.path.add(DirectoryBlock.ROOT_DIRECTORY_BLOCK_NAME);
        }
        else
        {
            this.path.addAll(Arrays.asList(path.split("/")));
        }
        this.setName(this.path.getLast());
    }

    public Queue<String> getPath( )
    {
        return path;
    }

    public InodeBlock getInode()
    {
        return inode;
    }

    public void setInode(InodeBlock inodeBlock)
    {
        this.inode = inodeBlock;
    }
}
