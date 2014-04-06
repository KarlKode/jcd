package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.exceptions.*;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VFile.VFileImputStream;
import ch.ethz.jcd.main.layer.VFile.VFileOutputStream;
import ch.ethz.jcd.main.layer.VObject;

import java.io.*;
import java.util.HashMap;

/**
 * Public high level interface that hides the implementation details of all operations on a virtual disk.
 */
public class VDisk
{
    public static final String PATH_SEPARATOR = "/";

    private final File diskFile;
    private final VUtil vUtil;
    private VDirectory current;

    /**
     * Open an existing VDisk file that contains a valid VFS
     *
     * @param diskFile path to the VDisk file
     */
    public VDisk(File diskFile)
            throws FileNotFoundException
    {
        this.diskFile = diskFile;
        vUtil = new VUtil(diskFile);
        current = vUtil.getRootDirectory();
    }

    /**
     * Create a new VDisk file that contains an almost empty VFS
     *
     * @param diskFile path to the VDisk file
     * @param size     total size of the VDisk (in bytes). Has to be a multiple
     *                 of blockSize and have space for at least 16 blocks
     *                 (size >= blockSize * 16)
     */
    public static void format(File diskFile, long size)
            throws InvalidBlockAddressException, IOException, InvalidBlockCountException, VDiskCreationException, InvalidSizeException
    {
        VUtil.format(diskFile, size);
    }

    /**
     * This method deletes the virtual file system.
     */
    public void dispose()
    {
        if (!diskFile.delete())
        {
            //TODO do sth
        }
    }

    /**
     * This method lists the content of the current folder.
     *
     * @return list of the content as a HashMap using the object's name as key
     */
    public HashMap<String, VObject> list()
    {
        return list(current);
    }

    /**
     * This method lists the content of the given folder.
     *
     * @param destination given
     *
     * @return list of the content as a HashMap using the object's name as key
     */
    public HashMap<String, VObject> list(VDirectory destination)
    {
        HashMap<String, VObject> list = new HashMap<>();

        try
        {
            for (VObject object : destination.getEntries())
            {
                list.put(object.getName(), object);
            }
        }
        catch (IOException e)
        {
            // TODO do sth
        }

        return list;
    }

    /**
     * This method is used to go to the root of the virtual file system tree.
     */
    public void chdir()
    {
        current = vUtil.getRootDirectory();
    }

    /**
     * This method is used to navigate in the file system tree. Used to provide
     * the common operations like mkdir, touch, copy and move without specifying
     * the destination.
     *
     * @param path where to go
     */
    public void chdir(String path)
    {
        VDirectory current = this.resolve(path);

        if (current != null)
        {
            this.current = current;
        }
        else
        {
            chdir();
        }
    }

    /**
     * @return the current working directory
     */
    public VDirectory pwdir()
    {
        return current;
    }

    /**
     * This method creates a new directory at the current location.
     *
     * @param name to set
     *
     * @return the created directory
     */
    public VDirectory mkdir(String name)
    {
        return this.mkdir(current, name);
    }

    /**
     * This method creates a new directory at the given destination.
     *
     * @param destination where to create a new directory
     * @param name        to set
     *
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
     * This method creates a new file at the current location.
     *
     * @param name to set
     *
     * @return the created file
     */
    public VFile touch(String name)
    {
        return this.touch(current, name);
    }

    /**
     * This method creates a new file at the given destination.
     *
     * @param destination where to create a new file
     * @param name        to set
     *
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
     * This method renames the given VObject
     * <p/>
     * //TODO think about only using move
     *
     * @param object to rename
     * @param name   to set
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
     * This method first renames and the moves the given object and its
     * underlying structure to the current destination.
     *
     * @param object to move
     * @param name   to set
     */
    public void move(VObject object, String name)
    {
        this.move(object, current, name);
    }

    /**
     * This method moves the given object and its underlying structure to the
     * specified destination.
     *
     * @param object      to move
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
     * @param object      to move
     * @param destination where to move
     * @param name        to set
     */
    public void move(VObject object, VDirectory destination, String name)
    {
        this.rename(object, name);
        this.move(object, destination);
    }

    /**
     * This method copies the given VObject to the given destination without renaming
     * the VObject.
     *
     * @param object to copy
     * @param name   to set
     */
    public <T extends VObject> T copy(T object, String name)
    {
        return this.copy(object, current, name);
    }

    /**
     * This method copies the given VObject to the given destination without renaming
     * the VObject.
     *
     * @param object      to copy
     * @param destination where to copy
     */
    public <T extends VObject> T copy(T object, VDirectory destination)
    {
        try
        {
            return (T) object.copy(vUtil, destination);
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

        return null;
    }

    /**
     * This method copies the given VObject to the given destination with renaming
     * the VObject
     *
     * @param object      to copy
     * @param destination where to copy
     * @param name        of copied VObject
     */
    public <T extends VObject> T copy(T object, VDirectory destination, String name)
    {
        VObject copy = this.copy(object, destination);
        this.rename(copy, name);
        return (T) copy;
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
     * This method imports the given source file from the host file system into
     * this virtual file system using the current working directory as destination.
     *
     * @param source file to import
     */
    public VFile importFromHost(File source)
    {
        return importFromHost(source, current);
    }

    /**
     * This method imports the given source file from the host file system into
     * this virtual file system.
     *
     * @param source      file to import
     * @param destination directory where to import
     */
    public VFile importFromHost(File source, VDirectory destination)
    {
        VFile file = this.touch(destination, source.getName());

        try
        {
            FileInputStream stream = new FileInputStream(source);
            VFileImputStream vfile = file.inputStream(vUtil);

            long remaining = stream.available();

            while (0 < remaining)
            {
                int len = remaining < VUtil.BLOCK_SIZE ? (int) remaining : VUtil.BLOCK_SIZE;
                byte[] bytes = new byte[len];
                remaining -= stream.read(bytes);
                vfile.put(bytes);
            }

            stream.close();

            return file;
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
        return null;
    }

    /**
     * This method export the given object from this virtual file system into
     * the specified file on the host file system.
     *
     * @param source      object to export
     * @param destination file to write in
     */
    public void exportToHost(VFile source, File destination)
    {
        try
        {
            FileOutputStream stream = new FileOutputStream(destination);

            VFileOutputStream iterator = source.iterator();

            while (iterator.hasNext())
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

    /**
     * This method returns an object, that could be queried to get statistical
     * information about the choose virtual file system.
     *
     * @return instance of VStats object
     */
    public VStats stats()
    {
        return new VStats(vUtil);
    }

    /**
     * This methods resolves a given path and returns the VDirectory.
     *
     * @param path given
     *
     * @return VDirectory to the given path
     */
    public VDirectory resolve(String path)
    {
        VDirectory destination = vUtil.getRootDirectory();

        if (path.length() <= 0 || !path.startsWith(PATH_SEPARATOR) || !path.endsWith(PATH_SEPARATOR))
        {
            // TODO: Throw correct exception
            destination = null;
        }

        String[] directories = path.split(PATH_SEPARATOR);
        int i = 1;

        while (i < directories.length && destination != null)
        {
            try
            {
                VObject object = destination.getEntry(directories[i]);
                destination = object instanceof VDirectory ? (VDirectory) object : null;
            }
            catch (IOException e)
            {
                destination = null;
            }
            i++;
        }

        return destination;
    }
}
