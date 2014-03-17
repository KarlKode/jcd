package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.visitor.VTypeVisitor;

import java.util.LinkedList;

/**
 * VDirectory represents a directory from the users view. It includes the path,
 * the name as well as the content.
 */
public class VDirectory extends VInode
{
    protected LinkedList<VInode> content = new LinkedList<VInode>();

    /**
     * Instantiate a new VDirectory by setting the directory's name and path.
     *
     * @param path to the directory
     */
    public VDirectory(String path)
    {
        super(path);
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
    @Override
    public <R, A> R accept(VTypeVisitor<R, A> visitor, A arg)
    {
        return visitor.directory(this, arg);
    }

    /**
     * This method adds either a file or directory to the directory. The path of
     * the added inode will be updated.
     *
     * @param type either VDirectory or VFile
     */
    public void add(VInode type)
    {
        LinkedList<String> newInodePath = new LinkedList<String>(this.path);
        newInodePath.add(type.getName());
        type.setPath(newInodePath);
        content.add(type);
    }

    /**
     * This method returns the directory's content
     *
     * @return a list of directories and files
     */
    public LinkedList<VInode> list()
    {
        return content;
    }
}
