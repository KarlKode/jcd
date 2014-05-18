package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.exceptions.FormatExcepion;
import ch.ethz.jcd.main.exceptions.NoSuchFileOrDirectoryException;
import ch.ethz.jcd.main.exceptions.command.*;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VFile.VFileInputStream;
import ch.ethz.jcd.main.layer.VFile.VFileOutputStream;
import ch.ethz.jcd.main.layer.VObject;

import java.io.*;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Public high level interface that hides the implementation details of all operations on a virtual disk.
 */
public class VDisk
{
    public static final String PATH_SEPARATOR = "/";

    private final File diskFile;
    private final VUtil vUtil;
    private VCompressor compressor;
    private boolean compressed;

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
        compressed = vUtil.isCompressed();
        compressor = new VCompressor();
    }

    /**
     * Create a new VDisk file that contains an almost empty VFS
     *
     * @param diskFile path to the VDisk file
     * @param size     total size of the VDisk (in bytes). Has to be a multiple
     *                 of blockSize and have space for at least 16 blocks
     *                 (size >= blockSize * 16)
     */
    public static void format(File diskFile, long size, boolean compressed)
    {
        int state = compressed ? 1 : 0;
        try
        {
            VUtil.format(diskFile, size, state);
        } catch (Exception e)
        {
            throw new FormatExcepion(e);
        }

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
     * This method lists the content of the given folder.
     *
     * @param destination given
     * @return list of the content as a HashMap using the object's name as key
     */
    public HashMap<String, VObject> list(VDirectory destination)
            throws ListException
    {
        HashMap<String, VObject> list = new HashMap<>();

        try
        {
            for (VObject object : destination.getEntries())
            {
                list.put(object.getName(), object);
            }
        } catch (IOException e)
        {
            throw new ListException(e);
        }

        return list;
    }

    /**
     * This method creates a new directory at the given destination.
     *
     * @param destination where to create a new directory
     * @param name        to set
     * @return the created directory
     */
    public VDirectory mkdir(VDirectory destination, String name)
            throws MkDirException
    {// directory is created unlinked and then is named and linked
        try
        {
            VDirectory directory = new VDirectory(vUtil.allocateDirectoryBlock(), destination);
            directory.setName(name);
            directory.move(destination);
            return directory;
        } catch (Exception ex)
        {
            throw new MkDirException(ex);
        }
    }

    /**
     * This method creates a new file at the given destination.
     *
     * @param destination where to create a new file
     * @param name        to set
     * @return the created file
     */
    public VFile touch(VDirectory destination, String name)
            throws TouchException
    {
        try
        {
            // file is created unlinked and then is named and linked
            VFile file = new VFile(vUtil.allocateFileBlock(), destination);
            file.setName(name);
            file.move(destination);
            return file;
        } catch (Exception ex)
        {
            throw new TouchException(ex);
        }
    }

    /**
     * This method renames the given VObject
     * <p>
     *
     * @param object to rename
     * @param name   to set
     */
    public void rename(VObject object, String name)
            throws RenameException
    {
        try
        {
            object.setName(name);
        } catch (Exception ex)
        {
            throw new RenameException(ex);
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
            throws MoveException
    {
        try
        {
            object.move(destination);
            if (name != null)
            {
                this.rename(object, name);
            }
        } catch (Exception ex)
        {
            throw new MoveException(ex);
        }
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
            throws CopyException
    {
        try
        {
            if (name == null)
            {
                name = object.getName();
            }
            T copy = (T) object.copy(vUtil, destination, name);
            return copy;
        } catch (Exception ex)
        {
            //TODO free block if sth got wrong
            throw new CopyException(ex);
        }
    }

    /**
     * This method deletes the given VObject and its underlying
     * structure
     *
     * @param object to delete
     */
    public void delete(VObject object)
            throws DeleteException
    {
        try
        {
            object.delete(vUtil);
        } catch (Exception e)
        {
            throw new DeleteException(e);
        }
    }

    /**
     * This method imports the given source file from the host file system into
     * this virtual file system.
     *
     * @param source      file to import
     * @param destination directory where to import
     */
    public VFile importFromHost(File source, VDirectory destination)
            throws ImportException
    {
        try
        {
            VFile file = this.touch(destination, source.getName());
            FileInputStream stream = new FileInputStream(source);
            VFileInputStream vfile = file.inputStream(vUtil, compressed);
            long remaining = stream.available();

            while (0 < (remaining = stream.available()))
            {
                int len = remaining < VUtil.BLOCK_SIZE ? (int) remaining : VUtil.BLOCK_SIZE;
                byte[] bytes = new byte[len];
                remaining -= stream.read(bytes);

                if (compressed)
                {
                    vfile.put(compressor.compress(bytes));
                } else
                {
                    vfile.put(bytes);
                }
            }

            stream.close();
            return file;
        } catch (Exception e)
        {
            throw new ImportException(e);
        }
    }

    /**
     * This method export the given object from this virtual file system into
     * the specified file on the host file system.
     *
     * @param source      object to export
     * @param destination file to write in
     */
    public void exportToHost(VFile source, File destination)
            throws ExportException
    {
        try
        {
            FileOutputStream stream = new FileOutputStream(destination);
            VFileOutputStream iterator = source.iterator(vUtil.isCompressed());

            while (iterator.hasNext())
            {
                if (compressed)
                {
                    stream.write(compressor.decompress(iterator.next().array()));
                } else
                {
                    stream.write(iterator.next().array());
                }
            }
            stream.close();
        } catch (Exception e)
        {
            throw new ExportException(e);
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
     * This methods resolves a given path and returns the VObject.
     *
     * @param path given
     * @return VObject to the given path
     */
    public VObject resolve(String path)
            throws ResolveException
    {
        try
        {
            if (path == null || path.length() <= 0 || !path.startsWith(PATH_SEPARATOR))
            {
                throw new NoSuchFileOrDirectoryException();
            }

            if (path.endsWith(PATH_SEPARATOR))
            {
                path = path.substring(0, path.length() - 1);
            }

            path = path.substring(path.indexOf(VDisk.PATH_SEPARATOR) + 1);
            VObject object = vUtil.getRootDirectory().resolve(path);

            if (object == null)
            {
                throw new NoSuchFileOrDirectoryException();
            }

            return object;
        } catch (Exception e)
        {
            throw new ResolveException(e);
        }
    }

    /**
     * Searches for files matching the given regular expression.
     *
     * @param regex     compiled regular expression Patttern
     * @param folder    where to start searching
     * @param recursive indicates whether including sub folders or not
     * @return HashMap filled with all search results
     */
    public HashMap<VObject, String> find(Pattern regex, VDirectory folder, boolean recursive)
            throws FindException
    {
        try
        {
            return folder.find(regex, recursive);
        } catch (Exception e)
        {
            throw new FindException(e);
        }
    }


    public boolean exists(String path) throws IOException
    {
        return vUtil.getRootDirectory().resolve(path) != null;

    }
}
