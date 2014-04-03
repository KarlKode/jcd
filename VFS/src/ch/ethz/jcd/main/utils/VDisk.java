package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.exceptions.*;
import ch.ethz.jcd.main.layer.VDirectory;
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
     * @param diskFile path to the VDisk file
     */
    public VDisk(File diskFile) throws FileNotFoundException
    {
        this.diskFile = diskFile;
        vUtil = new VUtil(diskFile);
    }

    /**
     * Create a new VDisk file that contains an almost empty VFS
     *
     * @param diskFile  path to the VDisk file
     * @param size      total size of the VDisk (in bytes). Has to be a multiple
     *                  of blockSize and have space for at least 16 blocks
     *                  (size >= blockSize * 16)
     */
    public static void format(File diskFile, long size) throws InvalidBlockAddressException, IOException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        VUtil.format(diskFile, size);
    }

    /**
     *
     */
    public void dispose()
    {
        if (!diskFile.delete())
        {
            // TODO: Throw correct exception
            throw new NotImplementedException();
        }
    }

    public VObject create(VDirectory destination, String name) throws BlockFullException, DiskFullException, InvalidBlockAddressException, InvalidNameException, IOException
    {
        DirectoryBlock block = vUtil.allocateDirectoryBlock();
        VDirectory directory = new VDirectory(block, destination);

        directory.clear(vUtil);
        directory.setName(name);
        directory.setParent(destination);
        destination.addEntry(directory);

        return directory;
    }
    /*
    public VFile createFile(VDirectory destination, String name)
    {
        // TODO
        throw new NotImplementedException();
    }*/

    /**
     * This method deletes the given VObject and its underlying
     * structure
     *
     * @param object to delete
     */
    public void delete(VObject object)
    {
        try
        {
            object.delete(vUtil);
        }
        catch (IOException e)
        {
            //TODO do sth
        }
    }


    /**
     * This method renames the given VObject
     *
     * //TODO think about only using move
     *
     * @param object to rename
     * @param name to set
     */
    public void rename(VObject object, String name)
    {
        try
        {
            object.setName(name);
        }
        catch (InvalidNameException e)
        {
            //TODO do sth
        }
        catch (IOException e)
        {
            //TODO do sth
        }
    }

    // Simple move of object into destination directory
    public void move(VObject object, VDirectory destination)
    {
        // TODO
        throw new NotImplementedException();
    }

    /**
     * This method copies the given VObject to the given destination without renaming
     * the VObject
     *
     * @param object to copy
     * @param destination where to copy
     */
    public void copy(VObject object, VDirectory destination)
    {
        this.copy(object, destination, null);
    }

    /**
     * This method copies the given VObject to the given destination with renaming
     * the VObject
     *
     * @param object to copy
     * @param destination where to copy
     * @param name of copied VObject
     */
    public void copy(VObject object, VDirectory destination, String name)
    {
        try
        {
            object.copy(vUtil, destination);

            if(name != null)
            {
                object.setName(name);
            }
        }
        catch (BlockFullException e)
        {
            //TODO do sth
        }
        catch (IOException e)
        {
            //TODO do sth
        }
        catch (InvalidBlockAddressException e)
        {
            //TODO do sth
        }
        catch (DiskFullException e)
        {
            //TODO do sth
        }
        catch (InvalidBlockSizeException e)
        {
            //TODO do sth
        }
        catch (InvalidNameException e)
        {
            //TODO do sth
        }
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

    /**
     * This methods resolves a given path and returns the VDirectory.
     *
     * @param path given
     * @return VDirectory to the given path
     * @throws FileNotFoundException if the given path could not be resolved
     */
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
}
