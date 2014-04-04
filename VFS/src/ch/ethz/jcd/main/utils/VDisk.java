package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.DataBlock;
import ch.ethz.jcd.main.exceptions.*;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VFile.*;
import ch.ethz.jcd.main.layer.VObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;

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

    /**
     * This method creates a new directory at the given destination.
     *
     * @param destination where to create a new directory
     * @param name to set
     * @return the created directory
     */
    public VDirectory mkdir(VDirectory destination, String name)
    {
        try
        {
            // directory is created unlinked and then is named and linked
            VDirectory directory = new VDirectory(vUtil.allocateDirectoryBlock(), destination);
            directory.setName(name);
            directory.move(destination);
            return directory;
        }
        catch (DiskFullException e)
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
        catch (InvalidNameException e)
        {
            //TODO do sth
        }
        catch (BlockFullException e)
        {
            //TODO do sth
        }
        return null;
    }

    /**
     * This method creates a new file at the given destination.
     *
     * @param destination where to create a new file
     * @param name to set
     * @return the created file
     */
    public VFile touch(VDirectory destination, String name)
    {
        try
        {
            // file is created unlinked and then is named and linked
            VFile file = new VFile(vUtil.allocateFileBlock(), destination);
            file.setName(name);
            file.move(destination);
            return file;
        }
        catch (InvalidNameException e)
        {
            //TODO do sth
        }
        catch (IOException e)
        {
            //TODO do sth
        }
        catch (InvalidBlockAddressException e)
        {
            e.printStackTrace();
        }
        catch (DiskFullException e)
        {
            e.printStackTrace();
        }
        catch (BlockFullException e)
        {
            e.printStackTrace();
        }
        return null;
    }

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

    /**
     * This method moves the given object and its underlying structure to the
     * specified destination.
     *
     * @param object to move
     * @param destination where to move
     */
    public void move(VObject object, VDirectory destination)
    {
        try
        {
            object.move(destination);
        }
        catch (BlockFullException e)
        {
            //TODO do sth
        }
        catch (IOException e)
        {
            //TODO do sth
        }
    }

    /**
     * This method first renames and then moves the given object and its
     * underlying structure to the specified destination.
     *
     * @param object to move
     * @param destination where to move
     */
    public void move(VObject object, VDirectory destination, String name)
    {
        try
        {
            this.rename(object, name);
            object.move(destination);
        }
        catch (BlockFullException e)
        {
            //TODO do sth
        }
        catch (IOException e)
        {
            //TODO do sth
        }
    }

    /**
     * This method copies the given VObject to the given destination without renaming
     * the VObject.
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

    /**
     * This method imports the given source file from the host file system into
     * this virtual file system.
     *
     * @param source file to import
     * @param destination directory where to import
     */
    public void importFromHost(File source, VDirectory destination)
    {
        VFile file = this.touch(destination, source.getName());

        try
        {
            FileInputStream stream = new FileInputStream(source);
            VFileImputStream vfile = file.inputStream(vUtil);

            byte[] bytes = new byte[DataBlock.MAX_DATA_BLOCK_SIZE];

            while(stream.read(bytes) > 0)
            {
                vfile.put(bytes);
            }

            stream.close();
        }
        catch (IOException e)
        {
            //TODO do sth
        }
        catch (InvalidBlockSizeException e)
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
        catch (BlockFullException e)
        {
            //TODO do sth
        }
    }

    /**
     * This method export the given object from this virtual file system into
     * the specified file on the host file system.
     *
     * @param source object to export
     * @param destination file to write in
     */
    public void exportToHost(VFile source, File destination)
    {
        try
        {
            FileOutputStream stream = new FileOutputStream(destination);

            byte[] bytes = new byte[DataBlock.MAX_DATA_BLOCK_SIZE];

            VFileOutputStream iterator = source.iterator();

            while(iterator.hasNext())
            {
                stream.write(iterator.next().array());
            }

            stream.close();
        }
        catch (IOException e)
        {
            //TODO do sth
        }
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
                //TODO do sth
            }
        }

        return destination;
    }
}
