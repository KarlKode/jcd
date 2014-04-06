package ch.ethz.jcd.console;

import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class VFSCommands
{
    private VDisk vDisk;

    public VFSCommands(VDisk vDisk)
    {
        this.vDisk = vDisk;
    }

    public void vfsCreate(String[] arguments)
    {
        quitWithUsageIfLessThan(arguments, 2);

        File diskFile = new File(arguments[0]);
        long diskSize = Long.parseLong(arguments[1]);

        try
        {
            VDisk.format(diskFile, diskSize);
        } catch (InvalidBlockAddressException | InvalidSizeException | VDiskCreationException | InvalidBlockCountException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public void vfsDestroy(String[] arguments)
    {
        quitWithUsageIfLessThan(arguments, 1);

        // Simply remove the file
        String vDiskFilePath = arguments[0];
        File vDiskFile = new File(vDiskFilePath);
        String errorMessage = String.format("Could not destroy VDisk file at \"%s\".", vDiskFilePath);
        if (!vDiskFile.exists())
        {
            System.err.println(errorMessage + " VDisk file does not exist.");
            System.exit(1);
        }
        if (!vDiskFile.delete())
        {
            System.err.println(errorMessage);
            System.exit(1);
        }
        System.out.println(String.format("Destroyed VDisk file at \"%s\".", vDiskFilePath));
    }

    public void vfsLs(String[] arguments)
    {
        quitWithUsageIfLessThan(arguments, 2);

        File diskFile = new File(arguments[0]);
        String path = arguments[1];

        try
        {
            VDisk disk = new VDisk(diskFile);
            System.out.println(disk.resolve(path).getEntries());
            disk.mkdir(disk.resolve(path), "test");
            System.out.println(disk.resolve(path).getEntries());
            for (VObject b : disk.resolve(path).getEntries())
            {
                System.out.println(b.getName());
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void vfsGet(String[] arguments)
    {
        throw new NotImplementedException();
    }

    public void vfsPut(String[] arguments)
    {
        throw new NotImplementedException();
    }

    public void vfsRm(String[] arguments)
    {
        throw new NotImplementedException();
    }

    public void vfsCp(String[] arguments)
    {
        throw new NotImplementedException();
    }

    public void vfsImport(String[] arguments)
    {
        throw new NotImplementedException();
    }

    public void vfsExport(String[] arguments)
    {
        throw new NotImplementedException();
    }

    public void usage()
    {
        System.out.println("Usage: vdisk <command>[ arguments]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  help <command> - Get help for a certain command");
        System.out.println("  create <VDisk file> - Create a new VDisk at <file>");
        System.out.println("  destroy <VDisk file> - Destroy the existing VDisk at <file>");
        System.out.println("  ls <VDisk file> <VPath dir> - List contents of VDirectory at <dir>");
        System.out.println("  get <VDisk file> <VPath file> - Read contents of VFile at <file> and print it to stdout");
        System.out.println("  put <VDisk file> <VPath file> - Read from stdin into VFile <file>");
        System.out.println("  rm <VDisk file> <VPath file/dir> - Remove VFile or VDirectory at <file/dir>");
        System.out.println("  cp <VDisk file> <VPath src> <VPath dst> - Copy VFile or VDirectory from <src> to <dst>");
        System.out.println("  import <VDisk file> <Path src> <VPath dst> - Import file or directory from <src> to the VFile or VDirectory at <dst>");
        System.out.println("  export <VDisk file> <VPath src> <VPath dst> - Export the VFile VDirectory from <src> to file or directory at <dst>");
    }

    public void help(String[] arguments)
    {
        quitWithUsageIfLessThan(arguments);

        String command = arguments[0];

        switch (command)
        {
            case "help":
                System.out.println("Usage: vdisk help <command>");
                System.out.println("  Get help for a certain command");
                break;
            case "create":
                break;
            case "destroy":
                System.out.println("Usage: vdisk destroy <VDisk file>");
                System.out.println("  Destroy the VDisk file at <VDisk file>.");
                break;
            case "ls":
                break;
            case "get":
                break;
            case "put":
                break;
            case "rm":
                break;
            case "cp":
                break;
            case "import":
                break;
            case "export":
                break;
            default:
                System.out.println(String.format("\"%s\" is an unknown command.", command));
        }
    }

    public void quitWithUsageIfLessThan(String[] arguments)
    {
        quitWithUsageIfLessThan(arguments, 1);
    }

    public void quitWithUsageIfLessThan(String[] arguments, int minArgumentLength)
    {
        if (arguments.length < minArgumentLength)
        {
            usage();
            System.exit(1);
        }
    }
}
