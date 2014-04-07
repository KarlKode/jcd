package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

import java.io.IOException;

public abstract class AbstractVFSCommand
{
    public abstract void execute(VFSConsole console, String[] args);

    public void usage( )
    {
        System.out.print("Usage: ");
        help();
    }

    public abstract void help( );

    protected VDirectory resolveDirectory(VFSConsole console, String path)
    {
        if(!path.startsWith(VDisk.PATH_SEPARATOR))
        {
            try
            {
                String pwd = console.getCurrent().getPath();
                path = pwd.endsWith(VDisk.PATH_SEPARATOR) ?  pwd + path : pwd + VDisk.PATH_SEPARATOR + path;
            }
            catch (IOException e)
            {
                return null;
            }
        }

        VObject destination = console.getVDisk().resolve(path);

        return (destination instanceof VDirectory) ? (VDirectory) destination : null;
    }

    protected VFile resolveFile(VFSConsole console, String path)
    {
        if(!path.startsWith(VDisk.PATH_SEPARATOR))
        {
            try
            {
                String pwd = console.getCurrent().getPath();
                path = pwd.endsWith(VDisk.PATH_SEPARATOR) ?  pwd + path : pwd + VDisk.PATH_SEPARATOR + path;
            }
            catch (IOException e)
            {
                return null;
            }
        }

        VObject destination = console.getVDisk().resolve(path);

        return (destination instanceof VFile) ? (VFile) destination : null;
    }
}
