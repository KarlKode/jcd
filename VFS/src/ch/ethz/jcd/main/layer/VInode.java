package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.blocks.InodeBlock;
import ch.ethz.jcd.main.visitor.VTypeVisitor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Abstract representation of directories and files in a sense of "the users
 * view of an inode". Holds all information that belongs to both.
 */
public abstract class VInode
{
    protected String name;
    protected LinkedList<String> path = new LinkedList<>();
    protected InodeBlock inode;

    /**
     * Provides instantiation features that belongs to both, files and directories.
     *
     * @param path of given inode
     */
    public VInode(String path)
    {
        setPath(path);
    }

    /**
     * This method is part of the visitor pattern and is called by the visitor.
     * It tells to the visitor which sort of VInode he called.
     *
     * @param visitor calling this method
     * @param arg to pass
     * @param <R> generic return type
     * @param <A> generic argument type
     * @return the visitors return value
     */
    public abstract <R, A> R accept(VTypeVisitor<R, A> visitor, A arg);

    /**
     *
     *  @return the name of the inode
     */
    public String getName()
    {
        return name;
    }

    /**
     * This method sets the name of the inode.
     *
     * @param name of the inode
     */
    public void setName(String name)
    {
        this.name = name;
        this.path.removeLast();
        this.path.addLast(name);
    }

    /**
     * This method sets the path of the inode by splitting the path using "/"
     * as delimiter. If the given Path is the ROOT_DIRECTORY_BLOCK_PATH, the
     * list containing the path includes only the empty string. If the given
     * path ends with the delimiter itself, the last delimiter is removed.
     *
     * @param path of the inode
     */
    public void setPath(String path)
    {
        this.path.clear();
        if(path.equals(DirectoryBlock.ROOT_DIRECTORY_BLOCK_PATH))
        {
            this.path.add(DirectoryBlock.ROOT_DIRECTORY_BLOCK_NAME);
            this.name = DirectoryBlock.ROOT_DIRECTORY_BLOCK_NAME;
        }
        else
        {
            this.path.addAll(Arrays.asList(path.split("/")));
            if(this.path.getLast().equals(""))
            {
                this.path.removeLast();
            }
            this.name = this.path.getLast();
        }
    }

    /**
     * This method sets the path stored in a linke list
     *
     * @param path to set
     */
    public void setPath(LinkedList<String> path)
    {
        this.path.clear();
        this.path.addAll(path);
        this.name = this.path.getLast();
    }

    /**
     * This method returns the path as a queue to be able to do tree search
     *
     * @return path as a queue
     */
    public Queue<String> getPathQueue()
    {
        return path;
    }

    /**
     * This method returns the path as a queue to be able to do tree search
     *
     * @return path as a queue
     */
    public LinkedList<String> getPath()
    {
        return path;
    }

    /**
     * This method returns the hold InodeBlock
     *
     * @return the stored InodeBlock
     */
    public InodeBlock getInode()
    {
        return inode;
    }

    /**
     * This method sets the given InodeBlock.
     *
     * @param inodeBlock to set
     */
    public void setInode(InodeBlock inodeBlock)
    {
        this.inode = inodeBlock;
    }
}
