package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.exceptions.*;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Public high level interface that hides the implementation details of all operations on a virtual disk.
 */
public class VDisk
{
    public static final String PATH_SEPARATOR = "/";

    private final File diskFile;
    private final VUtil vUtil;

    /**
     * Open an existing VDisk file that contains a valid VFS
     *
     * @param vDiskFile path to the VDisk file
     */
    public VDisk(File diskFile) throws FileNotFoundException
    {
        this.diskFile = diskFile;
        vUtil = new VUtil(diskFile);
    }

    /**
     * Create a new VDisk file that contains an almost empty VFS
     *
     * @param vDiskFileName path to the VDisk file
     * @param size          total size of the VDisk (in bytes).
     *                      has to be a multiple of blockSize and have space for at least 16 blocks (size >= blockSize * 16)
     * @param blockSize     block size of the new VFS
     */
    public static void format(File diskFile, long size) throws InvalidBlockAddressException, IOException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        VUtil.format(diskFile, size);
    }

    public void dispose()
    {
        if (!diskFile.delete())
        {
            // TODO: Throw correct exception
            throw new NotImplementedException();
        }
    }

    public VDirectory getDirectory(String path) throws FileNotFoundException
    {
        if (path.length() <= 0 || !path.startsWith(PATH_SEPARATOR) || !path.endsWith(PATH_SEPARATOR))
        {
            // TODO: Throw correct exception
            throw new FileNotFoundException();
        }

        VDirectory destination = vUtil.getRootDirectory();

        String[] directories = path.split(PATH_SEPARATOR);

        for (int i = 1; i < directories.length; i++)
        {
            try
            {
                VObject object = destination.getEntry(directories[i]);
                if (!(object instanceof VDirectory))
                {
                    throw new FileNotFoundException();
                }
            } catch (IOException e)
            {
                // TODO
                e.printStackTrace();
            }
        }

        return destination;
    }

    public VDirectory createDirectory(VDirectory destination, String name) throws IOException, BlockFullException, DiskFullException, InvalidBlockAddressException, InvalidNameException
    {
        DirectoryBlock block = vUtil.allocateDirectoryBlock();
        VDirectory directory = new VDirectory(block, destination);

        directory.clear();
        directory.setName(name);
        directory.setParent(destination);

        return directory;
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
