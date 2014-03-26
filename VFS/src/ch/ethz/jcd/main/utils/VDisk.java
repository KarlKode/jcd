package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.exceptions.InvalidBlockSizeException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Public high level interface that hides the implementation details of all operations on a virtual disk.
 */
public class VDisk
{
    public static final String PATH_SEPARATOR = "/";
    private VUtil vUtil;

    /**
     * Open an existing VDisk file that contains a valid VFS
     *
     * @param vDiskFile path to the VDisk file
     */
    public VDisk(String vDiskFile) throws FileNotFoundException
    {
        vUtil = new VUtil(vDiskFile);
    }

    /**
     * Create a new VDisk file that contains an almost empty VFS
     *
     * @param vDiskFileName path to the VDisk file
     * @param size          total size of the VDisk (in bytes).
     *                      has to be a multiple of blockSize and have space for at least 16 blocks (size >= blockSize * 16)
     * @param blockSize     block size of the new VFS
     */
    public static void format(String vDiskFileName, long size, int blockSize) throws InvalidBlockSizeException, InvalidSizeException, VDiskCreationException, FileNotFoundException
    {
        VUtil.format(vDiskFileName, size, blockSize);
    }

    public void dispose()
    {
        // TODO
        throw new NotImplementedException();
    }

    // something like cd
    public VDirectory getDirectory(String path)
    {
        // TODO
        throw new NotImplementedException();
    }

    public List<VObject> listDirectory(VDirectory directory)
    {
        // TODO
        throw new NotImplementedException();
    }

    public VDirectory createDirectory(VDirectory destination, String name)
    {
        // TODO
        throw new NotImplementedException();
    }

    public VFile createFile(VDirectory destination, String name)
    {
        // TODO
        throw new NotImplementedException();
    }

    // rm (recursively if necessary)
    public void delete(VObject object)
    {
        // TODO
        throw new NotImplementedException();
    }

    // Simple rename (don't change directory hierarchy)
    public void rename(VObject object)
    {
        // TODO
        throw new NotImplementedException();
    }

    // Simple move of object into destination directory
    public void move(VObject object, VDirectory destination)
    {
        // TODO
        throw new NotImplementedException();
    }

    // Simple copy and rename of object into destination directory with new name
    public void copy(VObject object, VDirectory destination, String name)
    {
        // TODO
        throw new NotImplementedException();
    }

    public void importFromHost(File source, VDirectory destination)
    {
        // TODO
        throw new NotImplementedException();
    }

    public void exportToHost(VObject source, File destination)
    {
        // TODO
        throw new NotImplementedException();
    }

    public void stats()
    {
        // TODO
        throw new NotImplementedException();
    }
}
