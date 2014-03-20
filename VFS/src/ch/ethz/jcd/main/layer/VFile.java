package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.utils.ByteArray;
import ch.ethz.jcd.main.visitor.VTypeVisitor;

/**
 * VFile represents a file from the users view. It includes the path,
 * the name as well as the content.
 */
public class VFile extends VInode
{
    protected int size;
    protected ByteArray content;

    /**
     * Instantiate a new VDirectory by setting the directory's name and path.
     *
     * @param path to the directory
     */
    public VFile(String path)
    {
        super(path);
    }

    /**
     * This method is part of the visitor pattern and is called by the visitor.
     * It tells to the visitor which sort of VInode he called.
     *
     * @param visitor calling this method
     * @param arg     to pass
     * @param <R>     generic return type
     * @param <A>     generic argument type
     * @return the visitors return value
     */
    @Override
    public <R, A> R accept(VTypeVisitor<R, A> visitor, A arg)
    {
        return visitor.file(this, arg);
    }

    /**
     * This method returns the size of the file in bytes
     *
     * @return the size of the file
     */
    public int getSize()
    {
        return size;
    }

    /**
     * @return the file's content
     */
    public ByteArray getContent()
    {
        return content;
    }

    /**
     * @return the file's content as byte array
     */
    public byte[] getBytes()
    {
        return content.getBytes();
    }
}
